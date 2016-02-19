package co.com.poncho.websocket;

import java.io.StringReader;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import co.com.poncho.model.User;

@ApplicationScoped
@ServerEndpoint("/ponchito")
public class Websocket {

	@Inject
    private UserSessionHandler sessionHandler;
	
	@OnOpen
	public void open(Session session) {
		sessionHandler.addSession(session);
	}

	@OnClose
	public void close(Session session) {
		sessionHandler.removeSession(session);
	}

	@OnError
	public void onError(Throwable error) {
		System.out.println("Error en ponchitooooooooooo");
		error.printStackTrace();
	}

	@OnMessage
	public void handleMessage(String message, Session session) {
		try (JsonReader reader = Json.createReader(new StringReader(message))) {
            JsonObject jsonMessage = reader.readObject();

            if ("add".equals(jsonMessage.getString("action"))) {
            	User user = new User();
            	user.setName(jsonMessage.getString("name"));
                sessionHandler.addUser(user);
            }

            if ("remove".equals(jsonMessage.getString("action"))) {
                int id = (int) jsonMessage.getInt("id");
                sessionHandler.removeUser(id);
            }

            if ("toggle".equals(jsonMessage.getString("action"))) {
                int id = (int) jsonMessage.getInt("id");
                sessionHandler.toggleUser(id);
            }
        }
	}
}
