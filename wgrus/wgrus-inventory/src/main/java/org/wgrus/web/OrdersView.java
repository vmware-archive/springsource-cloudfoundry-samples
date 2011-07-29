package org.wgrus.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.wgrus.OrderQueue;

/**
 * Handles order requests.
 */
@Controller
@RequestMapping(value="/")
public class OrdersView {

	@Autowired
	private OrderQueue orderQueue;

	@RequestMapping(method=RequestMethod.GET)
	public String display(Model model) {
		model.addAttribute("count", orderQueue.count());
		model.addAttribute("orders", orderQueue.list());
		return "orders";
	}

}
