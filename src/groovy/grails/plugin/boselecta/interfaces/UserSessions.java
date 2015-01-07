package grails.plugin.boselecta.interfaces;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.websocket.Session;



public interface UserSessions {
	static Set<Session> jobUsers = Collections.synchronizedSet(new HashSet<Session>());
}
