package co.com.poncho.websocket;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonObject;
import javax.websocket.Session;

import co.com.poncho.model.User;

@ApplicationScoped
public class UserSessionHandler {
	
	private int userId = 0;
	private final Set sessions = new HashSet<>();
    private final Set users = new HashSet<>();
    
    public void addSession(Session session) {
        sessions.add(session);
    }

    public void removeSession(Session session) {
        sessions.remove(session);
    }
    
    public List getUsers() {
        return new ArrayList<>(users);
    }

    public void addUser(User user) {
    }

    public void removeUser(int id) {
    }

    public void toggleUser(int id) {
    }

    private User getUserById(int id) {
        return null;
    }

    private JsonObject createAddMessage(User user) {
        return null;
    }

    private void sendToAllConnectedSessions(JsonObject message) {
    }

    private void sendToSession(Session session, JsonObject message) {
    }
}
