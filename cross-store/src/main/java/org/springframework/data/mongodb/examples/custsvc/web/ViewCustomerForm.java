package org.springframework.data.mongodb.examples.custsvc.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.examples.custsvc.data.CustomerRepository;
import org.springframework.data.mongodb.examples.custsvc.domain.Customer;
import org.springframework.data.mongodb.examples.custsvc.domain.Survey;
import org.springframework.data.mongodb.examples.custsvc.domain.SurveyInfo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.support.SessionStatus;

@Controller
@RequestMapping(value = "/customer/{id}")
public class ViewCustomerForm {

    @Autowired
    CustomerRepository customerRepository;

    @RequestMapping(method = RequestMethod.GET)
    public String setupForm(@PathVariable Long id, Model model) {
        Customer customer = customerRepository.findOne(id);
        Survey survey = new Survey();
        model.addAttribute("customer", customer);
        model.addAttribute("survey", survey);
        return "customer/viewCustomerForm";
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @Transactional
    public String processSubmit(@PathVariable Long id, @ModelAttribute("survey") Survey survey, BindingResult result, SessionStatus status) {
        status.setComplete();
        Customer customer = customerRepository.findOne(id);
        if (customer.getSurveyInfo() == null) {
            customer.setSurveyInfo(new SurveyInfo());
        }
        if (survey.getQuestion() != null && survey.getQuestion().trim().length() > 0) {
            customer.getSurveyInfo().addQuestionsAndAnswer(survey);
            customerRepository.save(customer);
        }
        return "redirect:/customer/" + id;
    }

}
