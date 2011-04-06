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
package org.wgrus;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Dave Syer
 * 
 */
public class ServletConfigurationTests {
	
	@BeforeClass
	public static void init() {
		System.setProperty("spring.profiles.default", "default");
	}

	@AfterClass
	public static void close() {
		System.clearProperty("spring.profiles.default");
	}

	@Test
	public void testContext() throws Exception {

		ClassPathXmlApplicationContext parent = new ClassPathXmlApplicationContext(
				"classpath:/root-context.xml");

		try {

			ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
					new String[] { "classpath:/servlet-context.xml" }, parent);

			try {

			} finally {
				context.close();
			}

		} finally {

			parent.close();

		}

	}

}
