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
        compile ":spring-security-core:1.1.2"
        runtime ":blueprint:1.0.2",
                ":cloud-foundry:1.0",
                ":cloud-foundry-ui:1.0.1",
                ":executor:0.2",
                ":hibernate:2.0.0.BUILD-SNAPSHOT",
                ":mongodb:1.0.0.M6.1",
                ":redis:1.0.0.M6",
                ":searchable:0.6"
        build ":tomcat:2.0.0.BUILD-SNAPSHOT"
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.
    }
}
