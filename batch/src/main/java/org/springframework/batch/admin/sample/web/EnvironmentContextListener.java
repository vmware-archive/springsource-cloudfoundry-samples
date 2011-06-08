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

package org.springframework.batch.admin.sample.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Dave Syer
 * 
 */
public class EnvironmentContextListener implements ServletContextListener {

	private static Log logger = LogFactory.getLog(EnvironmentContextListener.class);

	public void contextInitialized(ServletContextEvent sce) {

		logger.info("Attempting to detect cloud environment");

		if (initializeCloudEnvironment("VCAP_")) {
			logger.info("Found VCAP environment");
		} else {
			logger.info("No cloud environment");
		}

	}

	private boolean initializeCloudEnvironment(String prefix) {

		boolean detected = false;

		if (System.getenv(prefix + "APP_VERSION") != null) {
			System.setProperty("TOPOLOGY", "cluster");
			System.setProperty("DISCOVERY", "dynamic");
			detected = true;
		}

		if (!detected) {
			return false;
		}

		String mysql = System.getenv(prefix + "MYSQL");
		if (mysql != null) {
			try {
				JsonWrapper wrapper = new JsonWrapper(System.getenv(prefix + "SERVICES"));
				logger.info(prefix + "SERVICES: " + wrapper);
				System.setProperty("MYSQL_USER", wrapper.get("mysql.options.user", String.class));
				System.setProperty("MYSQL_PASSWORD", wrapper.get("mysql.options.password", String.class));
				System.setProperty("MYSQL_DATABASE", wrapper.get("mysql.options.name", String.class));
				String[] split = mysql.split(":");
				System.setProperty("MYSQL_HOST", split[0]);
				System.setProperty("MYSQL_PORT", split[1]);
				System.setProperty("ENVIRONMENT", "vmc");
			} catch (Exception e) {
				// Ignore it...
				logger.debug(prefix + "SERVICES not discovered ", e);
			}
		}

		return detected;

	}

	public void contextDestroyed(ServletContextEvent sce) {
	}

}
