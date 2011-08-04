package org.cloudfoundry.picalc.config;

import org.cloudfoundry.runtime.env.CloudEnvironment;
import org.cloudfoundry.runtime.service.messaging.RabbitServiceCreator;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public abstract class AbstractConfiguration {
	
	@Bean
	public ConnectionFactory connectionFactory() {
		CloudEnvironment environment = new CloudEnvironment();		
		if (environment.getInstanceInfo() != null) {
			return new RabbitServiceCreator(new CloudEnvironment()).createSingletonService().service;
		} else {
			CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost");
			connectionFactory.setUsername("guest");
			connectionFactory.setPassword("guest");
			return connectionFactory;
		}
	}
	
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JsonMessageConverter();
    }
	
	@Bean
	public AmqpAdmin amqpAdmin() {
		return new RabbitAdmin(connectionFactory());
	}
	
	@Bean
	public Queue workQueue() {
		// This queue will be declared due to the presence of the AmqpAdmin class in the context.
		// Every queue is bound to the default direct exchange		
		return new Queue(QueueNames.WORK_QUEUE_NAME);
	}
	
	@Bean
	public Queue resultQueue() {
		// This queue will be declared due to the presence of the AmqpAdmin class in the context.
		// Every queue is bound to the default direct exchange		
		return new Queue(QueueNames.RESULT_QUEUE_NAME);
	}
	
	@Bean 
	public DirectExchange piExchange() {
		return new DirectExchange("piExchange");
	}
	
	@Bean
	public Binding workerBinding() {
		return BindingBuilder.bind(workQueue()).to(piExchange()).with(QueueNames.WORK_QUEUE_NAME);
	}
	
	
	@Bean
	public Binding masterBinding() {
		return BindingBuilder.bind(resultQueue()).to(piExchange()).with(QueueNames.RESULT_QUEUE_NAME);
	}
	
	
}
