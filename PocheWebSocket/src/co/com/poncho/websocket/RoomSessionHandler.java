package co.com.poncho.websocket;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.Session;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import co.com.poncho.model.Room;
import co.com.poncho.model.Usuario;

@ApplicationScoped
public class RoomSessionHandler {
	
	private Map<String, Room> rooms = new TreeMap<String, Room>();
	
	public void addRoom(Room room){
		rooms.put(room.getName(), room);
	}
	
	public void removeRoom(Room room){
		rooms.remove(room.getName());
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
		if(!rooms.containsKey(room.getName())){
			addRoom(room);
		} 
		room.addUser(user);
	}
	
	public Room getRoomByName(String name){
		return rooms.get(name);
	}

	private JsonObject getRoomStatus(Room room) {
		JsonObject jsonObject = new JsonObject();

		JsonArray jsonArray = new JsonArray();
		
		for (Usuario usu : room.getUsers()) {
			jsonArray.add(new JsonParser().parse(usu.getEstado()).getAsJsonObject());
		}
		int boardStatus=0;
		if(room.getUsersWithVote().size()==room.getUsers().size())
			boardStatus=1;
		jsonObject.addProperty("boardStatus", boardStatus);
		jsonObject.add("usuarios", jsonArray);
		return jsonObject;
	}

	private void sendToAllConnectedSessions(Room room, JsonObject message) {
		
		for ( Usuario user : room.getUsers()) {
			sendToSession(user.getSession(), message);
		}
	}

	private void sendToSession(Session session, JsonObject message) {
		try {
			session.getBasicRemote().sendText(message.toString());
		} catch (IOException ex) {
			Logger.getLogger(UserSessionHandler.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void showListRooms(Session session) {
		JsonObject message = new JsonObject();

		JsonArray jsonArray = new JsonArray();
		jsonArray.add("prueba");
		for (String room : rooms.keySet()) {
			jsonArray.add(room);
		}
		message.add("salas", jsonArray);
		try {
			System.out.println(message.toString());
			session.getBasicRemote().sendText(message.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
