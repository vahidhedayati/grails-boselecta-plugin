package grails.plugin.boselecta


import grails.plugin.boselecta.interfaces.UserSessions
import grails.util.Environment

import javax.servlet.ServletContext
import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener
import javax.servlet.annotation.WebListener
import javax.websocket.EndpointConfig
import javax.websocket.OnClose
import javax.websocket.OnError
import javax.websocket.OnMessage
import javax.websocket.OnOpen
import javax.websocket.Session
import javax.websocket.server.PathParam
import javax.websocket.server.ServerContainer
import javax.websocket.server.ServerEndpoint

import org.codehaus.groovy.grails.web.context.ServletContextHolder as SCH
import org.codehaus.groovy.grails.web.json.JSONObject
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes as GA
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@WebListener
@ServerEndpoint("/BoSelectaEndpoint/{job}")
class BoSelectaEndpoint  implements ServletContextListener, UserSessions {

	private final Logger log = LoggerFactory.getLogger(getClass().name)

	private ConfigObject config
	private AuthService authService
	private MessagingService messagingService
	
	@Override
	public void contextInitialized(ServletContextEvent event) {
		ServletContext servletContext = event.servletContext
		final ServerContainer serverContainer = servletContext.getAttribute("javax.websocket.server.ServerContainer")
		try {

			if (Environment.current == Environment.DEVELOPMENT) {
				serverContainer.addEndpoint(BoSelectaEndpoint)
			}

			def ctx = servletContext.getAttribute(GA.APPLICATION_CONTEXT)

			def grailsApplication = ctx.grailsApplication

			config = grailsApplication.config.boselecta
			int defaultMaxSessionIdleTimeout = config.timeout ?: 0
			serverContainer.defaultMaxSessionIdleTimeout = defaultMaxSessionIdleTimeout
		}
		catch (IOException e) {
			log.error e.message, e
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
	}

	@OnOpen
	public void handleOpen(Session userSession,EndpointConfig c,@PathParam("job") String job) {
		jobUsers.add(userSession)
		
		def ctx= SCH.servletContext.getAttribute(GA.APPLICATION_CONTEXT)
		def grailsApplication = ctx.grailsApplication
		config = grailsApplication.config.boselecta
		authService = ctx.authService
		messagingService = ctx.messagingService

		userSession.userProperties.put("job", job)
		
	}


	@OnMessage
	public void handleMessage(String message,Session userSession) throws IOException {
		try {
			verifyAction(userSession,message)
		} catch(IOException e) {
			log.debug "Error $e"
		}
	}

	@OnClose
	public void handeClose(Session userSession) throws SocketException {
		try {
			jobUsers.remove(userSession)
		} catch(SocketException e) {
			log.debug "Error $e"
		}
	}

	@OnError
	public void handleError(Throwable t) {
		t.printStackTrace()
	}

	private void verifyAction(Session userSession,String message) {
		def myMsg = [:]
		String username = userSession.userProperties.get("username") as String
		String job  =  userSession.userProperties.get("job") as String
		String connector = "CONN:-"
		Boolean isuBanned = false
		if (!username)  {
			if (message.startsWith(connector)) {
				authService.connectUser(message,userSession,job)
			}
		}else{
			if (message.startsWith("DISCO:-")) {
				jobUsers.remove(userSession)
				userSession.close()
				Session usersSess = findSession(username+frontend)
				if (usersSess) {
					jobUsers.remove(usersSess)
					usersSess.close()
				}
			}else if (message.startsWith("/pm")) {
				def values = parseInput("/pm ",message)
				String user = values.user as String
				def msg = values.msg

				if (!user.equals(username)) {
					messagingService.privateMessage(userSession, user,msg)
				}else{
					myMsg.put("message","Private message self?")
					messagingService.messageUser(userSession,myMsg)
				}
			}
		}
	}

	private Map<String, JSONObject> parseInput(String mtype,String message){
		def p1 = mtype
		def mu = message.substring(p1.length(),message.length())
		def msg
		def user
		def resultset = []
		if (mu.indexOf(",")>-1) {
			user = mu.substring(0,mu.indexOf(","))
			msg = mu.substring(user.length()+1,mu.length())
		}else{
			user = mu.substring(0,mu.indexOf(" "))
			msg = mu.substring(user.length()+1,mu.length())
		}
		Map<String, String> values  =  new HashMap<String, Double>();
		values.put("user", user);
		values.put("msg", msg);
		return values
	}

	private String getFrontend() {
		return config.frontenduser ?: '_frontend'
	}
	
	private Session findSession(String username) {
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

}
