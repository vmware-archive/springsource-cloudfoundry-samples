package org.cloudfoundry.services;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.document.mongodb.MongoTemplate;
import org.springframework.data.document.mongodb.query.Criteria;
import org.springframework.data.document.mongodb.query.Query;
import org.springframework.data.keyvalue.redis.connection.RedisConnectionFactory;
import org.springframework.data.keyvalue.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mongodb.Mongo;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	@Autowired(required=false) DataSource dataSource;
	@Autowired(required=false) RedisConnectionFactory redisConnectionFactory;
	@Autowired(required=false) Mongo mongo;
	@Autowired(required=false) MongoTemplate template;
	@Autowired(required=false) @Qualifier(value="serviceProperties") Properties serviceProperties;

	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value="/", method=RequestMethod.GET)
	public String home(Model model) {
		List<String> services = new ArrayList<String>();
		if (dataSource instanceof BasicDataSource) {
			services.add("Data Source: " + ((BasicDataSource) dataSource).getUrl());
		}
		else if (dataSource instanceof SimpleDriverDataSource) {
			services.add("Data Source: " + ((SimpleDriverDataSource) dataSource).getUrl());
		}
		if (redisConnectionFactory != null) {
			services.add("Redis: " + ((JedisConnectionFactory) redisConnectionFactory).getHostName() + ":" + ((JedisConnectionFactory) redisConnectionFactory).getPort());
		}
		if (mongo != null) {
			services.add("Mongo: " + mongo.getAddress());
		}
		
		
		Person p = new Person("Mark", 41);
	  template.save(p);
	  
	  List<Person> people = template.find(new Query(Criteria.where("firstName").is("Mark")), Person.class);
	  
	  services.add(people.toString());
	  
	  
		//Map envMap = System.getenv();
		for (Object key : serviceProperties.keySet())
    {
        services.add(key + " = " +  serviceProperties.get(key));
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
	}

}
