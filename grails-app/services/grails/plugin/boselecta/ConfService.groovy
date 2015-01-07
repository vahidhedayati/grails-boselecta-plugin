package grails.plugin.boselecta

import java.text.SimpleDateFormat
import java.util.Map;

import javax.websocket.Session


class ConfService {

	static transactional  =  false

	def grailsApplication

	boolean isConfigEnabled(String input) {
		return Boolean.valueOf(input ?: false)
	}


	Boolean isAdmin(Session userSession) {
		Boolean useris = false
		String userLevel = userSession.userProperties.get("userLevel") as String
		if (userLevel.toString().toLowerCase().startsWith('admin')) {
			useris = true
		}
		return useris
	}

	String getFrontend() {

		return config.frontenduser ?: '_frontend'
	}

	String getAppName(){
		String addAppName = config.add.appName ?: 'yes'
		if (addAppName) {
			grailsApplication.metadata['app.name']+"/"
		}else{
			return
		}
	}

	public Map<String, String> parseInput(String mtype,String message){
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

	def getConfig() {
		grailsApplication?.config?.boselecta
	}

}
