package grails.plugin.boselecta

import grails.converters.JSON
import grails.plugin.boselecta.interfaces.ClientSessions

import javax.websocket.Session

class BoSelectaTagLib implements ClientSessions {

	static namespace  =  "boselecta"

	def grailsApplication

	def clientListenerService
	def autoCompleteService


	def connect  =  { attrs ->
		def job = attrs.remove('job')?.toString()
		def actionMap = attrs.remove('actionMap')
		def jsonData = attrs.remove('jsonData')
		def receivers = attrs.remove('receivers')
		boolean strictMode = attrs.remove('strictMode')?.toBoolean() ?: false
		boolean autodisco = attrs.remove('autodisco')?.toBoolean() ?: false
		boolean masterNode = attrs.remove('masterNode')?.toBoolean() ?: false

		String hostname = attrs.remove('hostname')?.toString()
		String appName = attrs.remove('appName')?.toString()
		String user = attrs.remove('user')?.toString()
		String message = attrs.remove('message')?.toString()
		String divId = attrs.remove('divId')?.toString() ?: ''
		String sendType = attrs.remove('sendType')?.toString() ?: 'message'
		String event =  attrs.remove('event')?.toString()
		String context = attrs.remove('context')?.toString()
		String addAppName = config.add.appName ?: 'yes'

		if (receivers) {
			receivers = receivers as ArrayList
		}
		
		if (jsonData) {
			if(jsonData instanceof String) {
				jsonData =JSON.parse(jsonData)
			}
			jsonData = jsonData as JSON
		}
		
		String frontuser=user+frontend
		if (receivers &&  (receivers instanceof ArrayList) ) {
			receivers.add(frontuser)
		}
		
		if (actionMap) {
			actionMap = actionMap as Map
		}
		
		if (!appName) {
			appName = grailsApplication.metadata['app.name']
		}
		
		if (!hostname) {
			hostname = config.hostname ?: 'localhost:8080'
		}
		if (!message) {
			message = "testing"
		}

		String uri="ws://${hostname}/${appName}/${APP}/"
		if (addAppName=="no") {
			uri="ws://${hostname}/${APP}/"
		}

		// Make a socket connection as actual main user (backend connection)
		Session oSession = clientListenerService.p_connect(uri, user, job)
		
		// Reset the map 
		// TODO - fix this - if multiple calls of this one page is to be an option.  
		clientListenerService.truncateStoredMap()
		
		Map model = [  message : message, job: job, hostname: hostname, actionMap: actionMap,
			appName: appName, frontuser:frontuser,  user: user,  receivers: receivers, divId: divId,
			chatApp: APP, addAppName: addAppName ]

		String userTemplate = attrs.socketConnectTemplate ?: config.socketConnectTemplate ?: ''
		String defaultTemplate= "/${VIEW}/socketConnect"

		// Now load up the socketProcess template - which does the front end connection
		if (userTemplate) {
			out << g.render(template:userTemplate, model:model)
		}else{
			out << g.render(contextPath: pluginContextPath, template: defaultTemplate, model: model)
		}
		if (sendType == 'message') {
			if (receivers) {
				clientListenerService.sendArrayPM(oSession, job, message)
			}else{
				clientListenerService.sendMessage( oSession,  message)
			}
		}
		if (autodisco) {
			clientListenerService.disconnect(oSession)
		}else{
		}

	}


	def selectPrimary = {attrs ->
		def clazz = ""
		String job = attrs.remove('job')?.toString()
		String user = attrs.remove('user')?.toString()
		String id = attrs.remove('id')?.toString()
		String domain = attrs.remove('domain')?.toString()
		//def noSelection = attrs.remove('noSelection')
		String domain2 = attrs.remove('domain2')?.toString()
		String bindid = attrs.remove('bindid')?.toString()
		String searchField = attrs.remove('searchField')?.toString()
		String searchField2 = attrs.remove('searchField2')?.toString()
		String collectField = attrs.remove('collectField')?.toString()
		String collectField2 = attrs.remove('collectField2')?.toString()
		String name = attrs.remove('name')?.toString()
		String setId = attrs.remove('setId')?.toString()
		String appendValue = attrs.remove('appendValue')?.toString()
		String appendName = attrs.remove('appendName')?.toString()
		String value = attrs.remove('value')?.toString()

		boolean require = attrs.remove('require')?.toBoolean() ?: false

		if (!id) {
			throwTagError("Tag [selectPrimary] is missing required attribute [id]")
		}
		
		if (!domain) {
			throwTagError("Tag [selectPrimary] is missing required attribute [domain]")
		}
		if (!attrs.noSelection) {
			throwTagError("Tag [selectPrimary] is missing required attribute [noSelection]")
		}
		
		if (!domain2) {
			throwTagError("Tag [selectPrimary] is missing required attribute [domain2]")
		}
		
		if (!bindid) {
			throwTagError("Tag [selectPrimary] is missing required attribute [bindid]")
		}
		
		if (!searchField) {
			throwTagError("Tag [selectPrimary] is missing required attribute [searchField]")
		}
		
		if (!collectField) {
			collectField = searchField
		}
		if (!collectField2) {
			collectField2=collectField
		}
		
		if (!searchField2) {
			searchField2=searchField
		}
		
		if (!setId) {
			setId = "selectPrimary"
		}
		
		if (attrs.class) {
			clazz = " class='${attrs.class}'"
		}
		
		if (!name) {
			name = id
		}
		
		if ((appendValue)&&(!appendName)) {
			appendName='Values Updated'
		}
		
		Boolean requireField=true
		if (require) {
			requireField=require
		}

		List primarylist = autoCompleteService.returnPrimaryList(domain)

		String userTemplate = attrs.socketProcessTemplate ?: config.socketProcessTemplate ?: ''
		String defaultTemplate = "/${VIEW}/socketProcess"

		if (userTemplate) {
			out << g.render(template:userTemplate, model: [attrs:attrs])
		}else{
			out << g.render(contextPath: pluginContextPath, template: defaultTemplate, model: [attrs:attrs])
		}

		def gsattrs=['optionKey' : "${collectField}" , 'optionValue': "${searchField}",
			'id': "${id}", 'value': "${value}", 'name': "${name}"]
		gsattrs['noSelection'] = attrs.noSelection
		gsattrs['from'] = primarylist

		if (requireField) {
			gsattrs['required'] = 'required'
		}

		
		

		// Front End JAVA Script actioned by socketProcess gsp template
		gsattrs['onchange'] = "javascript:actionThis(this.value, '${setId}', '${user}');"
		

		
		out << g.select(gsattrs)

		// Generate Message which is initial map containing default containing result set that then
		// needs to be appended
		def message = [setId: "${setId}", secondary: "${domain2}", collectfield: "${collectField2}",
			searchField:  "${searchField2}", appendValue: appendValue, appendName: appendName]
		if (bindid) {
			message.put('bindId', bindid)
		}
		def cc=message as JSON
		clientListenerService.sendJobMessage(job, cc as String)
	}



	def selectSecondary = {attrs ->
		def clazz,name = ""
		String user = attrs.remove('user')?.toString()
		String job = attrs.remove('job')?.toString()
		String bindid = attrs.remove('bindid')?.toString()

		String searchField = attrs.remove('searchField')?.toString()
		String searchField2 = attrs.remove('searchField2')?.toString()
		String collectField = attrs.remove('collectField')?.toString()
		String collectField2 = attrs.remove('collectField2')?.toString()
		String setId = attrs.remove('setId')?.toString()
		String domain2 = attrs.remove('domain2')?.toString()
		String appendName = attrs.remove('appendName')?.toString()
		String value = attrs.remove('value')?.toString()
		String appendValue = attrs.remove('appendValue')?.toString()

		boolean require = attrs.remove('require')?.toBoolean() ?: false
		if (!searchField2) {
			throwTagError("Tag [selectPrimary] is missing required attribute [searchField2]")
		}

		if (!collectField2) {
			collectField2=searchField2
		}
		
		if (!collectField) {
			collectField = collectField2
		}
		
		if (!searchField) {
			searchField=searchField2
		}
		
		if (!attrs.id) {
			throwTagError("Tag [selectScondary] is missing required attribute [id]")
		}
		
		if (!attrs.controller)  {
			attrs.controller= "autoComplete"
		}
		
		if (!attrs.action) {
			attrs.action= "ajaxSelectSecondary"
		}
		
		if (!attrs.noSelection) {
			throwTagError("Tag [selectScondary] is missing required attribute [noSelection]")
		}
		
		if (!setId) {
			setId = "selectSecondary"
		}
		
		if (attrs.class) {
			clazz = " class='${attrs.class}'"
		}
		
		if (attrs.name) {
			name = "${attrs.name}"
		} else {
			name = "${attrs.id}"
		}
		
		if ((appendValue)&&(!appendName)) {
			appendName='Values Updated'
		}
		
		Boolean requireField=true

		if (require) {
			requireField=require
		}

		List secondarylist=[]


		def gsattrs=['optionKey' : "${collectField}" , 'optionValue': "${searchField}",
			'id': "${attrs.id}", 'value': "${attrs.value}", 'name': "${name}"]
		gsattrs['noSelection'] = attrs.noSelection
		gsattrs['from'] = secondarylist
		if (requireField) {
			gsattrs['required'] = 'required'
		}
		

		// Front End JAVA Script actioned by socketProcess gsp template
		gsattrs['onchange'] = "javascript:actionThis(this.value, '${setId}', '${user}');"


		out << g.select(gsattrs)

		// Generate Message which is initial map containing default containing result set that then
		// needs to be appended
		def message = [setId: "${setId}", secondary: "${domain2}", collectfield: "${collectField2}",
			searchField:  "${searchField2}", appendValue: appendValue, appendName: appendName]
		if (bindid) {
			message.put('bindId', bindid)
		}
		def cc=message as JSON
		clientListenerService.sendJobMessage(job, cc as String)
	}

	private String getFrontend() {
		def cuser=config.frontenduser ?: '_frontend'
		return cuser
	}

	private getConfig() {
		grailsApplication?.config?.boselecta
	}

}
