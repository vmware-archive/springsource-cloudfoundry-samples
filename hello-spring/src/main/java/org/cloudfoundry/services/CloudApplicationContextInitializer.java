package org.cloudfoundry.services;

import org.cloudfoundry.runtime.env.CloudEnvironment;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;

public class CloudApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		String cloudApiUri = null;
		try {
			CloudEnvironment env = new CloudEnvironment();
			cloudApiUri = env.getCloudApiUri();
		}
		catch (Exception e) {
			// ignore
		}
		System.out.println("cloud API: " + cloudApiUri);
		if (StringUtils.hasText(cloudApiUri)) {
			applicationContext.getEnvironment().setActiveProfiles("cloud");
		}
		else {
			applicationContext.getEnvironment().setActiveProfiles("default");
		}
	}

}
