package grails.plugin.boselecta

import grails.converters.JSON
import grails.plugin.boselecta.interfaces.ClientSessions

import javax.websocket.Session

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
println "----------------> ${rmesg}"
		// Initial connection 
		String secondary = rmesg.secondary
		String collectfield = rmesg.collectfield
		String searchField = rmesg.searchField
		String  setId = rmesg.setId
		
		String bindId = rmesg.bindId
		String appendValue = rmesg.appendValue
		String appendName = rmesg.appendName
		String jobName = rmesg.job

		String domain3 = rmesg.domain3
		String collectfield3 = rmesg.collectfield3
		String searchField3 = rmesg.searchField3
		String  setId3 = rmesg.setId3
		String bindId3 = rmesg.bindId3
		
		String domain4 = rmesg.domain4
		String collectfield4 = rmesg.collectfield4
		String searchField4 = rmesg.searchField4
		String  setId4 = rmesg.setId4
		String bindId4 = rmesg.bindId4
		
		String domain5 = rmesg.domain5
		String collectfield5 = rmesg.collectfield5
		String searchField5 = rmesg.searchField5
		String  setId5 = rmesg.setId5
		String bindId5 = rmesg.bindId5
		
		String domain6 = rmesg.domain6
		String collectfield6 = rmesg.collectfield6
		String searchField6 = rmesg.searchField6
		String  setId6 = rmesg.setId6
		String bindId6 = rmesg.bindId6
		
		String domain7 = rmesg.domain7
		String collectfield7 = rmesg.collectfield7
		String searchField7 = rmesg.searchField7
		String  setId7 = rmesg.setId7
		String bindId7 = rmesg.bindId7
		
		String domain8 = rmesg.domain8
		String collectfield8 = rmesg.collectfield8
		String searchField8 = rmesg.searchField8
		String  setId8 = rmesg.setId8
		String bindId8 = rmesg.bindId8
		
		String domain9 = rmesg.domain9
		String collectfield9 = rmesg.collectfield9
		String searchField9 = rmesg.searchField9
		String  setId9 = rmesg.setId9
		String bindId9 = rmesg.bindId9
		
		
		//Return via Javascript upon click
		String cjobName = rmesg.cjobName
		String updateValue = rmesg.updateValue
		String updateDiv = rmesg.updateDiv
		String updated = rmesg.updated ?: 'yes'

		if (setId) {
			
			def myMap = [
				jobName: jobName, setId: setId,  secondary: secondary,collectfield:collectfield, 
				searchField:searchField, bindId:bindId, appendValue:appendValue, appendName:appendName,
				setId3: setId3, domain3: domain3, searchField3:searchField3, collectfield3:collectfield3, bindId3: bindId3,
				setId4: setId4, domain4: domain4, searchField4:searchField4, collectfield4:collectfield4, bindId4: bindId4,
				setId5: setId5, domain5: domain5, searchField5:searchField5, collectfield5:collectfield5, bindId5: bindId5,
				setId6: setId6, domain6: domain6, searchField6:searchField6, collectfield6:collectfield6, bindId6: bindId6,
				setId7: setId7, domain7: domain7, searchField7:searchField7, collectfield7:collectfield7, bindId7: bindId7,
				setId8: setId8, domain8: domain8, searchField8:searchField8, collectfield8:collectfield8, bindId8: bindId8,
				setId9: setId9, domain9: domain9, searchField9:searchField9, collectfield9:collectfield9, bindId9: bindId9
			]

			storedMap.add(myMap)

		}else if (updateValue) {
			Map currentSelection = [:]
			if (storedMap) {
				boolean go = false

				storedMap.each { s ->
					go = false
					if (s.setId == updateDiv) {
						go = true
						secondary = s.secondary
						collectfield = s.collectfield
						searchField = s.searchField
						bindId = s.bindId
						
						domain3 = s.domain3
						collectfield3 = s.collectfield3
						searchField3 = s.searchField3
						bindId3 = s.bindId3
						setId3 = s.setId3
						
						domain4 = s.domain4
						collectfield4 = s.collectfield4
						searchField4 = s.searchField4
						bindId4 = s.bindId4
						setId4 = s.setId4
						
						domain5 = s.domain5
						collectfield5 = s.collectfield5
						searchField5 = s.searchField5
						bindId5 = s.bindId5
						setId5 = s.setId5
						
						domain6 = s.domain6
						collectfield6 = s.collectfield6
						searchField6 = s.searchField6
						bindId6 = s.bindId6
						setId6 = s.setId6
						
						domain7 = s.domain7
						collectfield7 = s.collectfield7
						searchField7 = s.searchField7
						bindId7 = s.bindId7
						setId7 = s.setId7
						
						domain8 = s.domain8
						collectfield8 = s.collectfield8
						searchField8 = s.searchField8
						bindId8 = s.bindId8
						setId8 = s.setId8
						
						domain9 = s.domain9
						collectfield9 = s.collectfield9
						searchField9 = s.searchField9
						bindId9 = s.bindId9
						setId9 = s.setId9
						
						appendValue = s.appendValue
						appendName = s.appendName
					}
					if (go) {
						ArrayList result = autoCompleteService.selectDomainClass(secondary, collectfield, searchField, bindId, updateValue )
						
						ArrayList result3, result4, result5, result6, result7, result8, result9=[]
						
						if (domain3 && domain3=="null") {
							result3 = autoCompleteService.selectDomainClass(domain3, collectfield3, searchField3, bindId3, setId3 )
						}
						if (domain4  && domain4=="null") {
							result4 = autoCompleteService.selectDomainClass(domain4, collectfield4, searchField4, bindId4, setId4 )
						}
						if (domain5  && domain5=="null") {
							result5 = autoCompleteService.selectDomainClass(domain5, collectfield5, searchField5, bindId5, setId5 )
						}
						if (domain6  && domain6=="null") {
							result6 = autoCompleteService.selectDomainClass(domain6, collectfield6, searchField6, bindId6, setId6 )
						}
						if (domain7  && domain7=="null") {
							result7 = autoCompleteService.selectDomainClass(domain7, collectfield7, searchField7, bindId7, setId7 )
						}
						if (domain8  && domain8=="null") {
							result8 = autoCompleteService.selectDomainClass(domain8, collectfield8, searchField8, bindId8, setId8 )
						}
						if (domain9  && domain9=="null") {
							result9 = autoCompleteService.selectDomainClass(domain9, collectfield9, searchField9, bindId9, setId9 )
						}
						
						
						JSON mresult = ([ result: result, result3: result3, result4: result4, result5: result5, result6: result6, result7: result7, result8: result8, 
							result9: result9, updateThisDiv: updateDiv, appendName: appendName, appendName: appendName, updated:updated ]) as JSON
						clientListenerService.sendFrontEndPM(userSession, username,mresult as String)

					}
				}
			}
		}
	}
}

