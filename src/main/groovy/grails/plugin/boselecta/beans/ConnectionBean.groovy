package grails.plugin.boselecta.beans


import grails.plugin.boselecta.interfaces.ClientSessions
import grails.util.Holders
import grails.validation.Validateable

class ConnectionBean implements ClientSessions, Validateable {

	String user
	String job

	//Map actionMap
	//def jsonData
	//def receivers

	boolean autodisco = false

	String hostname = getConfig('hostname') ?: 'localhost:8080'
	String appName = grails.util.Metadata.current.applicationName ?: Holders.grailsApplication.metadata['app.name']
	String addAppName = getConfig('appName') ?: 'no'
	String chatApp = APP

	def getFrontuser() {
		String frontuser =  user+(getConfig('frontenduser') ?: '_frontend')
		return frontuser
	}

	/*
	def getReceivers()  {
		if (receivers) {
			receivers.add(frontuser)
		}
		return receivers
	}

	def getJsonData() {
		if (jsonData) {
			if(jsonData instanceof String) {
				jsonData = JSON.parse(jsonData)
			}
			jsonData = jsonData as JSON
		}
		return jsonData
	}
	*/

	def getUri() {
		String uri="ws://${hostname}/${appName}/${APP}/"
		if (addAppName=="no") {
			uri="ws://${hostname}/${APP}/"
		}
		return uri
	}

	static constraints = {
		//actionMap nullable: true
		//receivers nullable: true
		job (nullable: false, validator:validateInput)
		user(nullable: false, validator:validateInput)
		appName(nullable:true)
		addAppName(nullable:true)
	}

	static mapping = {
		addAppName defaultValue: 'no'
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