import grails.plugin.boselecta.BoSelectaEndpoint


class BoselectaGrailsPlugin {
    def version = "0.1"
    def grailsVersion = "2.0 > *"
    def title = "BoSelecta: websocket multi-select/autoComplete domainClasses"
    def description = 'Default WebSocket used to query given domain classes with Unlimited depth of relationship + unlimited direct relationships. Either as dependent multi-select or autoComplete or maybe combination.'
    def documentation = "https://github.com/vahidhedayati/grails-boselecta-plugin"
    def license = "APACHE"
    def developers = [name: 'Vahid Hedayati', email: 'badvad@gmail.com']
    def issueManagement = [system: 'GITHUB', url: 'https://github.com/vahidhedayati/grails-boselecta-plugin/issues']
    def scm = [url: 'https://github.com/vahidhedayati/grails-boselecta-plugin']
	
	
	def doWithWebDescriptor = { xml ->
		def listenerNode = xml.'listener'
		listenerNode[listenerNode.size() - 1] + {
			listener {
				'listener-class'(BoSelectaEndpoint.name)
			}
		}
	}
	
}
