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
package org.springframework.amqp.rabbit.stocks.web;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Dave Syer
 * 
 */
public class ServletConfigurationTests {
	
	@Before
	public void init() {
		System.setProperty("PLATFORM", "default");		
	}

	@After
	public void close() {
		System.clearProperty("PLATFORM");
	}

	@Test
	public void testContext() throws Exception {

		ClassPathXmlApplicationContext parent = new ClassPathXmlApplicationContext(
				"classpath:/server-bootstrap-config.xml");

		try {

			ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
					new String[] { "classpath:/servlet-config.xml" }, parent);

			try {

				EnvironmentController controller = context.getBean(EnvironmentController.class);
				assertNotNull(controller);
				Map<String, Properties> env = controller.env();
				assertTrue(env.containsKey("env"));

			}
			finally {
				context.close();
			}

		}
		finally {

			parent.close();

		}

	}

	@Test
	public void testRedisContext() throws Exception {

		System.setProperty("REDIS", "true");

		ClassPathXmlApplicationContext parent = new ClassPathXmlApplicationContext(
				"classpath:/server-bootstrap-config.xml");

		try {

			ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
					new String[] { "classpath:/servlet-config.xml" }, parent);

			try {

				EnvironmentController controller = context.getBean(EnvironmentController.class);
				assertNotNull(controller);
				Map<String, Properties> env = controller.env();
				assertTrue(env.containsKey("env"));

			}
			finally {
				context.close();
			}

		}
		finally {

			System.clearProperty("REDIS");
			parent.close();

		}

	}
}
