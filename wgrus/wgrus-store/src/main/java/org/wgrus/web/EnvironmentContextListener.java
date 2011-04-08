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

package org.wgrus.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dave Syer
 * 
 */
public class EnvironmentContextListener implements ServletContextListener {

	private static Logger logger = LoggerFactory.getLogger(EnvironmentContextListener.class);

	public void contextInitialized(ServletContextEvent sce) {
		if (System.getenv("VMC_APP_VERSION") != null || System.getenv("VCAP_APP_VERSION") != null) {
			System.setProperty("spring.profiles.active", "cloud");
			logger.info("Cloud profile activated");
		} else {
			System.setProperty("spring.profiles.active", "default");			
			logger.info("Default profile set");			
		}
	}

	public void contextDestroyed(ServletContextEvent sce) {
	}

}
