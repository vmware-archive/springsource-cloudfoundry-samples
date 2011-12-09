package org.cloudfoundry.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.examples.custsvc.web.CloudFoundryEnvironmentHandlerInterceptor;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
@EnableWebMvc
@Import(ServicesConfiguration.class)
@ComponentScan("org.springframework.data.mongodb.examples.custsvc.web")
public class WebConfiguration extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
    }

    @Bean
    public CloudFoundryEnvironmentHandlerInterceptor interceptor() {
        return new CloudFoundryEnvironmentHandlerInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this.interceptor());
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
         registry.addViewController("/").setViewName("index");
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
     configurer.enable();
    }

    @Bean
    public InternalResourceViewResolver internalResourceViewResolver() {
        InternalResourceViewResolver ir = new InternalResourceViewResolver();
        ir.setPrefix("/WEB-INF/views/");
        ir.setSuffix(".jsp");
        return ir;
    }
}
