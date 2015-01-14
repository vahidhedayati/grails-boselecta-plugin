package grails.plugin.boselecta

import grails.plugin.boselecta.interfaces.UserSessions
import grails.transaction.Transactional

import javax.websocket.Session

@Transactional
class AuthService extends ConfService implements UserSessions  {

	def messagingService


	public void connectUser(String message,Session userSession,String room) {
		def myMsg = [:]
		String connector = "CONN:-"
		def user
		def username = message.substring(message.indexOf(connector)+connector.length(),message.length()).trim().replace(' ', '_').replace('.', '_')
		if (loggedIn(username)==false) {
			userSession.userProperties.put("username", username)
		}else{
			myMsg.put("message", "${username} is already loggged in elsewhere, action denied")
		}
		if (myMsg) {
			messagingService.broadcast(userSession,myMsg)
		}
	}


	
	Boolean loggedIn(String user) {
		Boolean loggedin = false
		try {
			synchronized (jobUsers) {
				jobUsers?.each { crec->
					if (crec && crec.isOpen()) {
						def cuser = crec.userProperties.get("username").toString()
						if (cuser.equals(user)) {
							loggedin = true
						}
					}
				}
			}
		} catch (IOException e) {
			log.info ("onMessage failed", e)
		}
		return loggedin
	}

}
