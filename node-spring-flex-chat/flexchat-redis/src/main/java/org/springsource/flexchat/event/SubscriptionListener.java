package org.springsource.flexchat.event;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.flex.messaging.SubscribeEvent;
import org.springframework.flex.messaging.UnsubscribeEvent;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionListener implements ApplicationListener<ApplicationEvent> {

	private final MessagingTemplate template = new MessagingTemplate();
	
	private MessageChannel flexChatReceiver;
	
	@Autowired 
	public void setFlexChatReceiver(MessageChannel flexChatReceiver) {
		this.flexChatReceiver = flexChatReceiver;
	}

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof SubscribeEvent) {
			Map<String, String> message = new HashMap<String, String>();
			message.put("userId", ((SubscribeEvent)event).getClientId());
			message.put("chatMessage", "I am connected.");
			template.convertAndSend(flexChatReceiver, message);
		} else if (event instanceof UnsubscribeEvent) {
			Map<String, String> message = new HashMap<String, String>();
			message.put("userId", ((UnsubscribeEvent)event).getClientId());
			message.put("chatMessage", "I am disconnected.");
			template.convertAndSend(flexChatReceiver, message);
		}	
	}
}
