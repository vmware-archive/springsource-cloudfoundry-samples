/*
 * Copyright (c) 2011 by the original author(s).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cloudfoundry.samples.tickeranalysis;

import static org.springframework.data.document.mongodb.query.Criteria.*;
import static org.springframework.data.document.mongodb.query.Query.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.document.mongodb.MongoTemplate;
import org.springframework.data.keyvalue.redis.core.RedisTemplate;
import org.springframework.data.keyvalue.redis.listener.adapter.MessageListenerAdapter;

/**
 * @author Jon Brisbin <jbrisbin@vmware.com>
 */
public class SummaryService extends MessageListenerAdapter {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private RedisTemplate redisTemplate;
	@Autowired
	private MongoTemplate mongoTemplate;
	private ObjectMapper mapper = new ObjectMapper();

	public RedisTemplate getRedisTemplate() {
		return redisTemplate;
	}

	public void setRedisTemplate(RedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public MongoTemplate getMongoTemplate() {
		return mongoTemplate;
	}

	public void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	public void handleMessage(String json) throws IOException {
		if (null == mongoTemplate) {
			log.info("template is null!");
			return;
		}

		TickerEvent event = mapper.readValue(new StringReader(json), TickerEvent.class);
		if (log.isDebugEnabled()) {
			log.debug("got event: " + event);
		}
		Summary summ = getSummary(event.getSymbol());
		if (null == summ) {
			summ = new Summary(event.getSymbol(), System.currentTimeMillis());
		}
		summ.addTickerEvent(event);
		mongoTemplate.save(summ);
	}

	public List<Summary> getSummaries() {
		return mongoTemplate.find(query(where("timestamp").exists(true)), Summary.class);
	}

	public Summary getSummary(String symbol) {
		return mongoTemplate.findOne(query(where("_id").is(symbol)), Summary.class);
	}

}
