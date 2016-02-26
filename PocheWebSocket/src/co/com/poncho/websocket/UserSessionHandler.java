package co.com.poncho.websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonObject;
import javax.json.spi.JsonProvider;
import javax.websocket.Session;

import co.com.poncho.model.Usuario;

@ApplicationScoped
public class UserSessionHandler {

	private int userId = 0;
	private final Set<Session> sessions = new HashSet<>();
	private final Set<Usuario> users = new HashSet<>();

	public void addSession(Session session) {
		System.out.println("todo bien en add session");
		sessions.add(session);
		for (Usuario user : users) {
			JsonObject addMessage = createAddMessage(user);
			sendToSession(session, addMessage);
		}
	}

	public void removeSession(Session session) {
		sessions.remove(session);
	}

	public List getUsers() {
		return new ArrayList<>(users);
	}

	public void addUser(Usuario user) {
		System.out.println("adduser");
		users.add(user);
		JsonObject addMessage = createAddMessage(user);
		sendToAllConnectedSessions(addMessage);
	}

//	public void removeUser(int id) {
//		Usuario user = getUserById(id);
//		if (user != null) {
//			users.remove(user);
//			JsonProvider provider = JsonProvider.provider();
//			JsonObject removeMessage = provider.createObjectBuilder().add("action", "remove").add("id", id).build();
//			sendToAllConnectedSessions(removeMessage);
//		}
//	}

	private JsonObject createAddMessage(Usuario user) {
		JsonProvider provider = JsonProvider.provider();
		JsonObject addMessage = provider.createObjectBuilder().add("action", "add")

				.build();
		return addMessage;
	}

	private void sendToAllConnectedSessions(JsonObject message) {
		System.out.println("bcast");
		for (Session session : sessions) {
			sendToSession(session, message);
		}
	}

	private void sendToSession(Session session, JsonObject message) {
		try {
			session.getBasicRemote().sendText(message.toString());
		} catch (IOException ex) {
			sessions.remove(session);
			Logger.getLogger(UserSessionHandler.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
