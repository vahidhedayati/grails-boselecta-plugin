package grails.plugin.boselecta

import grails.converters.JSON
import grails.plugin.boselecta.interfaces.ClientSessions

import javax.websocket.Session

import org.codehaus.groovy.grails.web.json.JSONObject

public class ClientProcessService extends ConfService implements ClientSessions {


	def clientListenerService
	def autoCompleteService

	private String 	domain3, collectfield3, searchField3, setId3, bindId3,
	domain4, collectfield4, searchField4, setId4, bindId4,
	domain5, collectfield5, searchField5, setId5, bindId5,
	domain6, collectfield6, searchField6, setId6, bindId6,
	domain7, collectfield7, searchField7, setId7, bindId7,
	domain8, collectfield8, searchField8, setId8, bindId8,
	domain9, collectfield9, searchField9, setId9, bindId9,
	domain10, collectfield10, searchField10, setId10, bindId10,
	domain11, collectfield11, searchField11, setId11, bindId11,
	domain12, collectfield12, searchField12, setId12, bindId12,
	domain13, collectfield13, searchField13, setId13, bindId13,
	domain14, collectfield14, searchField14, setId14, bindId14,
	domain15, collectfield15, searchField15, setId15, bindId15,
	domain16, collectfield16, searchField16, setId16, bindId16,
	domain17, collectfield17, searchField17, setId17, bindId17,
	domain18, collectfield18, searchField18, setId18, bindId18


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
		// Initial connection
		String secondary = rmesg.secondary
		String primary = rmesg.primary
		String collectfield = rmesg.collectfield
		String searchField = rmesg.searchField
		String  setId = rmesg.setId
		String formatting = rmesg.formatting
		String bindId = rmesg.bindId
		String appendValue = rmesg.appendValue ?: ''
		String appendName = rmesg.appendName ?: ''
		String jobName = rmesg.job



		//Return via Javascript upon click
		String cjobName = rmesg.cjobName
		String updateValue = rmesg.updateValue
		String updateDiv = rmesg.updateDiv
		String updated = rmesg.updated ?: 'yes'
		String nextValue = rmesg.nextValue ?: ''

		if (setId) {
			for (int a=3; a < depth; a++ ) {
				this."domain${a}" = rmesg."domain${a}"
				this."collectfield${a}" = rmesg."collectfield${a}"
				this."searchField${a}" = rmesg."searchField${a}"
				this."bindId${a}"= rmesg."bindId${a}"
				this."setId${a}"= rmesg."setId${a}"
			}
			Set<HashMap<String,String>> storedMap1= Collections.synchronizedSet(new HashSet<HashMap<String,String>>())

			def myMap = [
				jobName: jobName, setId: setId,  secondary: secondary,collectfield:collectfield,
				searchField:searchField, bindId:bindId, appendValue:appendValue, primary:primary,
				appendName:appendName, nextValue:nextValue, formatting:formatting
			]


			for (int a=3; a < depth; a++ ) {
				myMap += [("setId${a}"): this."setId${a}", ("domain${a}"): this."domain${a}",
					("searchField${a}"): this."searchField${a}", ("collectfield${a}"): this."collectfield${a}",
					("bindId${a}"): this."bindId${a}"]
			}

			def myMaper = userSession.userProperties.get("currentMap")
			storedMap1.add(myMap)
			if (myMaper) {
				myMaper.each { ss->
					storedMap1.add(ss)
				}
			}
			
			userSession.userProperties.put("currentMap", storedMap1)
			
		}else if (updateValue) {
			def myMaper = userSession.userProperties.get("currentMap")
			Map currentSelection = [:]
			if (myMaper) {
				boolean go = false
				myMaper.each { s ->
					go = false
					if (s.setId == updateDiv) {
						go = true
						secondary = s.secondary
						collectfield = s.collectfield
						searchField = s.searchField
						bindId = s.bindId
						appendValue = s.appendValue
						appendName = s.appendName
						formatting = s.formatting
						nextValue = s.nextValue
						primary = s.primary
					}
					if (go) {

						ArrayList result
						if (bindId.endsWith('.id')) {
						 result = autoCompleteService.selectDomainClass(secondary, collectfield, searchField, bindId, updateValue )
						}else{
						result = autoCompleteService.selectNoRefDomainClass(primary, secondary, collectfield, searchField, bindId, updateValue )
						}
						Map mresult = [ result: result,updateThisDiv: updateDiv, appendName: appendName, appendName: appendName,
							nextValue:nextValue,updated:updated, updateValue:updateValue, formatting:formatting]

						for (int a=3; a < depth; a++ ) {
							this."domain${a}" = mapValue( s, "domain${a}")
							this."collectfield${a}" =  mapValue( s, "collectfield${a}")
							this."searchField${a}" = mapValue( s, "searchField${a}")
							this."bindId${a}"= mapValue( s, "bindId${a}")
							this."setId${a}"= mapValue( s, "setId${a}")
							if (this."domain${a}") {
								def res
								if (this."bindId${a}".endsWith('.id')) {
									res = autoCompleteService.selectDomainClass(this."domain${a}", this."collectfield${a}",
										this."searchField${a}", this."bindId${a}", updateValue )
								}else{
									res = autoCompleteService.selectNoRefDomainClass(primary, this."domain${a}", this."collectfield${a}",
										this."searchField${a}", this."bindId${a}", updateValue  )
								}
								if (res) {
									mresult.put("result${a}", res)
									mresult.put("setId${a}", this."setId${a}")
								}
							}
						}
						clientListenerService.sendFrontEndPM(userSession, username,(mresult as JSON).toString())
					}
				}
			}
		}
	}

	private String mapValue(Map s, String search) {
		def se= s.find{ it.key  == search}
		if (se) {
			return se.value as String
		}
	}
}

