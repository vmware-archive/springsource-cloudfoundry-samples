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
import org.cloudfoundry.runtime.env.CloudEnvironment;

/**
 * @author Dave Syer
 * 
 */
public class EnvironmentContextListener implements ServletContextListener {
	
	private static Log log = LogFactory.getLog(EnvironmentContextListener.class);

	public void contextInitialized(ServletContextEvent sce) {
		CloudEnvironment environment = new CloudEnvironment();
		if (environment.getInstanceInfo()!=null) {
			log.info("VCAP_SERVICES: " + environment.getServiceInfos());
			System.setProperty("PLATFORM", "cloud");
		}
	}

	public void contextDestroyed(ServletContextEvent sce) {
	}

}
