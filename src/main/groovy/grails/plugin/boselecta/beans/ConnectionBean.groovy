package grails.plugin.boselecta.beans

import grails.converters.JSON
import grails.plugin.boselecta.interfaces.ClientSessions
import grails.util.Holders

//@Validateable
class ConnectionBean implements ClientSessions {
	
	String user
	String job
	
	Map actionMap
	def jsonData
	def receivers
	 
	boolean autodisco = false
	
	String hostname = getConfig('hostname') ?: 'localhost:8080'
	String appName = grails.util.Metadata.current.applicationName ?: Holders.grailsApplication.metadata['app.name'] 
	String addAppName = getConfig('appName') ?: 'no'
	String chatApp = APP
	
	def getFrontuser() { 
		String frontuser =  user+(getConfig('frontenduser') ?: '_frontend')
		return frontuser
	}
	
	def getReceivers()  {
		if (receivers) {
			receivers.add(frontuser)
		}
	}
	
	def getJsonData() { 
		if (jsonData) {
			if(jsonData instanceof String) {
				jsonData = JSON.parse(jsonData)
			}
			jsonData = jsonData as JSON
		}
	}
	
	def getUri() { 
		String uri="ws://${hostname}/${appName}/${APP}/"
		if (addAppName=="no") {
			uri="ws://${hostname}/${APP}/"
		}
		return uri
	}
	
	static contstraints = {
		actionMap nullable: true
		receivers nullable: true
		job (nullable: false, validator:validateInput)
		user (nullable: false, validator:validateInput)
	}

	static def validateInput={value,object,errors->
		if (!value) {
			return errors.rejectValue(propertyName,"invalid.$propertyName",[''] as Object[],'')
		}
	}
	
	def getConfig(String configProperty) {
		Holders.config.boselecta[configProperty] ?: ''
	}
}
