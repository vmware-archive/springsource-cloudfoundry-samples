package org.springframework.data.mongodb.examples.custsvc.web;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.CollectionCallback;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.examples.custsvc.data.CustomerRepository;
import org.springframework.data.mongodb.examples.custsvc.domain.Customer;
import org.springframework.data.mongodb.examples.custsvc.domain.SurveyInfo;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.DatabaseMetaDataCallback;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoException;

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

    @Autowired
    MongoTemplate mongoTemplate;

    @RequestMapping(value = { "/", "/index" }, method = RequestMethod.GET)
    public String index(Model model) {
        logger.info("Welcome home!");
        return "index";
    }

    @RequestMapping(value = { "/dump" }, method = RequestMethod.GET)
    public String dump(Model model) {
        String sqlInfo = "?";
        String sqlData = "";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        try {
            sqlInfo = (String) JdbcUtils.extractDatabaseMetaData(dataSource, new DatabaseMetaDataCallback() {
                public Object processMetaData(DatabaseMetaData dbmd) throws SQLException, MetaDataAccessException {
                    String info = dbmd.getDatabaseProductName() + " " + dbmd.getDatabaseProductVersion();
                    return info;
                }

            });
            List<Map<String, Object>> data = jdbcTemplate.queryForList("select * from customer");
            sqlData = data.toString();
        } catch (MetaDataAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        model.addAttribute("sqlinfo", sqlInfo);
        model.addAttribute("sqldata", sqlData);
        String mongoInfo = mongoTemplate.getDb().getMongo().debugString();
        final StringBuilder mongoData = new StringBuilder();
        mongoData.append("[");

        mongoTemplate.execute(Customer.class.getName(), new CollectionCallback<String>() {
            public String doInCollection(DBCollection collection) throws MongoException, DataAccessException {
                for (DBObject dbo : collection.find()) {
                    mongoData.append(mongoData.length() > 1 ? ", " : "");
                    mongoData.append(dbo.toString());
                }
                return null;
            }
        });
        mongoData.append("]");
        model.addAttribute("mongoinfo", mongoInfo);
        model.addAttribute("mongodata", mongoData);
        return "dump";
    }

    @RequestMapping(value = "/customer", method = RequestMethod.GET)
    public String list(Model model) {
        String dbInfo = "?";
        try {
            dbInfo = (String) JdbcUtils.extractDatabaseMetaData(dataSource, new DatabaseMetaDataCallback() {
                public Object processMetaData(DatabaseMetaData dbmd) throws SQLException, MetaDataAccessException {
                    String info = dbmd.getDatabaseProductName() + " " + dbmd.getDatabaseProductVersion();
                    return info;
                }

            });
        } catch (MetaDataAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        List<Customer> customers = customerRepository.findAll();
        model.addAttribute("dbinfo", dbInfo);
        model.addAttribute(customers);
        return "customer/list";
    }

    @RequestMapping(value = "/customerDelete/{id}")
    @Transactional
    public String delete(@PathVariable Long id) {
        Customer customer = customerRepository.findOne(id);
        if (customer != null) {
            customerRepository.delete(customer);
        }
        return "redirect:/customer";
    }

    @RequestMapping(value = "/customerClear/{id}")
    @Transactional
    public String clear(@PathVariable Long id) {
        Customer customer = customerRepository.findOne(id);
        if (customer != null) {
            customer.setSurveyInfo(new SurveyInfo());
            customerRepository.save(customer);
        }
        return "redirect:/customer/" + id;
    }
}
