/*
 * Copyright 2006-2010 the original author or authors.
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

package org.springframework.amqp.rabbit.stocks.web;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.stocks.context.RefreshScope;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

/**
 * @author Dave Syer
 * 
 */
@Controller
public class EnvironmentController {

	private static Log logger = LogFactory.getLog(EnvironmentController.class);

	private RefreshScope refreshScope;

	private AmqpTemplate amqpTemplate;

	private RedisOperations<String, String> redisTemplate;

	private Properties environmentProperties = new Properties();

	public void setAmqpTemplate(AmqpTemplate amqpTemplate) {
		this.amqpTemplate = amqpTemplate;
	}

	public void setRefreshScope(RefreshScope refreshScope) {
		this.refreshScope = refreshScope;
	}

	public void setRedisTemplate(RedisOperations<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	/**
	 * @param properties the envProperties to set
	 */
	public void setEnvironmentProperties(Properties properties) {
		this.environmentProperties = properties;
	}

	/**
	 * Get the system properties (key value "system") and the OS environment (key value "host") if available and the
	 * persistent back-end (key value "env" if present).
	 * 
	 * @return the system properties and OS environment
	 */
	@RequestMapping(value = "/env", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Properties> env() {
		Map<String, Properties> model = new HashMap<String, Properties>();
		model.put("system", getSystemProperties());
		model.put("host", getHostProperties());
		model.put("env", environmentProperties);
		return model;
	}

	/**
	 * Use this endpoint to refresh the cron trigger that is used to send market data to the broker. It builds a refresh
	 * request and sends it to the broker using the AMQP template provided.
	 * 
	 * @param bean a bean id
	 * @return a refresh request object (as information for the caller)
	 * 
	 * @see #handleRefresh(RefreshRequest) a handler for the request generated here
	 */
	@RequestMapping(value = "/refresh/{bean}", method = RequestMethod.POST)
	@ResponseBody
	public RefreshRequest refresh(@PathVariable String bean, WebRequest webRequest) {
		RefreshRequest request = new RefreshRequest();
		Properties updates = new Properties();
		Properties oldValues = new Properties();
		for (Iterator<String> keys = webRequest.getParameterNames(); keys.hasNext();) {
			String key = keys.next();
			updates.setProperty(key, webRequest.getParameter(key));
			String value = environmentProperties.getProperty(key);
			if (value != null) {
				oldValues.setProperty(key, value);
			}
		}
		request.setUpdates(updates);
		request.setOldValues(oldValues);
		request.setBeanName(bean);
		if (amqpTemplate != null) {
			amqpTemplate.convertAndSend(request);
		}
		return request;
	}

	/**
	 * Set some system properties and refresh a bean in the application context (provided it is in refresh scope).
	 * 
	 * @param request a refresh request
	 */
	public void handleRefresh(RefreshRequest request) {

		logger.info("Handling refresh: " + request);

		String name = request.getBeanName();
		Properties updates = request.getUpdates();
		for (String property : updates.stringPropertyNames()) {
			String old = environmentProperties.getProperty(property);
			String update = updates.getProperty(property);
			if (update.length() > 0 && !update.equals(old)) {
				environmentProperties.setProperty(property, update);
				if (redisTemplate != null) {
					redisTemplate.boundValueOps("env." + property).set(update);
				}
			}
		}

		if (name != null && refreshScope != null) {
			refreshScope.refresh(name);
		}

	}

	private Properties getSystemProperties() {
		Properties env = new Properties();
		try {
			env.putAll(System.getProperties());
		} catch (Exception e) {
			logger.warn("Could not obtain System properties", e);
		}
		return env;
	}

	private Properties getHostProperties() {
		Properties env = new Properties();
		try {
			Map<String, String> values = System.getenv();
			for (String key : values.keySet()) {
				env.setProperty(key, values.get(key));
			}
		} catch (Exception e) {
			logger.warn("Could not obtain OS environment", e);
		}
		return env;
	}

	public static class RefreshRequest {
		private String beanName;
		private Properties updates = new Properties();

		private Properties oldValues = new Properties();

		public String getBeanName() {
			return beanName;
		}

		public void setBeanName(String beanName) {
			this.beanName = beanName;
		}

		public Properties getUpdates() {
			return updates;
		}

		public Properties getOldValues() {
			return oldValues;
		}

		public void setOldValues(Properties oldValues) {
			this.oldValues = oldValues;
		}

		public void setUpdates(Properties oldValues) {
			this.updates = oldValues;
		}

		@Override
		public String toString() {
			return "RefreshRequest [beanName=" + beanName + ", updates=" + updates + ", oldValues=" + oldValues + "]";
		}
	}
}
