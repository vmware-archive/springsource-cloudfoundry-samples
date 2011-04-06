package org.springframework.samples.travel.services;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

/**
 *
 * Default local-environment configuration for {@link javax.sql.DataSource}
 *
 * @author Josh Long
 */
@Configuration
@Profile("default")
public class DefaultDataSourceConfiguration {

	private Log log = LogFactory.getLog(getClass()) ;

	@Value("classpath:/setup.sql")
	private Resource importSqlResource;

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

	@Bean(name = "mysqlDataSource")		// @Profile("default")
	public DataSource dataSource() {
		DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
		driverManagerDataSource.setUrl(this.url);
		driverManagerDataSource.setPassword(this.password);
		driverManagerDataSource.setUsername(this.user);
		driverManagerDataSource.setDriverClassName(this.driverClassName);
		log.debug( "creating 'default' mysqlDataSource");
		return driverManagerDataSource;
	}

}
