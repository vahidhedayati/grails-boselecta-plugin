package grails.plugin.boselecta

import grails.converters.JSON
import grails.plugin.boselecta.interfaces.UserSessions

import javax.websocket.Session

import org.codehaus.groovy.grails.web.json.JSONObject


class MessagingService extends ConfService  implements UserSessions {


	def sendMsg(Session userSession,String msg) {
		try {
			userSession.basicRemote.sendText(msg)
		} catch (IOException e) {
		}
	}

	def messageUser(Session userSession,Map msg) {
		def myMsgj = (msg as JSON).toString()
		sendMsg(userSession, myMsgj)
	}

	def privateMessage(Session userSession,String user,String msg) {
		String urecord = userSession.userProperties.get("username") as String
		Boolean found = false
		try {
			synchronized (jobUsers) {
				jobUsers?.each { crec->
					if (crec && crec.isOpen()) {
						def cuser = crec.userProperties.get("username").toString()
						if (cuser.equals(user)) {
							found = true
							if (cuser.endsWith(frontend)) {
								messageUser(crec,["message": "${msg}"])
							}else{
								crec.basicRemote.sendText(msg as String)
							}
						}
					}
				}
			}
		} catch (IOException e) {
			log.error ("onMessage failed", e)
		}
	}

}
