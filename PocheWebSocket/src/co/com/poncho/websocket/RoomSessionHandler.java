package co.com.poncho.websocket;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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
		message.addProperty("comando", Command.UPDATE_ROOM.getValue());
		
//		JsonArray jsonArray = new JsonArray();
//		
//		for (Usuario usu : room.getUsers()) {
//			jsonArray.add(new JsonParser().parse(usu.getEstado()).getAsJsonObject());
//		}
//		int boardStatus=0;
//		if(room.getUsersWithVote().size()==room.getUsers().size())
//			boardStatus=1;
//		
//		message.addProperty("boardStatus", boardStatus);
//		message.add("usuarios", jsonArray);
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
