package grails.plugin.boselecta

import grails.converters.JSON
import grails.plugin.boselecta.interfaces.ClientSessions

import javax.websocket.Session

class BoSelectaTagLib extends ConfService implements ClientSessions {

	static namespace  =  "bo"
	static returnObjectForTags = ['randomizeUser']
	def clientListenerService
	def autoCompleteService
	def randService

	def randomizeUser = { attrs ->
		String user = attrs.remove('user')?.toString()
		if (user) {
			out << randService.randomise(user)
		}
	} 
	
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

		// Reset the map - now held in userSession no need
		//clientListenerService.truncateStoredMap(oSession , job)

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
		}

		userTemplate = attrs.socketProcessTemplate ?: config.socketProcessTemplate ?: ''
		defaultTemplate = "/${VIEW}/socketProcess"

		if (userTemplate) {
			out << g.render(template:userTemplate, model: [job: job])
		}else{
			out << g.render(contextPath: pluginContextPath, template: defaultTemplate, model: [job: job])
		}

	}


	def selecta = {attrs ->
		def clazz = ""
		String job = attrs.remove('job')?.toString()
		String user = attrs.remove('user')?.toString()
		String id = attrs.remove('id')?.toString()
		String domain = attrs.remove('domain')?.toString()
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
		String nextValue = attrs.remove('nextValue')?.toString()
		String placeHolder = attrs.remove('placeHolder')?.toString()
		
		String max = attrs.remove('max')?.toString()
		String order = attrs.remove('order')?.toString()
		
		
		boolean norefPrimary = attrs.remove('norefPrimary')?.toBoolean() ?: false
		boolean autoComplete = attrs.remove('autoComplete')?.toBoolean() ?: false
		boolean autoCompletePrimary = attrs.remove('autoCompletePrimary')?.toBoolean() ?: false
		
		// Format can be set as JSON
		String formatting = attrs.remove('formatting').toString() ?: config.formatting ?: 'none'

		boolean require = attrs.remove('require')?.toBoolean() ?: false

		if (!id) {
			throwTagError("Tag [multiSelect] is missing required attribute [id]")
		}


		//if (!domain2) {
		//	throwTagError("Tag [multiSelect] is missing required attribute [domain2]")
		//}

		//if (!bindid) {
		//	throwTagError("Tag [multiSelect] is missing required attribute [bindid]")
		//}

		searchField2 = searchField2 ?: searchField
		if (!searchField2) {
			throwTagError("Tag [multiSelect] is missing required attribute [searchField]")
		}
		
		collectField2 = collectField2 ?: collectField
		if (!collectField2) {
			collectField2 = searchField2
		}
		
		if (!collectField) {
			collectField=collectField2
		}

		if (!searchField) {
			searchField=searchField2
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
		List primarylist = []

		if ((domain) && (bindid && (bindid.endsWith('.id'))||(norefPrimary))) {
			//primarylist = autoCompleteService.returnPrimaryList(domain, searchField, collectField)
			primarylist = autoCompleteService.returnPrimaryList(domain)
		}
		
		def multiDomainMap
		
		String dataList = "${id}-datalist"
		String sDataList = "${setId}-datalist"
		
		// AutoComplete box
		if (autoComplete) {
			genAutoComp(attrs.genAutoComplete, id,  placeHolder,  setId, collectField, searchField,
				 user, job, value, name, dataList, sDataList, autoCompletePrimary)
		}
		// Select Box
		else{
			
			def gsattrs=['optionKey' : "${collectField}" , 'optionValue': "${searchField}",
				'id': "${id}", 'value': "${value}", 'name': "${name}"]

			gsattrs['noSelection'] = attrs.noSelection
			gsattrs['from'] = primarylist

			if (requireField) {
				gsattrs['required'] = 'required'
			}

			// 	Front End JAVA Script actioned by socketProcess gsp template
			gsattrs['onchange'] = "javascript:actionThis(this.value, '${setId}', '${user}', '${job}');"

			// Parse taglib call for domain3..domainX and its setId's searchField and collectFields.....
			// Add to a map called multiDomainMap
			multiDomainMap = createDomainMap(attrs)

			out << g.select(gsattrs)
		}
		

		// Generate Message which is initial map containing default containing 
		// result set that then needs to be appended
		def message = [setId: setId, secondary: domain2, primaryCollect: collectField, collectfield: collectField2,	primarySearch: searchField, 
			searchField:  searchField2, appendValue: appendValue, appendName: appendName, job:job, formatting:formatting, nextValue:nextValue, 
			primary: domain, max:max, order:order, cId: id,	autoCompletePrimary:autoCompletePrimary, dataList:dataList, sDataList:sDataList]

		if (bindid) {
			message.put('bindId', bindid)
		}

		if (multiDomainMap) {
			message += multiDomainMap
		}

		def cc=message as JSON
		clientListenerService.sendJobMessage(job, cc as String)
		if (value||nextValue) {
			genDefinedValueScript(attrs.actionNonAppendThis,value,  setId, user, job)
		}
	}
	
	private void genAutoComp(String genAutoComplete, String id, String placeHolder, String setId, 
		String collectField, String searchField, String user, String job, String value,
		 String name, String dataList, String sDataList, boolean autoCompletePrimary) {
			// Moved to gsp template - so that you can override
			def userTemplate = genAutoComplete ?: config.genAutoComplete
			def defaultTemplate = "/${VIEW}/genAutoComplete"
			
			Map map = [value: value, setId:setId, user:user, job:job, name:name, dataList:dataList, searchField:searchField, 
				collectField: collectField, id:id, placeHolder:placeHolder, sDataList:sDataList]
			if (userTemplate) {
				out << g.render(template:userTemplate, model: map)
			}else{
				out << g.render(contextPath: pluginContextPath, template: defaultTemplate, model: map)
			}
	}
	
	private void genDefinedValueScript(String actionNonAppendThis, String value, String setId,String user, String job) {
		if (value) {
			// Moved to gsp template - so that you can override
			def userTemplate = actionNonAppendThis ?: config.actionNonAppendThis
			def defaultTemplate = "/${VIEW}/actionNonAppendThis"

			Map map = [value: value, setId:setId, user:user, job:job]
			if (userTemplate) {
				out << g.render(template:userTemplate, model: map)
			}else{
				out << g.render(contextPath: pluginContextPath, template: defaultTemplate, model: map)
			}

		}
	}
	
	private Map createDomainMap(attrs) {
		int a=3
		def multiDomainMap = [:]
		while (a < depth ) {
			String sId = attrs.remove('setId'+a)?.toString()
			String sf=attrs.remove('searchField'+a)?.toString()
			String cf=attrs.remove('collectField'+a)?.toString()
			String dom = attrs.remove('domain'+a)?.toString()
			String bindit = attrs.remove('bindid'+a)?.toString()
			if (sId && dom) {
				multiDomainMap+=[ "setId${a}": sId, "searchField${a}" : sf, "collectfield${a}": cf,
					"domain${a}": dom, "bindId${a}": bindit]
			}
			a++
		}
		return multiDomainMap
	}

}
