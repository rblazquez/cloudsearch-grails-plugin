
import grails.util.Environment;

import org.grails.plugins.cloudsearch.CloudSearchEventListener
import org.springframework.context.ApplicationContext;
import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.codehaus.groovy.grails.orm.hibernate.HibernateEventListeners

import com.amazonaws.services.cloudsearch.AmazonCloudSearchClient;

class CloudsearchGrailsPlugin {
	// the plugin version
	def version = "0.1"
	// the version or versions of Grails the plugin is designed for
	def grailsVersion = "2.2 > *"
	// the other plugins this plugin depends on
	def dependsOn = [
		domainClass: "1.0 > *",
		hibernate: "1.0 > *"
	]
	def loadAfter = ['services']
	// resources that are excluded from plugin packaging
	def pluginExcludes = [
		"grails-app/views/error.gsp"
	]

	// TODO Fill in these fields
	def title = "AWS CloudSearch Plugin" // Headline display name of the plugin
	def author = "Danilo Tuler"
	def authorEmail = "danilo.tuler@ideais.com.br"
	def description = '''\
AWS CloudSearch integration.
'''

	// URL to the plugin's documentation
	def documentation = "http://grails.org/plugin/cloudsearch"

	// Extra (optional) plugin metadata

	// License: one of 'APACHE', 'GPL2', 'GPL3'
	def license = "APACHE"

	// Details of company behind the plugin (if there is one)
	def organization = [ name: "Ideais", url: "http://www.ideais.com.br/" ]

	// Any additional developers beyond the author specified above.
	//    def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]

	// Location of the plugin's issue tracker.
	//    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

	// Online location of the plugin's browseable source code.
	def scm = [url: "https://github.com/tuler/cloudsearch-grails-plugin"]

	def doWithWebDescriptor = { xml ->
		// TODO Implement additions to web.xml (optional), this event occurs before
	}

	def doWithSpring = {
        def csConfig = getConfiguration(parentCtx, application)
        cloudSearchContextHolder(CloudSearchContextHolder) {
            config = csConfig
        }

		// TODO: proper initialization		
        cloudSearchClient(AmazonCloudSearchClient)

		cloudsearchEventListener(CloudSearchEventListener) {
			cloudSearchContextHolder = ref("cloudSearchContextHolder")
		}

        searchableClassMappingConfigurator(SearchableClassMappingConfigurator) { bean ->
            cloudSearchContext = ref("cloudSearchContextHolder")
            grailsApplication = ref("grailsApplication")
            cloudSearchClient = ref("cloudSearchClient")
            config = csConfig

            bean.initMethod = 'configureAndInstallMappings'
        }
		
		if (!csConfig.disableAutoIndex) {
			// do not install audit listener if auto-indexing is disabled.
			hibernateEventListeners(HibernateEventListeners) {
				listenerMap = [
					'post-delete': cloudsearchEventListener,
					'post-collection-update': cloudsearchEventListener,
					'post-update': cloudsearchEventListener,
					'post-insert': cloudsearchEventListener,
					'flush': cloudsearchEventListener
				]
			}
		}
	}

	def doWithDynamicMethods = { ctx ->
		// TODO Implement registering dynamic methods to classes (optional)
	}

	def doWithApplicationContext = { applicationContext ->
		// TODO Implement post initialization spring config (optional)
	}

	def onChange = { event ->
		// TODO Implement code that is executed when any artefact that this plugin is
		// watching is modified and reloaded. The event contains: event.source,
		// event.application, event.manager, event.ctx, and event.plugin.
	}

	def onConfigChange = { event ->
		// TODO Implement code that is executed when the project configuration changes.
		// The event is the same as for 'onChange'.
	}

	def onShutdown = { event ->
		// TODO Implement code that is executed when the application shuts down (optional)
	}
	// Get a configuration instance

	private getConfiguration(ApplicationContext applicationContext, GrailsApplication application) {
		def config = application.config
		// try to load it from class file and merge into GrailsApplication#config
		// Config.groovy properties override the default one
		try {
			Class dataSourceClass = application.getClassLoader().loadClass("DefaultCloudSearch")
			ConfigSlurper configSlurper = new ConfigSlurper(Environment.current)
			Map binding = new HashMap()
			binding.userHome = System.properties['user.home']
			binding.grailsEnv = application.metadata["grails.env"]
			binding.appName = application.metadata["app.name"]
			binding.appVersion = application.metadata["app.version"]
			configSlurper.binding = binding
			def defaultConfig = configSlurper.parse(dataSourceClass)
			config = defaultConfig.merge(config)
			return config.cloudSearch
		} catch (ClassNotFoundException e) {
			LOG.debug("Not found: ${e.message}")
		}
		// try to get it from GrailsApplication#config
		if (config.containsKey("cloudSearch")) {
			if (!config.cloudSearch.date?.formats) {
				config.cloudSearch.date.formats = ["yyyy-MM-dd'T'HH:mm:ss'Z'"]
			}
			return config.cloudSearch
		}

		// No config found, add some default and obligatory properties
		ConfigSlurper configSlurper = new ConfigSlurper(Environment.current)
		config.merge(configSlurper.parse({
			cloudSearch {
				date.formats = ["yyyy-MM-dd'T'HH:mm:ss'Z'"]
			}
		}))

		return config
	}
}
