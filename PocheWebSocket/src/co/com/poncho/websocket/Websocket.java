package co.com.poncho.websocket;

import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import co.com.poncho.model.Room;
import co.com.poncho.model.Usuario;

@ServerEndpoint("/ponchito")
public class Websocket {

	@OnClose
	public void onConnectionClose(Session session) {
		System.out.println("cerrada conexion");
		sessionHandler.removeSession(session);
	}

	@Inject
	private UserSessionHandler sessionHandler;
	private RoomSessionHandler roomsHandler;

	@OnOpen
	public void open(Session session) {
		System.out.println("open session ");
		sessionHandler.addSession(session);
	}

	@OnMessage
	public void onMessage(String message, Session session) {
		System.out.println("call message");
		JsonParser parser = new JsonParser();
		JsonObject jsonMessage = parser.parse(message).getAsJsonObject();

		int comando = jsonMessage.get("comando").getAsInt();
		Command command=Command.fromInt(comando);
		System.out.println(comando);
		switch (command) {
		case REGISTER_USER:
			System.out.println("Registrar usuario");
			String nombre = jsonMessage.get("nombre").getAsString();
			String roomName = jsonMessage.get("room").getAsString();
			Usuario user = new Usuario(nombre);
			if(jsonMessage.get("property") != null){
				Room room = new Room(roomName, user);
			}else{
				
			}
			
			sessionHandler.addUser(user, session);
			break;
		case VOTE:
			System.out.println("Registrar voto");
			JsonObject vote = jsonMessage.get("vote").getAsJsonObject();
			float voto = vote.get("value").getAsFloat();
			int tipoVoto = vote.get("type").getAsInt();
			sessionHandler.registerVote(voto, tipoVoto, session);
			break;
		case EVAL_RESULTS:
			System.out.println("Evaluar votación");
			boolean approved = jsonMessage.get("approved").getAsBoolean();
			sessionHandler.setConformity(session, approved);
			break;
		default:
			break;
		}

	}

}
