package org.springframework.data.mongodb.examples.custsvc.web;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.examples.custsvc.data.CustomerRepository;
import org.springframework.data.mongodb.examples.custsvc.domain.Customer;
import org.springframework.jdbc.support.DatabaseMetaDataCallback;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Handles requests for the application home page.
 */
@Controller
public class CustSvcController {

	private static final Logger logger = LoggerFactory.getLogger(CustSvcController.class);

	@Autowired
	CustomerRepository customerRepository;
	
	@Autowired
	DataSource dataSource;

	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value={"/", "/index"}, method=RequestMethod.GET)
	public String index() {
		logger.info("Welcome home!");
		return "index";
	}

	@RequestMapping(value = "/customer", method = RequestMethod.GET)
	public String list(Model model) {
		String dbInfo = "?";
		try {
			dbInfo = (String) JdbcUtils.extractDatabaseMetaData(dataSource,
				new DatabaseMetaDataCallback() {
					public Object processMetaData(DatabaseMetaData dbmd)
							throws SQLException, MetaDataAccessException {
						String info = dbmd.getDatabaseProductName() + " " + 
							dbmd.getDatabaseProductVersion();
						return info;
					}
				
				}	
			);
		} catch (MetaDataAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Customer> customers = customerRepository.findAll();
		model.addAttribute("dbinfo", dbInfo);
		model.addAttribute(customers);
		return "customer/list";
	}

}

