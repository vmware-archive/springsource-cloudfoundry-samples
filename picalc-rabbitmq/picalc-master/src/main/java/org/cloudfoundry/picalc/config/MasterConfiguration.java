package org.cloudfoundry.picalc.config;

import org.cloudfoundry.picalc.messaging.MasterHandler;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MasterConfiguration extends AbstractConfiguration {
	
	@Bean
	public RabbitTemplate rabbitTemplate() {
		RabbitTemplate template = new RabbitTemplate(connectionFactory());
		template.setMessageConverter(jsonMessageConverter());
		template.setRoutingKey(QueueNames.WORK_QUEUE_NAME);	
		return template;
	}
	
	@Bean
	public MasterHandler masterHandler() {
		return new MasterHandler();
	}
	

	@Bean
	public SimpleMessageListenerContainer listenerContainer() {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();		
		container.setConnectionFactory(connectionFactory());
		container.setQueueNames(QueueNames.RESULT_QUEUE_NAME);
		MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(masterHandler());
		messageListenerAdapter.setMessageConverter(jsonMessageConverter());
		container.setMessageListener(messageListenerAdapter);
		container.setConcurrentConsumers(10);
		return container;
	}
	
	/* comment in if you want also to have workers in the same web app
	@Bean
	public SimpleMessageListenerContainer workerListenerContainer() {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory());
		container.setQueueNames(QueueNames.WORK_QUEUE_NAME);
		MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(new WorkerHandler());
		messageListenerAdapter.setMessageConverter(jsonMessageConverter());
		container.setMessageListener(messageListenerAdapter);	
		container.setConcurrentConsumers(10);
		return container;
	}*/
	
}
