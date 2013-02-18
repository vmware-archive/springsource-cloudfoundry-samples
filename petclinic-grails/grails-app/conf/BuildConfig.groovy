grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir	= "target/test-reports"
grails.project.dependency.resolution = {
    inherits "global" // inherit Grails' default dependencies
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
        grailsHome()
        mavenCentral()	
        grailsCentral()
        mavenRepo "http://maven.springframework.org/milestone/"
    }

    dependencies {
        runtime "hsqldb:hsqldb:1.8.0.10", "postgresql:postgresql:9.1-901-1.jdbc4"
    }

    plugins {
        compile ":cloud-foundry:1.2.3",
                ":hibernate:$grailsVersion",
                ":webxml:1.4.1"
        build   ":tomcat:$grailsVersion"
    }
}
