package grails.plugin.boselecta

import grails.converters.JSON
import grails.plugin.boselecta.interfaces.ClientSessions

import javax.websocket.Session

import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject

public class ClientProcessService extends ConfService implements ClientSessions {


	def clientListenerService
	def autoCompleteService


	public void processResponse(Session userSession, String message) {
		String username = userSession.userProperties.get("username") as String
		boolean disco = true
		if (message.startsWith("/pm")) {
			def values = parseInput("/pm ",message)
			String user = values.user as String
			String msg = values.msg as String
			if (user == username) {
				checkMessage(userSession, username, msg)
			}
		}else if (message.startsWith('{')) {
			JSONObject rmesg=JSON.parse(message)

			checkMessage(userSession, username, rmesg)

			String actionthis=''
			String msgFrom = rmesg.msgFrom
			boolean pm = false

			String disconnect = rmesg.system
			if (rmesg.privateMessage) {

				JSONObject rmesg2=JSON.parse(rmesg.privateMessage)
				checkMessage(userSession, username, rmesg2)
				String command = rmesg2.command
				if (command) {
					String event,context=''
					boolean strictMode, masterNode, autodisco, frontenduser = false
					JSONArray data
					JSONArray arguments = rmesg2.arguments as JSONArray
					arguments.each { args ->
						event = args.event
						context = args.context
						data = args.data
					}
					def jsonData = (data as JSON).toString()
					if ( (event == "open_session")  || (autodisco == false)){
						disco = false
					}
				}
			}
			if (disconnect && disconnect == "disconnect") {
				clientListenerService.disconnect(userSession)
			}
			if (msgFrom ) {
				actionthis = rmesg.privateMessage
				pm = true
			}

			def rmessage = rmesg.message
			if (rmessage) {
				def matcher = (rmessage =~ /(.*): (.*)/)
				if (matcher.matches()){
					msgFrom = matcher[0][1]
					if (msgFrom) {
						actionthis = matcher[0][2]
					}
				}
			}

			if (actionthis) {
				if (actionthis == 'close_connection') {
					clientListenerService.disconnect(userSession)
				}else if (actionthis == 'close_my_connection') {
					if (pm) {
						clientListenerService.sendPM(userSession, msgFrom,"close_connection")
					}
				}else{
					if (!msgFrom.endsWith(username)) {
						clientListenerService.sendBackEndPM(userSession, username,actionthis)
					}
					if (disco) {
						clientListenerService.disconnect(userSession)

					}
				}
			}
		}
	}

	def checkMessage(Session userSession, String username, JSONObject rmesg) {

		String secondary = rmesg.secondary
		String collectfield = rmesg.collectfield
		String searchField = rmesg.searchField
		String  setId = rmesg.setId
		String bindId = rmesg.bindId

		String updateValue = rmesg.updateValue
		String updateDiv = rmesg.updateDiv

		if (setId) {
			def myMap = [setId: setId,  secondary: secondary,collectfield:collectfield, searchField:searchField, bindId:bindId ]
			storedMap.add(myMap)
		}else if (updateValue) {
			Map currentSelection = [:]
			if (storedMap) {
				boolean go = false
				storedMap.each { s->
					go = false
					s.each {  k,v ->
						if (k == "setId" && v == updateDiv) {
							go = true
						}
						if (go) {
							if (k=="secondary") {
								secondary = v
							} else if (k=="collectfield") {
								collectfield = v
							}else if (k=="searchField") {
								searchField = v
							}else if (k=="bindId") {
								bindId = v
							}
						}
					}
					if (go) {
						ArrayList result = autoCompleteService.selectDomainClass(secondary, collectfield, searchField, bindId, updateValue )
						JSON mresult = ([ result: result, 'updateThisDiv': updateDiv]) as JSON
						clientListenerService.sendFrontEndPM(userSession, username,mresult as String)

					}
				}
			}
		}
	}

}

