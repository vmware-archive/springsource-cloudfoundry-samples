package org.springframework.data.mongodb.examples.custsvc.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.examples.custsvc.data.CustomerRepository;
import org.springframework.data.mongodb.examples.custsvc.domain.Customer;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.support.SessionStatus;

@Controller
@RequestMapping(value = "/customer/new")
public class NewCustomerForm {

    private static final Logger logger = LoggerFactory.getLogger(NewCustomerForm.class);

    @Autowired
    CustomerRepository customerRepository;

    @RequestMapping(method = RequestMethod.GET)
    public String setupForm(Model model) {
        Customer customer = new Customer();
        model.addAttribute("customer", customer);
        return "customer/newCustomerForm";
    }

    @RequestMapping(method = RequestMethod.POST)
    @Transactional
    public String processSubmit(@ModelAttribute("customer") Customer customer, BindingResult result, SessionStatus status) {
        status.setComplete();
        logger.debug(customer.toString());
        customerRepository.save(customer);
        return "redirect:/customer";
    }

}
