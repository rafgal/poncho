package co.com.poncho.websocket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import co.com.poncho.model.Room;
import co.com.poncho.model.Usuario;
import co.com.poncho.util.Command;

@ApplicationScoped
public class RoomSessionHandler {
	
	private Map<String, Room> rooms = new HashMap<String, Room>();
	
	public void addRoom(Room room){
		rooms.put(room.getName(), room);
	}
	
	public void removeRoom(Room room){
		for (Usuario user : room.getUsers()) {
			user.setRoom(null);
		}
		rooms.remove(room.getName());
		sendToAllConnectedSessions(room, getMessageListRooms(Command.REMOVE_ROOM));
	}
	
	public void addUserToRoom(Room room, Usuario user){
		if(!rooms.containsKey(room.getName())){
			addRoom(room);
		} 
		room.addUser(user);
		JsonObject addMessage = getRoomStatus(room);
		sendToAllConnectedSessions(room, addMessage);
	}
	
	public void removeUserToRoom(Room room, Usuario user){
		if(rooms.containsKey(room.getName())){
			user.setRoom(null);
			room.removeUser(user);
		} 
	}
	
	public Set<String> getAllRooms() {
		return rooms.keySet();
	}
	
	public Room getRoomByName(String name){
		return rooms.get(name);
	}

	private JsonObject getRoomStatus(Room room) {
		JsonObject message = new JsonObject();
		JsonObject board = new JsonObject();
		message.addProperty("comando", Command.UPDATE_ROOM.getValue());
		
		JsonArray jsonArray = new JsonArray();
		JsonObject user = new JsonObject();
		for (Usuario usu : room.getUsers()) {
			user = new JsonObject();
			user.addProperty("nombre", usu.getNombre());
			user.addProperty("voto", usu.getVoto());
			jsonArray.add(user);
		}
		int boardStatus= (room.getUsersWithVote().size()==room.getUsers().size()) ? 1 : 0;
		
		board.addProperty("boardStatus", boardStatus);
		board.add("usuarios", jsonArray);
		message.add("board", board);
		return message;
	}

	private void sendToAllConnectedSessions(Room room, JsonObject message) {
		for ( Usuario user : room.getUsers()) {
			MessageHandler.sendToSession(user.getSession(), message);
		}
	}

	public JsonObject getMessageListRooms(Command command) {
		JsonObject message = new JsonObject();
		JsonArray jsonArray = new JsonArray();
		for (String room : rooms.keySet()) {
			jsonArray.add(room);
		}
		message.addProperty("comando", command.getValue());
		message.add("salas", jsonArray);
		return message;
	}

}
