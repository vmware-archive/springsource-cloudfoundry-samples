package org.springframework.data.mongodb.examples.custsvc.web;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cloudfoundry.runtime.env.CloudEnvironment;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;


public class CloudFoundryEnvironmentHandlerInterceptor extends HandlerInterceptorAdapter {

    @Inject
    CloudEnvironment cloudEnvironment;

    @PostConstruct
    public void setup() {
        Assert.notNull(this.cloudEnvironment, "the cloudEnvironment is not null");
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        String apiUrl = this.cloudEnvironment.getCloudApiUri(), host = this.cloudEnvironment.getInstanceInfo().getHost();
        int port = this.cloudEnvironment.getInstanceInfo().getPort();
        modelAndView.addObject("port", port);
        modelAndView.addObject("host", host);
        modelAndView.addObject("api", apiUrl);
        modelAndView.addObject("cloudEnvironment", this.cloudEnvironment);
        super.postHandle(request, response, handler, modelAndView);
    }
}
