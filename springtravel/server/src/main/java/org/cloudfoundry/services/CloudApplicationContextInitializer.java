package org.cloudfoundry.services;

import org.cloudfoundry.runtime.env.CloudEnvironment;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
public class CloudApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
	@Override
	public void initialize(ConfigurableApplicationContext context) {
	CloudEnvironment environment = new CloudEnvironment();
	context.getEnvironment().setActiveProfiles("default");
	}
}
