package org.cloudfoundry.services;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	@Autowired(required=false) DataSource dataSource;
	@Autowired(required=false) RedisConnectionFactory redisConnectionFactory;
	@Autowired(required=false) MongoDbFactory mongoDbFactory;
	@Autowired(required=false) ConnectionFactory rabbitConnectionFactory;
	
	@Autowired(required=false) @Qualifier("cloudProperties") Properties cloudProperties;

	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value="/", method=RequestMethod.GET)
	public String home(Model model) {
		List<String> services = new ArrayList<String>();
		if (ClassUtils.isPresent("org.apache.tomcat.dbcp.dbcp.BasicDataSource", ClassUtils.getDefaultClassLoader())
				&& dataSource instanceof org.apache.tomcat.dbcp.dbcp.BasicDataSource) {
			services.add("Data Source: " + ((org.apache.tomcat.dbcp.dbcp.BasicDataSource) dataSource).getUrl());
		}
		else if (ClassUtils.isPresent("org.apache.commons.dbcp.BasicDataSource", ClassUtils.getDefaultClassLoader())
				&& dataSource instanceof org.apache.commons.dbcp.BasicDataSource) {
			services.add("Data Source: " + ((org.apache.commons.dbcp.BasicDataSource) dataSource).getUrl());
		}
		else if (dataSource instanceof SimpleDriverDataSource) {
			services.add("Data Source: " + ((SimpleDriverDataSource) dataSource).getUrl());
		}
		if (redisConnectionFactory != null) {
			services.add("Redis: " + ((JedisConnectionFactory) redisConnectionFactory).getHostName() + ":" + ((JedisConnectionFactory) redisConnectionFactory).getPort());
		}
		if (mongoDbFactory != null) {
			services.add("MongoDB: " + mongoDbFactory.getDb().getMongo().getAddress());
		}
		if (rabbitConnectionFactory != null) {
			services.add("RabbitMQ: " + rabbitConnectionFactory.getHost() + ":" + rabbitConnectionFactory.getPort());
		}
		model.addAttribute("services", services);
		String environmentName = (System.getenv("VCAP_APPLICATION") != null) ? "Cloud" : "Local";
		model.addAttribute("environmentName", environmentName);
		return "home";
	}

	@RequestMapping("/env")
	public void env(HttpServletResponse response) throws IOException {
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		out.println("System Properties:");
		for (Map.Entry<Object, Object> property : System.getProperties().entrySet()) {
			out.println(property.getKey() + ": " + property.getValue());
		}
		out.println();
		out.println("System Environment:");
		for (Map.Entry<String, String> envvar : System.getenv().entrySet()) {
			out.println(envvar.getKey() + ": " + envvar.getValue());
		}
		out.println();
		out.println("Cloud Properties:");
		if (cloudProperties != null) {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			List<String> keys = new ArrayList(cloudProperties.keySet());
			Collections.sort(keys);
			for (Object key : keys) {
				out.println(key + ": " + cloudProperties.get(key));
			}
		} else {
			out.println("Cloud properties not set");
		}
	}

}
