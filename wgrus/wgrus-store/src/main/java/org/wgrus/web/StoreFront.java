package org.wgrus.web;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.wgrus.Order;

/**
 * Handles order requests.
 */
@Controller
@RequestMapping(value="/")
public class StoreFront {

	private final AtomicLong orderIdCounter = new AtomicLong(1);

	@Autowired @Qualifier("orderChannel")
	private MessageChannel orderChannel;

	@RequestMapping(method=RequestMethod.GET)
	public String displayForm() {
		return "order";
	}

	@RequestMapping(method=RequestMethod.POST)
	public String placeOrder(@RequestParam String email, @RequestParam int quantity, @RequestParam String productId, Model model) {
		long orderId = orderIdCounter.getAndIncrement();
		Order order = new Order(orderId);
		order.setEmail(email);
		order.setQuantity(quantity);
		order.setProductId(productId);
		MessagingTemplate template = new MessagingTemplate(this.orderChannel);
		template.convertAndSend(order);
		model.addAttribute("orderId", orderId);
		return "order";
	}

}
