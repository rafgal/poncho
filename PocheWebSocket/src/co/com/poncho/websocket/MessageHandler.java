package co.com.poncho.websocket;

import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.websocket.Session;

import com.google.gson.JsonObject;

public class MessageHandler {
	
	public static void sendToAllConnectedSessions(Set<Session> sessions, JsonObject message) {
		for (Session session : sessions) {
			sendToSession(session, message);
		}
	}

	public static void sendToSession(Session session, JsonObject message) {
		try {
			if(session.isOpen()){
				System.out.println("message: " + message.toString());
				session.getBasicRemote().sendText(message.toString());
			}
		} catch (IOException ex) {
			Logger.getLogger(UserSessionHandler.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

}
