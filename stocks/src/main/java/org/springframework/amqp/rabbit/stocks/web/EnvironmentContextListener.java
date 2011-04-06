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

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Dave Syer
 * 
 */
public class EnvironmentContextListener implements ServletContextListener {
	
	private static Log log = LogFactory.getLog(EnvironmentContextListener.class);

	public void contextInitialized(ServletContextEvent sce) {
		try {
		if (System.getenv("VMC_APP_VERSION")!=null) {
			String rabbit = System.getenv("VMC_RABBITMQ");
			if (rabbit!=null) {
				String[] split = rabbit.split(":");
				System.setProperty("RABBIT_HOST", split[0]);
				System.setProperty("RABBIT_PORT", split[1]);
			}
			String redis = System.getenv("VMC_REDIS");
			if (redis!=null) {
				System.setProperty("REDIS", "true");
				String[] split = redis.split(":");
				System.setProperty("REDIS_HOST", split[0]);
				System.setProperty("REDIS_PORT", split[1]);
			}
		}
		} catch (Exception e) {
			log.warn("Could not set up environment on startup", e);
		}
	}

	public void contextDestroyed(ServletContextEvent sce) {
	}

}
