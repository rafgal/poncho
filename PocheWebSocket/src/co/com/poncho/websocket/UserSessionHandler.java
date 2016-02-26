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

import co.com.poncho.model.User;

@ApplicationScoped
public class UserSessionHandler {
	
	private int userId = 0;
	private final Set<Session> sessions = new HashSet<>();
    private final Set<User> users = new HashSet<>();
    
    public void addSession(Session session) {
        sessions.add(session);
        for (User user : users) {
            JsonObject addMessage = createAddMessage(user);
            sendToSession(session, addMessage);
        }
    }

    public void removeSession(Session session) {
        sessions.remove(session);
    }
    
    public List<User> getUsers() {
        return new ArrayList<>(users);
    }

    public void addUser(User user) {
    	user.setId(userId);
    	users.add(user);
    	userId++;
        JsonObject addMessage = createAddMessage(user);
        sendToAllConnectedSessions(addMessage);
    }

    public void removeUser(int id) {
    	User user = getUserById(id);
        if (user != null) {
            users.remove(user);
            JsonProvider provider = JsonProvider.provider();
            JsonObject removeMessage = provider.createObjectBuilder()
                    .add("action", "remove")
                    .add("id", id)
                    .build();
            sendToAllConnectedSessions(removeMessage);
        }
    }

    public void toggleUser(int id) {
    	JsonProvider provider = JsonProvider.provider();
        User user = getUserById(id);
        if (user != null) {
            if (user.isStatus()) {
                user.setStatus(false);
            } else {
                user.setStatus(true);
            }
            JsonObject updateDevMessage = provider.createObjectBuilder()
                    .add("action", "toggle")
                    .add("id", user.getId())
                    .add("status", user.isStatus())
                    .build();
            sendToAllConnectedSessions(updateDevMessage);
        }
    }

    private User getUserById(int id) {
    	for (User user : users) {
            if (user.getId() == id) {
                return user;
            }
        }
        return null;
    }

    private JsonObject createAddMessage(User user) {
    	JsonProvider provider = JsonProvider.provider();
        JsonObject addMessage = provider.createObjectBuilder()
                .add("action", "add")
                .add("id", user.getId())
                .add("name", user.getName())
                .add("status", user.isStatus())
                .build();
        return addMessage;
    }

    private void sendToAllConnectedSessions(JsonObject message) {
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
