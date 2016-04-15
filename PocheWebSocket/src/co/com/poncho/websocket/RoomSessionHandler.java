package co.com.poncho.websocket;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.enterprise.context.ApplicationScoped;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import co.com.poncho.model.Room;
import co.com.poncho.model.Usuario;
import co.com.poncho.util.Command;

@ApplicationScoped
public class RoomSessionHandler {
	
	private Map<String, Room> rooms = new HashMap<String, Room>();
	Timer timer;
	
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
	
	public void removeUserToRoom(Usuario user){
		Room room = user.getRoom();
		if(rooms.containsKey(room.getName())){
			user.setRoom(null);
			room.removeUser(user);
		}
		sendToAllConnectedSessions(room, getRoomStatus(room));
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
			user.addProperty("tipoVoto", usu.getTipoVoto());
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
	
	public void registerVote(float voto, int tipoVoto, Usuario usuario) {
		usuario.setVoto(voto);
		usuario.setTipoVoto(tipoVoto);
		Room room = usuario.getRoom();
		room.addVote(usuario);
		
		Set<Usuario> usersWithVote = room.getUsersWithVote();
		System.out.println("Votos actuales: " + usersWithVote.size());
		
		if (usersWithVote.size() == 1) {
			startTimer(room);
		}
		
		JsonObject voteMessage = getRoomStatus(room);
		sendToAllConnectedSessions(room, voteMessage);
	}
	
	private void startTimer(Room room) {
		timer = new Timer();
        timer.schedule(new TimeoutTask(room), 10*1000);
	}
	
	class TimeoutTask extends TimerTask {
		
		private Room room;
		
		public TimeoutTask(Room _room) {
			room = _room;
		}
		
        public void run() {
            System.out.println("Time's up!");
            
            JsonObject voteMessage = getRoomStatus(room);
            System.out.println("Estado de la sala: " + voteMessage.getAsJsonObject("board").toString());
            voteMessage.getAsJsonObject("board").addProperty("boardStatus", 1);
            sendToAllConnectedSessions(room, voteMessage);
            
            timer.cancel();
        }
    }
	
	public void setConformity(Usuario usuario, boolean approved) {
		Room room = usuario.getRoom();
		usuario.setAceptado(approved);
		
		if (room.getUserAccpted() >= room.getUsers().size()) {
			room.resetRoom();
		}
		
		JsonObject voteMessage = getRoomStatus(room);
		sendToAllConnectedSessions(room, voteMessage);
	}

}
