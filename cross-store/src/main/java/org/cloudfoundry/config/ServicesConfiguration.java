package org.cloudfoundry.config;

import org.cloudfoundry.runtime.env.CloudEnvironment;
import org.cloudfoundry.runtime.env.MongoServiceInfo;
import org.cloudfoundry.runtime.env.RdbmsServiceInfo;
import org.cloudfoundry.runtime.service.document.MongoServiceCreator;
import org.cloudfoundry.runtime.service.relational.RdbmsServiceCreator;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoExceptionTranslator;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.crossstore.MongoChangeSetPersister;
import org.springframework.data.mongodb.crossstore.MongoDocumentBacking;
import org.springframework.data.mongodb.examples.custsvc.data.CrossStoreCustomerRepository;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@ComponentScan(basePackageClasses = CrossStoreCustomerRepository.class)
@EnableTransactionManagement(mode = AdviceMode.ASPECTJ)
public class ServicesConfiguration {

    @Bean
    public CloudEnvironment cloudEnvironment() {
        return new CloudEnvironment();
    }

    @Bean
    public MongoServiceInfo mongoServiceInfo() {
        MongoServiceInfo mongoServiceInfo = cloudEnvironment().getServiceInfos(    MongoServiceInfo.class).iterator().next();
        assert null != mongoServiceInfo : "there needs to be at least one MongoDB instance bound to this application" ;
        return mongoServiceInfo;
    }

    @Bean
    public MongoDbFactory mongoDbFactory() {
        MongoServiceCreator mongoServiceCreator = new MongoServiceCreator();
        return mongoServiceCreator.createService(mongoServiceInfo());
    }

    @Bean
    public DataSource dataSource() {
        RdbmsServiceInfo rdbmsServiceInfo = cloudEnvironment().getServiceInfos(  RdbmsServiceInfo.class).iterator().next();
        assert null != rdbmsServiceInfo : "there needs to be at least one MySQL RDBMS bound to this application";
        RdbmsServiceCreator rdbmsServiceCreator = new RdbmsServiceCreator();
        return rdbmsServiceCreator.createService(rdbmsServiceInfo);
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoDbFactory());
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setDatabase(Database.MYSQL);
        hibernateJpaVendorAdapter.setGenerateDdl(true);
        return hibernateJpaVendorAdapter;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean() {
        LocalContainerEntityManagerFactoryBean lcfb = new LocalContainerEntityManagerFactoryBean();
        lcfb.setJpaVendorAdapter(jpaVendorAdapter());
        lcfb.setDataSource(dataSource());
        return lcfb;
    }

    @Bean
    public PlatformTransactionManager platformTransactionManager() {
        JpaTransactionManager txMan = new JpaTransactionManager();
        txMan.setEntityManagerFactory(localContainerEntityManagerFactoryBean().getObject());
        return txMan;
    }

    @Bean
    public MongoDocumentBacking mongoDocumentBacking() {
        MongoDocumentBacking mdb = MongoDocumentBacking.aspectOf();
        mdb.setChangeSetPersister(changeSetPersister());
        return mdb;
    }

    @Bean
    public MongoChangeSetPersister changeSetPersister() {
        MongoChangeSetPersister mongoChangeSetPersister = new MongoChangeSetPersister();
        mongoChangeSetPersister.setEntityManagerFactory(localContainerEntityManagerFactoryBean().getObject());
        mongoChangeSetPersister.setMongoTemplate(mongoTemplate());
        return mongoChangeSetPersister;
    }

    @Bean
    public MongoExceptionTranslator mongoExceptionTranslator() {
        return new MongoExceptionTranslator();
    }

    @Bean
    public PersistenceAnnotationBeanPostProcessor persistenceAnnotationBeanPostProcessor() {
        return new PersistenceAnnotationBeanPostProcessor();
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor persistenceExceptionTranslationPostProcessor() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

}
