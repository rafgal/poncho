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
import co.com.poncho.util.Command;

@ServerEndpoint("/ponchito")
public class Websocket {

	@OnClose
	public void onConnectionClose(Session session) {
		sessionHandler.removeSession(session);
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
		Command command=Command.fromInt(comando);
		Usuario user = null;
		switch (command) {
		case REGISTER_USER:
			System.out.println("Registrar usuario");
			String nombre = jsonMessage.get("nombre").getAsString();
			String roomName = jsonMessage.get("room").getAsString();
			Room room = roomsHandler.getRoomByName(roomName);
			
			user = new Usuario(nombre, session);
			sessionHandler.addUser(user, session);
			if(room == null){
				room = new Room(roomName, user);
				roomsHandler.addRoom(room);
				sessionHandler.sendToAllConnectedSessionsWithoutUser(roomsHandler.getMessageListRooms(Command.ROOMS));
			}
			user.setRoom(room);
			roomsHandler.addUserToRoom(room, user);
			
			break;
		case VOTE:
			System.out.println("Registrar voto");
			JsonObject vote = jsonMessage.get("vote").getAsJsonObject();
			float voto = vote.get("value").getAsFloat();
			int tipoVoto = vote.get("type").getAsInt();
			user = sessionHandler.getUserBySession(session);
			roomsHandler.registerVote(voto, tipoVoto, user);
			break;
		case LOGOUT:
			System.out.println("salir de la sala");
			user = sessionHandler.getUserBySession(session);
			sessionHandler.removeUserSession(session);
			roomsHandler.removeUserToRoom(user);
			sessionHandler.sendToAllConnectedSessionsWithoutUser(roomsHandler.getMessageListRooms(Command.ROOMS));
			break;
		case CLEAR_ROOM:
			System.out.println("Volver a votar");
			user = sessionHandler.getUserBySession(session);
			roomsHandler.resetRoom(user.getRoom());
			break;
		default:
			break;
		}

	}

}
