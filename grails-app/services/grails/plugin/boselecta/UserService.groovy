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
	/*
	 public ArrayList genAllUsers() {
	 def uList = []
	 synchronized (jobUsers) {
	 jobUsers?.each { crec->
	 def myUser = [:]
	 if (crec && crec.isOpen()) {
	 def cuser = crec.userProperties.get("username").toString()
	 uList.add(cuser)
	 }
	 }
	 }
	 return uList				
	 }
	 private ArrayList genUserMenu(ArrayList friendslist, ArrayList blocklist, String room, String uiterator, String listType ) {
	 def uList = []
	 def vList = []
	 try {
	 synchronized (jobUsers) {
	 jobUsers?.each { crec->
	 def myUser = [:]
	 if (crec && crec.isOpen()) { 
	 def cuser = crec.userProperties.get("username").toString()
	 vList.add(cuser)
	 if (room.equals(crec.userProperties.get("room"))) {
	 //def cuser = crec.userProperties.get("username").toString()
	 def av = crec.userProperties.get("av").toString()
	 def rtc = crec.userProperties.get("rtc").toString()	
	 if (listType=="generic") { 
	 if (cuser.equals(uiterator)) {
	 myUser.put("owner", cuser)
	 uList.add(myUser)
	 }else{
	 myUser.put("user", cuser)
	 uList.add(myUser)
	 }	
	 }else{
	 myUser.put("users", cuser)
	 uList.add(myUser)
	 }
	 }
	 }
	 }
	 }	
	 } catch (IOException e) {
	 log.error ("onMessage failed", e)
	 }
	 return uList
	 }
	 def sendFlatUsers(Session userSession,String username) {
	 userListGen(userSession, username, "flat")
	 }
	 def sendUsers(Session userSession,String username) {
	 userListGen(userSession, username, "generic")
	 }
	 private void userListGen(Session userSession,String username, String listType) {
	 String room  =  userSession.userProperties.get("room") as String
	 try {
	 synchronized (jobUsers) {
	 jobUsers?.each { crec2->
	 if (crec2 && crec2.isOpen()) {
	 String uiterator = crec2.userProperties.get("username").toString()
	 def finalList = [:]
	 def blocklist
	 def friendslist
	 def	uList = genUserMenu(friendslist, blocklist, room, uiterator, listType)	
	 if (listType=="generic") {
	 finalList.put("users", uList)
	 }else{
	 finalList.put("flatusers", uList)
	 }
	 sendUserList(uiterator,finalList)
	 }
	 }
	 }
	 } catch (IOException e) {
	 log.error ("onMessage failed", e)
	 }
	 }
	 void sendUsersLoggedOut(String room,String username) {
	 try {
	 def myMsg = [:]
	 String sendleave = config.send.leftroom  ?: 'yes'
	 myMsg.put("message", "${username} has left ${room}")
	 synchronized (jobUsers) {
	 jobUsers?.each { crec2->
	 if (crec2 && crec2.isOpen()) {
	 def uiterator = crec2.userProperties.get("username").toString()
	 if (uiterator != username) {
	 //broadcast(crec2,myMsg)
	 //isuBanned = isBanned(username)
	 //if (!isuBanned && (sendleave= = 'yes')){
	 if (sendleave == 'yes') {
	 messagingService.broadcast(crec2,myMsg)
	 }
	 def finalList = [:]
	 def blocklist
	 def friendslist
	 def uList = genUserMenu(friendslist, blocklist, room, uiterator, 'generic')
	 finalList.put("users", uList)
	 sendUserList(uiterator,finalList)
	 }
	 }
	 }
	 }
	 } catch (IOException e) {
	 log.error ("onMessage failed", e)
	 }
	 }
	 private void sendUserList(String iuser,Map msg) {
	 def myMsgj = msg as JSON
	 try {
	 synchronized (jobUsers) {
	 jobUsers?.each { crec->
	 if (crec && crec.isOpen()) {
	 def cuser = crec.userProperties.get("username").toString()
	 if (cuser=='null') {
	 removeUser(cuser)
	 //logoutUser(cuser)
	 }
	 if (cuser.equals(iuser)) {
	 crec.basicRemote.sendText(myMsgj as String)
	 }
	 }
	 }
	 }
	 } catch (IOException e) {
	 log.error ("onMessage failed", e)
	 }
	 }
	 */
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
			//chatroomUsers.remove(userSession)
		}else{
			return username
		}
	}
}
