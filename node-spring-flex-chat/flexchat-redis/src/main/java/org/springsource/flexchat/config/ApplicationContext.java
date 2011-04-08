package org.springsource.flexchat.config;

import org.cloudfoundry.runtime.env.CloudEnvironment;
import org.cloudfoundry.runtime.service.keyvalue.RedisServiceCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.keyvalue.redis.connection.RedisConnectionFactory;
import org.springframework.data.keyvalue.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.util.CollectionUtils;

@Configuration
public class ApplicationContext {
	
	@Bean public RedisConnectionFactory redisConnectionFactory() {
		if (CollectionUtils.isEmpty(environment().getServices())) {
			return new JedisConnectionFactory();
		} else {
			return redisServiceCreator().createSingletonService().service;
		}
	}
	
	@Bean CloudEnvironment environment() {
		return new CloudEnvironment();
	}
	
	@Bean RedisServiceCreator redisServiceCreator() {
		return new RedisServiceCreator(environment());
	}
}