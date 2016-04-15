package co.com.poncho.websocket;

import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import co.com.poncho.model.Room;
import co.com.poncho.model.Usuario;
import co.com.poncho.util.Command;

@ServerEndpoint("/ponchito")
public class Websocket {

	@OnClose
	public void onConnectionClose(Session session) {
		//sessionHandler.removeSession(session);
	}

	@Inject
	private UserSessionHandler sessionHandler;

	@Inject
	private RoomSessionHandler roomsHandler;

	@OnOpen
	public void open(Session session) {
		sessionHandler.addSession(session);
	}

	@OnMessage
	public void onMessage(String message, Session session) {
		JsonParser parser = new JsonParser();
		JsonObject jsonMessage = parser.parse(message).getAsJsonObject();

		int comando = jsonMessage.get("comando").getAsInt();
		Command command = Command.fromInt(comando);
		switch (command) {
		case REGISTER_USER:
			System.out.println("Registrar usuario");
			JsonElement ponchoSessionIdElement = jsonMessage.get("ponchoSessionId");
			if (ponchoSessionIdElement == null) {
				String nombre = jsonMessage.get("nombre").getAsString();
				String roomName = jsonMessage.get("room").getAsString();
				Room room = roomsHandler.getRoomByName(roomName);

				Usuario user = new Usuario(nombre, session);
				sessionHandler.addUser(user, session);
				if (room == null) {
					room = new Room(roomName, user);
					roomsHandler.addRoom(room);
					sessionHandler.sendToAllConnectedSessions(roomsHandler.getMessageListRooms(Command.ROOMS));
				}
				user.setRoom(room);
				roomsHandler.addUserToRoom(room, user);
			} else {
				try {
					System.out.println("recover usuario");
					String ponchoSessionId = ponchoSessionIdElement.getAsString();
					System.out.println("session id found "+ponchoSessionId);
					sessionHandler.recoverUser(ponchoSessionId, session);
				} catch (SessionNotFoundException e) {
					System.out.println("session not found exc");
					MessageHandler.sendToSession(session, roomsHandler.getMessageListRooms(Command.ROOMS));
				}
			}
			break;
		// case VOTE:
		// System.out.println("Registrar voto");
		// JsonObject vote = jsonMessage.get("vote").getAsJsonObject();
		// float voto = vote.get("value").getAsFloat();
		// int tipoVoto = vote.get("type").getAsInt();
		// sessionHandler.registerVote(voto, tipoVoto, session);
		// break;
		// case EVAL_RESULTS:
		// System.out.println("Evaluar votaci�n");
		// boolean approved = jsonMessage.get("approved").getAsBoolean();
		// sessionHandler.setConformity(session, approved);
		// break;
		default:
			break;
		}

	}

}
