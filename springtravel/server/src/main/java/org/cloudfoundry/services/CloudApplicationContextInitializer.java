package org.cloudfoundry.services;

import org.cloudfoundry.runtime.env.CloudEnvironment;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class CloudApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		CloudEnvironment env = new CloudEnvironment();
		if (env.getInstanceInfo() != null) {
			System.out.println("cloud API: " + env.getCloudApiUri());
			applicationContext.getEnvironment().setActiveProfiles("cloud");
		}
		else {
			applicationContext.getEnvironment().setActiveProfiles("default");
		}
	}
}
