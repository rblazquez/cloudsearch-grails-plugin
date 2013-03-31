grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.project.dependency.resolution = {
	// inherit Grails' default dependencies
	inherits("global") {
	}
	log "error" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
	legacyResolve false // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility
	repositories {
        grailsPlugins()
        grailsHome()
		grailsCentral()
        mavenCentral()
	}
	dependencies {
		compile 'com.amazonaws:aws-java-sdk:1.4.1'
	}

	plugins {
		runtime ":hibernate:$grailsVersion"
		build(":tomcat:$grailsVersion",
				":release:2.2.0",
				":rest-client-builder:1.0.3") {
			export = false
		}
	}
}
