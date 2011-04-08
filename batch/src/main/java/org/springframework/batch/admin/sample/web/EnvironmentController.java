/*
 * Copyright 2006-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.batch.admin.sample.web;

import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Dave Syer
 * 
 */
@Controller
public class EnvironmentController {
	
	private static Log logger = LogFactory.getLog(EnvironmentController.class);

	@RequestMapping(value = "/env", method = RequestMethod.GET)
	public void env(ModelMap model) {
		model.put("system", getSystemProperties());
		model.put("host", getHostProperties());
	}

	private Properties getSystemProperties() {
		Properties env = new Properties();
		try {
			env.putAll(System.getProperties());
		}
		catch (Exception e) {
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
		}
		catch (Exception e) {
			logger.warn("Could not obtain OS environment", e);
		}
		return env;
	}

}
