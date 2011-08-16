package org.cloudfoundry.samples.rabbitmq.chat;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class ChatController {

	@Autowired
	private volatile AmqpTemplate amqpTemplate;

	private final Queue<String> messages = new LinkedBlockingQueue<String>();

	@RequestMapping(value = "/")
	public String home() {
		return "WEB-INF/views/chat.jsp";
	}

	@RequestMapping(value = "/publish", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void publish(@RequestParam String username, @RequestParam String text) {
		this.amqpTemplate.convertAndSend(username + ": " + text);
	}

	@RequestMapping(value = "/chatlog")
	@ResponseBody
	public String chatlog() {
		return StringUtils.arrayToDelimitedString(this.messages.toArray(), "<br/>");
	}

	/**
	 * This method is invoked when a RabbitMQ Message is received.
	 */
	public void handleMessage(String message) {
		if (messages.size() > 100) {
			messages.remove();
		}
		messages.add(message);
	}

}
