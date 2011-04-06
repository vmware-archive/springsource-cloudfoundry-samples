package org.cloudfoundry.services;

import org.cloudfabric.runtime.env.CloudEnvironment;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author Mark Fisher
 */
public class CloudApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

	@Override
	public void initialize(ConfigurableApplicationContext context) {
		CloudEnvironment environment = new CloudEnvironment();
		if (environment.getHost() != null) {
			context.getEnvironment().setActiveProfiles("cloud");
		}
		else {
			context.getEnvironment().setActiveProfiles("default");
		}
	}

}
