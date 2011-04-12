grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.project.dependency.resolution = {
    inherits "global"
    log      "warn"
    
    repositories {
        grailsPlugins()
        grailsHome()
        grailsCentral()
        mavenCentral()
    }
    plugins {
        compile ":redis:1.0.0.M4"
        runtime ":blueprint:1.0.2",
                ":quartz:0.4.2",
                ":resources:1.0-RC2",
                ":cached-resources:1.0-alpha6",
                ":zipped-resources:1.0-RC1",
                ":hibernate:1.3.7"
        build ":tomcat:1.3.7"
    }
    dependencies {
    }
}
