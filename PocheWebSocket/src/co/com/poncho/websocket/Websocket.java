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

	@OnClose
	public void onConnectionClose(Session session) {
		System.out.println("cerrada conexion");
		sessionHandler.removeSession(session);
	}

	@Inject
	private UserSessionHandler sessionHandler;

	@OnOpen
	public void open(Session session) {
		System.out.println("open session ");
		sessionHandler.addSession(session);
	}

	@OnMessage
	public void onMessage(String message, Session session) {
		System.out.println("call message");
		try (JsonReader reader = Json.createReader(new StringReader(message))) {
			JsonObject jsonMessage = reader.readObject();

			int comando = jsonMessage.getInt("comando");
			System.out.println(comando);
			switch (comando) {
			case 0:
				System.out.println("Registrar usuario");
				String nombre = jsonMessage.getString("nombre");
				Usuario user = new Usuario(nombre);
				sessionHandler.addUser(user, session);
				break;

			case 1:
				System.out.println("Registrar voto");
				JsonObject vote = jsonMessage.getJsonObject("vote");
				int voto = vote.getInt("value");
				int tipoVoto = vote.getInt("type");
				sessionHandler.registerVote(voto, tipoVoto, session);
				break;
			case 2:
				System.out.println("Aprobar votación");
				sessionHandler.aprobarVotacion(session);
				break;
			default:
				break;
			}

		}
	}

}
