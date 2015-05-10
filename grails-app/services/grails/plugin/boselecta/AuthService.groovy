package grails.plugin.boselecta

import grails.plugin.boselecta.interfaces.UserSessions
import grails.transaction.Transactional

import javax.websocket.Session

@Transactional
class AuthService extends ConfService {

	def messagingService


	public void connectUser(String message,Session userSession,String room) {
		def myMsg = [:]
		String connector = "CONN:-"
		def user
		def username = message.substring(message.indexOf(connector)+connector.length(),message.length()).trim().replace(' ', '_').replace('.', '_')
		if (loggedIn(username)==false) {
			userSession.userProperties.put("username", username)
			jobUsers.putIfAbsent(username, userSession)
		}else{
			myMsg.put("message", "${username} is already loggged in elsewhere, action denied")
		}
		if (myMsg) {
			messagingService.messageUser(userSession,myMsg)
		}
	}



	Boolean loggedIn(String user) {
		Boolean loggedin = false
		jobNames.each { String cuser, Session crec ->
			if (crec && crec.isOpen()) {
				if (cuser.equals(user)) {
					loggedin = true
				}
			}
		}
		return loggedin
	}

}
