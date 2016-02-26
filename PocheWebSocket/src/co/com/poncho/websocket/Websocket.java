package co.com.poncho.websocket;

import java.io.StringReader;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import co.com.poncho.model.Usuario;

//@ApplicationScoped
@ServerEndpoint("/ponchito")
public class Websocket {

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@OnClose
	public void onConnectionClose(Session session) {
		logger.info("Connection close .... " + session.getId());
	}

	@Inject
	private UserSessionHandler sessionHandler;

	@OnOpen
	public void open(Session session) {
		System.out.println("open session ");
		sessionHandler.addSession(session);
	}

	
	@OnMessage
	public void  onMessage(String message, Session session) {
		System.out.println("call message");
		try (JsonReader reader = Json.createReader(new StringReader(message))) {
			JsonObject jsonMessage = reader.readObject();

			int comando = jsonMessage.getInt("comando");
			
			switch (comando) {
			case 0:
				String nombre = jsonMessage.getString("nombre");
				Usuario user = new Usuario(nombre);
				sessionHandler.addUser(user);
				break;

			default:
				break;
			}
			
		}
	}

}
