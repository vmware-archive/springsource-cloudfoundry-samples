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
        mavenLocal()
        grailsCentral()
    }
    plugins {
        compile ":spring-security-core:1.2.7.1"
        runtime ":blueprint:1.0.2",
                ":cloud-foundry:1.2.2.BUILD-SNAPSHOT",
                ":executor:0.3",
                ":hibernate:$grailsVersion",
                ":mongodb:1.0.0.RC3",
                ":redis:1.2",
                ":searchable:0.6.3", {
            excludes "grails-datastore-gorm", "grails-datastore-core"
        }
        build ":tomcat:$grailsVersion"
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.
        runtime "hsqldb:hsqldb:1.8.0.10"
    }
}
