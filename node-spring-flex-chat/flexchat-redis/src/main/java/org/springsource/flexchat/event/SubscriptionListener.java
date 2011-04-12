package org.springsource.flexchat.event;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.flex.messaging.AsyncMessageCreator;
import org.springframework.flex.messaging.MessageTemplate;
import org.springframework.flex.messaging.SubscribeEvent;
import org.springframework.flex.messaging.UnsubscribeEvent;
import org.springframework.flex.messaging.integration.FlexHeaders;

import flex.messaging.MessageBroker;
import flex.messaging.messages.AsyncMessage;
import flex.messaging.util.UUIDUtils;

public class SubscriptionListener implements ApplicationListener<ApplicationEvent> {

	private final MessageTemplate flexTemplate = new MessageTemplate();
	
	@Autowired
	public SubscriptionListener(MessageBroker broker) {
		flexTemplate.setMessageBroker(broker);
	}

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof SubscribeEvent) {
			SubscribeEvent subscribe = (SubscribeEvent) event;
			String messageClientId = subscribe.getClientId();
			Map<String, String> body = new HashMap<String, String>();
			body.put("userId", subscribe.getSource().toString());
			body.put("chatMessage", "I am connected.");
			flexTemplate.send(new EventMessageCreator(event.getSource().toString(), messageClientId, "si-chat-receiver", body));
		} else if (event instanceof UnsubscribeEvent) {
			UnsubscribeEvent unsubscribe = (UnsubscribeEvent) event;
			String messageClientId = unsubscribe.getClientId();
			Map<String, String> body = new HashMap<String, String>();
			body.put("userId", unsubscribe.getSource().toString());
			body.put("chatMessage", "I am disconnected.");
			flexTemplate.send(new EventMessageCreator(event.getSource().toString(), messageClientId, "si-chat-receiver", body));
		}
		
	}
	
	private static final class EventMessageCreator implements AsyncMessageCreator {
		
		private final String flexClientId;
		
		private final String messageClientId;
		
		private final String destinationId;
		
		private final Object body;
		
		public EventMessageCreator(String flexClientId, String messageClientId, String destinationId, Object body) {
			this.flexClientId = flexClientId;
			this.messageClientId = messageClientId;
			this.destinationId = destinationId;
			this.body = body;
		}
		
		@Override
		public AsyncMessage createMessage() {
			AsyncMessage message = new AsyncMessage();
			message.setHeader(FlexHeaders.FLEX_CLIENT_ID, flexClientId);
            message.setClientId(this.messageClientId);
            message.setDestination(this.destinationId);
            message.setMessageId(UUIDUtils.createUUID());
            message.setTimestamp(System.currentTimeMillis());
            message.setBody(body);
            return message;
		}
	}
}
