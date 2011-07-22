/*
 * Copyright 2002-2010 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.springframework.amqp.rabbit.stocks.context;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisOperations;

/**
 * @author Dave Syer
 * 
 */
public class RedisPropertiesFactoryBean implements FactoryBean<Properties> {

	private Log logger = LogFactory.getLog(RedisPropertiesFactoryBean.class);

	private RedisOperations<String, String> redisTemplate;
	private String key;
	private Properties merge;

	/**
	 * @param merge the merge to set
	 */
	public void setMerge(Properties merge) {
		this.merge = merge;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @param redisTemplate the redisTemplate to set
	 */
	public void setRedisTemplate(RedisOperations<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public Properties getObject() throws Exception {
		Properties properties = new Properties();
		try {
			BoundHashOperations<String, String, String> ops = redisTemplate.boundHashOps(this.key);
			Map<String, String> map = ops.entries();
			for (String key : map.keySet()) {
				String value = map.get(key);
				properties.setProperty(key, value);
				if (merge != null) {
					merge.setProperty(key, value);
				}
			}
		} catch (Exception e) {
			logger.error("Could not obtain Redis hash from " + key);
		}
		try {
			String pattern = this.key;
			if (!pattern.endsWith("*")) {
				pattern = pattern + "*";
			}
			Set<String> keys = redisTemplate.keys(pattern);
			for (String key : keys) {
				String value = redisTemplate.opsForValue().get(key);
				key = key.substring(pattern.lastIndexOf("*")+1);
				properties.setProperty(key, value);
				if (merge != null) {
					merge.setProperty(key, value);
				}
			}
		} catch (Exception e) {
			logger.error("Could not obtain Redis values from " + key);
		}
		return properties;
	}

	public Class<?> getObjectType() {
		return Properties.class;
	}

	public boolean isSingleton() {
		return true;
	}

}
