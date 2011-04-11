package org.springframework.samples.travel.services;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.cache.HashtableCacheProvider;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.MySQL5InnoDBDialect;
import org.hibernate.ejb.HibernatePersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple class that initializes all the services including data access logic
 */

@Configuration
public class ServicesConfiguration {

	private Log log = LogFactory.getLog(getClass()) ;

	private Class<? extends Dialect> dialect = MySQL5InnoDBDialect.class;

	@Value("${ds.url}")
	protected String url;

	@Value("${ds.password}")
	protected String password;

	@Value("${ds.driverClassName}")
	protected String driverClassName;

	@Value("${ds.user}")
	protected String user;

	@Value("${ds.name}")
	protected String database;

	@Autowired (required = false )
	DataSource dataSource ;




	public String getPersistenceXmlLocation() {
		return "classpath:/META-INF/persistence.xml";
	}

	@Bean
	public Map<String, Object> jpaProperties() {
		Map<String, Object> props = new HashMap<String, Object>();
		props.put("hibernate.dialect", this.dialect.getName());
//		props.put("hibernate.hbm2ddl.auto", "none");
		props.put("hibernate.show_sql", "true");
		props.put("hibernate.cache.provider_class", HashtableCacheProvider.class.getName());
		return props;
	}

	@Bean
	public JpaVendorAdapter jpaVendorAdapter() {
		HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
		hibernateJpaVendorAdapter.setShowSql(true);
		hibernateJpaVendorAdapter.setDatabase(Database.MYSQL);
		hibernateJpaVendorAdapter.setGenerateDdl(true);
		return hibernateJpaVendorAdapter;
	}

	@Bean
	public JpaTransactionManager transactionManager() {
		return new JpaTransactionManager(this.localContainerEntityManagerFactoryBean().getObject());
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean() {
		LocalContainerEntityManagerFactoryBean lef = new LocalContainerEntityManagerFactoryBean();
		lef.setDataSource(this.dataSource  );
		lef.setJpaVendorAdapter(this.jpaVendorAdapter());
		lef.setJpaPropertyMap(this.jpaProperties());
		lef.setPersistenceUnitName("travelDatabase");
		lef.setPersistenceProviderClass(HibernatePersistence.class);
		lef.setPersistenceXmlLocation(getPersistenceXmlLocation());
		return lef;
	}
}
