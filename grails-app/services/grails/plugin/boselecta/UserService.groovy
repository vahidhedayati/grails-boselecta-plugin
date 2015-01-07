package grails.plugin.boselecta

import grails.converters.JSON
import grails.plugin.boselecta.interfaces.UserSessions
import groovy.time.TimeCategory

import java.text.SimpleDateFormat

import javax.websocket.Session

//@Transactional
class UserService extends ConfService  implements UserSessions {

	def messagingService

	void kickUser(Session userSession,String username) {
		Boolean useris = isAdmin(userSession)
		if (useris) {
			logoutUser(userSession,username)
		}
	}

	def logUserOut(Session userSession,String username,String room) {
		userSession.close()
		removeUser(username)
	}

	private void logoutUser(String username) {
		def myMsg = [:]
		myMsg.put("message", "${username} about to be kicked off")
		try {
			synchronized (jobUsers) {
				jobUsers?.each { crec->
					if (crec && crec.isOpen()) {
						def uList = []
						def finalList = [:]
						def cuser = crec.userProperties.get("username").toString()
						if (cuser.equals(username)) {
							def myMsg1 = [:]
							myMsg1.put("system","disconnect")
							messagingService.messageUser(crec,myMsg1)
						}
					}
				}
			}
		} catch (IOException e) {
			log.error ("onMessage failed", e)
		}
	}

	private void logoutUser(Session userSession,String username) {
		def myMsg = [:]
		myMsg.put("message", "${username} about to be kicked off")
		messagingService.broadcast(userSession,myMsg)
		try {
			synchronized (jobUsers) {
				jobUsers?.each { crec->
					if (crec && crec.isOpen()) {
						def uList = []
						def finalList = [:]
						def cuser = crec.userProperties.get("username").toString()
						if (cuser.equals(username)) {
							def myMsg1 = [:]
							myMsg1.put("system","disconnect")
							messagingService.messageUser(crec,myMsg1)
						}
					}
				}
			}
		} catch (IOException e) {
			log.error ("onMessage failed", e)
		}
	}

	Session usersSession(String username) {
		Session userSession
		try {
			synchronized (jobUsers) {
				jobUsers?.each { crec->
					if (crec && crec.isOpen()) {
						def cuser = crec.userProperties.get("username").toString()
						if (cuser.equals(username)) {
							userSession=crec
						}
					}
				}
			}
		} catch (IOException e) {
			log.error ("onMessage failed", e)
		}
		return userSession
	}

	boolean findUser(String username) {
		boolean found = false
		try {
			synchronized (jobUsers) {
				jobUsers?.each { crec->
					if (crec && crec.isOpen()) {
						def cuser = crec.userProperties.get("username").toString()
						if (cuser.equals(username)) {
							found = true
						}
					}
				}
			}
		} catch (IOException e) {
			log.error ("onMessage failed", e)
		}
		return found
	}
	
	private void removeUser(String username) {
		try {
			synchronized (jobUsers) {
				jobUsers?.each { crec->
					if (crec && crec.isOpen()) {
						def cuser = crec.userProperties.get("username").toString()
						if (cuser.equals(username)) {
							jobUsers.remove(crec)
						}
					}
				}
			}
		} catch (IOException e) {
			log.error ("onMessage failed", e)
		}
	}




	private String getCurrentUserName(Session userSession) {
		def myMsg = [:]
		String username = userSession.userProperties.get("username") as String
		if (!username) {
			myMsg.put("message","Access denied no username defined")
			messagingService.messageUser(userSession,myMsg)
		}else{
			return username
		}
	}
}
