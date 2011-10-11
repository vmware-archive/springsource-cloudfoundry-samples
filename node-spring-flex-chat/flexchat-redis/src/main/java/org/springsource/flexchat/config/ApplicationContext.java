package org.springsource.flexchat.config;

import java.util.HashMap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.json.JsonInboundMessageMapper;
import org.springframework.integration.json.JsonOutboundMessageMapper;
import org.springframework.integration.support.converter.MessageConverter;
import org.springframework.integration.support.converter.SimpleMessageConverter;

@Configuration
public class ApplicationContext {
	
	@Bean MessageConverter jsonConverter() {
		SimpleMessageConverter converter = new SimpleMessageConverter();
		JsonInboundMessageMapper inbound = new JsonInboundMessageMapper(HashMap.class);
		JsonOutboundMessageMapper outbound = new JsonOutboundMessageMapper();
		converter.setInboundMessageMapper(inbound);
		converter.setOutboundMessageMapper(outbound);		
		return converter;
	}
}