package org.springframework.samples.travel.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.samples.travel.domain.*;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;
import org.springframework.web.servlet.mvc.annotation.DefaultAnnotationHandlerMapping;
import org.springframework.web.servlet.view.xml.MarshallingView;

import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author Josh Long
 */
@Configuration
public class RestConfiguration {

	private Class[] jaxbClasses = { Hotels.class,Bookings.class, Amenity.class ,Booking.class, User.class, Hotel.class};

	@Value("classpath:/travel.xsd")
	private Resource schema;

	@Bean
	public Jaxb2Marshaller marshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setClassesToBeBound(this.jaxbClasses);
		return marshaller;
	}

	@Bean
	public MappingJacksonHttpMessageConverter mappingJacksonHttpMessageConverter() {
		MappingJacksonHttpMessageConverter mappingJacksonHttpMessageConverter = new MappingJacksonHttpMessageConverter();
		mappingJacksonHttpMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON));
		return mappingJacksonHttpMessageConverter;
	}

	@Bean
	public MarshallingHttpMessageConverter marshallingHttpMessageConverter() {
		MarshallingHttpMessageConverter converter = new MarshallingHttpMessageConverter(this.marshaller());
		converter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_XML));
		return converter;
	}

	public List<HttpMessageConverter<?>> httpMessageConverters() {
		List<HttpMessageConverter<?>> mcList = new ArrayList<HttpMessageConverter<?>>();
		mcList.add(this.marshallingHttpMessageConverter());
		mcList.add(this.mappingJacksonHttpMessageConverter());
		return mcList;
	}
	@Bean
	public MarshallingView view() {
		return new MarshallingView( marshaller());
	}

	/*@Bean
	public DefaultAnnotationHandlerMapping defaultAnnotationHandlerMapping() {
		return new DefaultAnnotationHandlerMapping();
	}*/

	@Bean
	public AnnotationMethodHandlerAdapter handlerAdapter() {

		List<HttpMessageConverter<?>> converters =  httpMessageConverters();

		AnnotationMethodHandlerAdapter annotationMethodHandlerAdapter = new AnnotationMethodHandlerAdapter();
		annotationMethodHandlerAdapter.setMessageConverters(converters.toArray(new HttpMessageConverter[converters.size()]));
		return annotationMethodHandlerAdapter;
	}

}
