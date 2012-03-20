import grails.plugin.cloudfoundry.AppCloudEnvironment
import org.springframework.amqp.rabbit.log4j.AmqpAppender

// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if(System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }

grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [ html: ['text/html','application/xhtml+xml'],
                      xml: ['text/xml', 'application/xml'],
                      text: 'text/plain',
                      js: 'text/javascript',
                      rss: 'application/rss+xml',
                      atom: 'application/atom+xml',
                      css: 'text/css',
                      csv: 'text/csv',
                      all: '*/*',
                      json: ['application/json','text/json'],
                      form: 'application/x-www-form-urlencoded',
                      multipartForm: 'multipart/form-data'
                    ]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// The default codec used to encode data with ${}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"
// enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = true
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// whether to install the java.util.logging bridge for sl4j. Disable for AppEngine!
grails.logging.jul.usebridge = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []

searchable {
    compassConnection = null
    compassSettings = [
            'compass.engine.analyzer.default.type': "Snowball",
            'compass.engine.analyzer.default.name': "English"]
    defaultExcludedProperties = ["password"]
    defaultFormats = [:]
    defaultMethodOptions = [
        search: [reload: false, escape: false, offset: 0, max: 10, defaultOperator: "and"],
        suggestQuery: [userFriendly: true]
    ]
    mirrorChanges = false
    bulkIndexOnStartup = false
}

rabbitmq {
    connectionfactory {
        username = 'guest'
        password = 'guest'
        hostname = 'localhost'
    }

    queues = {
        exchange name: 'search.sync', type: fanout, durable: false
    }
}

// log4j configuration
log4j = {
    // AMQP appender needs RabbitMQ connection settings. Pull from
    // VCAP_SERVICES.
    def rabbitConfig
    def cloudEnv = new AppCloudEnvironment()
    if (cloudEnv.available) {
        def serviceInfo = new AppCloudEnvironment().getServiceByVendor('rabbitmq')
        if (serviceInfo) {
        }

        rabbitConfig = [
                host: serviceInfo.host,
                username: serviceInfo.userName,
                password: serviceInfo.password,
                virtualHost: serviceInfo.virtualHost,
                port: serviceInfo.port]

    }
    else {
        def connFactory = owner.rabbitmq.connectionfactory
        rabbitConfig = [
                host: connFactory.hostname,
                username: connFactory.username,
                password: connFactory.password,
                virtualHost: connFactory.virtualHost]
    }

    appenders {
        rabbitConfig << [
                name: "rabbit",
                exchangeName: "amq.topic",
                exchangeType: "topic",
                routingKeyPattern: "logs.grailstwitter",
                contentEncoding: "UTF-8",
                applicationId: "grailstwitter" ]
        
        appender new AmqpAppender(rabbitConfig)
        console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    }

    root {
        info 'stdout', 'rabbit'
    }

    error  'org.codehaus.groovy.grails.web.servlet',  //  controllers
           'org.codehaus.groovy.grails.web.pages', //  GSP
           'org.codehaus.groovy.grails.web.sitemesh', //  layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping', // URL mapping
           'org.codehaus.groovy.grails.commons', // core / classloading
           'org.codehaus.groovy.grails.plugins', // plugins
           'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
           'org.springframework',
           'org.hibernate',
           'net.sf.ehcache.hibernate'

    warn   'org.mortbay.log'

    info   'grails.app'
    debug  'grails.app.controllers.org.grails.twitter',
           'grails.app.services.org.grails.twitter'
          
}

// Added by the Spring Security Core plugin:
grails.plugins.springsecurity.userLookup.userDomainClassName = 'org.grails.twitter.auth.Person'
grails.plugins.springsecurity.userLookup.authorityJoinClassName = 'org.grails.twitter.auth.PersonAuthority'
grails.plugins.springsecurity.authority.className = 'org.grails.twitter.auth.Authority'
