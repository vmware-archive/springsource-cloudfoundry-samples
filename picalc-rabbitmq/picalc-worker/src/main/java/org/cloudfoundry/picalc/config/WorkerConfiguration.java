package org.cloudfoundry.picalc.config;

import org.cloudfoundry.picalc.messaging.WorkerHandler;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WorkerConfiguration extends AbstractConfiguration {

	
	@Bean
	public RabbitTemplate rabbitTemplate() {
		RabbitTemplate template = new RabbitTemplate(connectionFactory());
		template.setMessageConverter(jsonMessageConverter());
		template.setRoutingKey(QueueNames.RESULT_QUEUE_NAME);	
		return template;
	}
	
	@Bean
	public SimpleMessageListenerContainer workerListenerContainer() {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory());
		container.setQueueNames(QueueNames.WORK_QUEUE_NAME);
		MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(new WorkerHandler());
		messageListenerAdapter.setMessageConverter(jsonMessageConverter());
		container.setMessageListener(messageListenerAdapter);	
		container.setConcurrentConsumers(1);
		return container;
	}

}
