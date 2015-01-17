package grails.plugin.boselecta


import grails.converters.JSON
import grails.plugin.boselecta.interfaces.ClientSessions
import grails.plugin.boselecta.interfaces.UserSessions

import javax.websocket.ContainerProvider
import javax.websocket.Session

public class ClientListenerService extends ConfService implements UserSessions, ClientSessions {


	def sendArrayPM(Session userSession, String job,String message) {
		try {
			synchronized (jobUsers) {
				jobUsers?.each { crec->
					if (crec && crec.isOpen()) {
						String cuser = crec.userProperties.get("username") as String
						String cjob =  crec.userProperties.get("job") as String
						boolean found = false
						if (job==cjob) {
							found=findUser(cuser)
							if (found) {
								crec.basicRemote.sendText("/pm ${cuser},${message}")
							}
							if (!cuser.toString().endsWith(frontend)) {
								found=findUser(cuser+frontend)
								if (found) {
									crec.basicRemote.sendText("/pm ${cuser+frontend},${message}")
								}
							}
						}
					}
				}
			}
		} catch (IOException e) {
			log.error ("onMessage failed", e)
		}
	}

	def sendJobMessage(String job,String message) {
		try {
			synchronized (jobUsers) {
				jobUsers?.each { crec->
					if (crec && crec.isOpen()) {
						String cuser = crec.userProperties.get("username") as String
						String cjob =  crec.userProperties.get("job") as String
						boolean found = false
						if (job==cjob) {
							crec.basicRemote.sendText("${message}")
						}
					}
				}
			}

		} catch (IOException e) {
			log.error ("onMessage failed", e)
		}
	}


	def sendFrontEndPM(Session userSession, String user,String message) {
		def found=findUser(user+frontend)
		// Fixed - private messaging from backend to front-end
		// messages were getting sent to all before.
		userSession.basicRemote.sendText("/pm ${user+frontend},${message}")

	}

	// Added backend PM - new connection info was being relayed to all before.
	def sendBackPM(String user,String message) {
		if (user.endsWith(frontend)) {
			user=user.substring(0,user.indexOf(frontend))
		}
		try {
			synchronized (jobUsers) {
				jobUsers?.each { crec->
					if (crec && crec.isOpen()) {
						String cuser = crec.userProperties.get("username") as String
						String cjob =  crec.userProperties.get("job") as String
						boolean found = false
						if (user==cuser) {
							crec.basicRemote.sendText("${message}")
						}
					}
				}
			}

		} catch (IOException e) {
			log.error ("onMessage failed", e)
		}
	}

	def sendBackEndPM(Session userSession, String user,String message) {
		if (user.endsWith(frontend)) {
			user=user.substring(0,user.indexOf(frontend))
		}
		userSession.basicRemote.sendText("/pm ${user},${message}")
	}

	def sendPM(Session userSession, String user,String message) {
		String username = userSession.userProperties.get("username") as String
		boolean found

		found=findUser(user)
		if (found) {
			userSession.basicRemote.sendText("/pm ${user},${message}")
		}
		if (!user.endsWith(frontend)) {
			found=findUser(user+frontend)
			if (found) {
				sendFrontEndPM(userSession,user, message )
			}
		}
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


	public void sendMessage(Session userSession,final String message) {
		userSession.basicRemote.sendText(message)
	}

	Session p_connect(String _uri, String _username, String room){
		URI oUri
		if(_uri){
			oUri = URI.create(_uri+room);
		}
		def container = ContainerProvider.getWebSocketContainer()
		Session oSession
		try{
			oSession = container.connectToServer(BoSelectaClientEndpoint.class, oUri)
			oSession.basicRemote.sendText(CONNECTOR+_username)
		}catch(Exception e){
			e.printStackTrace()
			if(oSession && oSession.isOpen()){
				oSession.close()
			}
			return null
		}
		oSession.userProperties.put("username", _username)
		return  oSession
	}


	public Session disconnect(Session _oSession){
		try{
			if(_oSession && _oSession.isOpen()){
				sendMessage(_oSession, DISCONNECTOR)
			}
		}catch (Exception e){
			e.printStackTrace()
		}
		return _oSession
	}



}
