grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.project.dependency.resolution = {
    inherits "global", {
        excludes "xml-apis", "commons-digester"
    }
    log      "warn"
    repositories {
        grailsPlugins()
        grailsHome()
        grailsCentral()
    }
    plugins {
        compile ":spring-security-core:1.2.4"
        runtime ":blueprint:1.0.2",
                ":cloud-foundry:1.2.1",
                ":executor:0.3",
                ":hibernate:$grailsVersion",
                ":mongodb:1.0.0.RC1",
                ":redis:1.2",
                ":searchable:0.6.3", {
            excludes "slf4j-simple"
        }
        build ":tomcat:$grailsVersion"
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.
        runtime "hsqldb:hsqldb:1.8.0.10"
    }
}
