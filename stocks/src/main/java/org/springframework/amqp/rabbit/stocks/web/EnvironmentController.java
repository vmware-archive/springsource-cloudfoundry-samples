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
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.stocks.context.RefreshScope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Dave Syer
 * 
 */
@Controller
public class EnvironmentController {

	private static Log logger = LogFactory.getLog(EnvironmentController.class);

	private RefreshScope refreshScope;

	private AmqpTemplate amqpTemplate;

	public void setAmqpTemplate(AmqpTemplate amqpTemplate) {
		this.amqpTemplate = amqpTemplate;
	}

	public void setRefreshScope(RefreshScope refreshScope) {
		this.refreshScope = refreshScope;
	}

	/**
	 * Get the system properties (key value "system") and the OS environment (key value "env") if available.
	 * 
	 * @return the system properties and OS environment
	 */
	@RequestMapping(value = "/env", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Properties> env() {
		Map<String, Properties> model = new HashMap<String, Properties>();
		model.put("system", getSystemProperties());
		model.put("env", getHostProperties());
		return model;
	}

	/**
	 * Use this endpoint to refresh the cron trigger that is used to send market data to the broker. It builds a refresh
	 * request and sends it to the broker using the AMQP template provided.
	 * 
	 * @param trigger a cron trigger expression, e.g. "0/10 * * * * *" is every 10 seconds
	 * @return a refresh request object (as information for the caller)
	 * 
	 * @see #handleRefresh(RefreshRequest) a handler for the request generated here
	 */
	@RequestMapping(value = "/refresh", method = RequestMethod.POST)
	@ResponseBody
	public RefreshRequest refresh(@RequestParam(defaultValue = "") String trigger) {
		RefreshRequest request = new RefreshRequest();
		Properties model = new Properties();
		if (trigger.length() > 0) {
			model.setProperty("quote.trigger", trigger);
		}
		request.setProperties(model);
		request.setBeanName("trigger");
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
		Properties properties = request.getProperties();
		for (String property : properties.stringPropertyNames()) {
			String old = System.getProperty(property);
			String update = properties.getProperty(property);
			if (update.length() > 0 && !update.equals(old)) {
				System.setProperty(property, update);
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
		private Properties properties = new Properties();

		public String getBeanName() {
			return beanName;
		}

		public void setBeanName(String beanName) {
			this.beanName = beanName;
		}

		public Properties getProperties() {
			return properties;
		}

		public void setProperties(Properties properties) {
			this.properties = properties;
		}

		@Override
		public String toString() {
			return "RefreshRequest [beanName=" + beanName + ", properties=" + properties + "]";
		}
	}
}
