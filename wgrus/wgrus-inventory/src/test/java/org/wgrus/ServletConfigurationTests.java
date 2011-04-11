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

import static org.junit.Assert.assertEquals;

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

	@Test
	public void testUriDiscovery() throws Exception {
		String services = "{\"droplet_id\":1119,\"instance_id\":\"1b944bd60f7b3acacda84847a302b5bf\",\"instance_index\":0,\"name\":\"batch\",\"dir\":\"/var/vcap/data/dea/apps/batch-0-1b944bd60f7b3acacda84847a302b5bf\",\"uris\":[\"wgrus-inventory.cloudfoundry.com\"],\"users\":[\"dsyer@vmware.com\"],\"version\":\"336b66d32276651ba48605f0b4018d14e648a082-3\",\"mem_quota\":536870912,\"disk_quota\":2147483648,\"fds_quota\":256,\"state\":\"STARTING\",\"runtime\":\"java\",\"start\":\"2011-04-09 07:41:41 +0000\",\"state_timestamp\":1302334901,\"secure_user\":\"vcap-user-5\",\"resources_tracked\":true,\"port\":13036}";
		assertEquals("cloudfoundry.com", services.replaceAll(".*wgrus-inventory\\.([a-zA-Z0-9.]*)\\\".*", "$1"));

	}

}
