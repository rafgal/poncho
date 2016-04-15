package co.com.poncho.websocket;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.Session;

import com.google.gson.JsonObject;

import co.com.poncho.model.Room;
import co.com.poncho.model.Usuario;
import co.com.poncho.util.Command;

@ApplicationScoped
public class UserSessionHandler {
	
	@Inject
	private RoomSessionHandler roomsHandler;

	private Map<Session, Usuario> sesionesUsuarios = new HashMap<Session, Usuario>();
	private Map<String, Session> sessionsId=new TreeMap<String, Session>();


	public void addSession(Session session) {
		sesionesUsuarios.put(session, null);
		MessageHandler.sendToSession(session, roomsHandler.getMessageListRooms(Command.ROOMS));
	}

	public void removeSession(Session session) {
		Usuario user = sesionesUsuarios.remove(session);
		sessionsId.remove(user.getSession().getId());
		if(user != null && user.getRoom() != null){
			Room room = user.getRoom();
			if(user.equals(room.getOwner())){
				roomsHandler.removeRoom(room);
				sendToAllConnectedSessions(roomsHandler.getMessageListRooms(Command.ROOMS));
				
			} else {
				roomsHandler.removeUserToRoom(room, user);
			}
		}
	}

	public void addUser(Usuario user, Session session) {
		sesionesUsuarios.put(session, user);
		sessionsId.put(session.getId(), session);
	}
	
	public void recoverUser(String oldSessionId, Session newSession) throws SessionNotFoundException{
		Session oldSession = sessionsId.get(oldSessionId);
		if(oldSession==null){
			throw new SessionNotFoundException();
		}
		System.out.println("recupera sesion");
		Usuario usuario = sesionesUsuarios.remove(oldSession);
		usuario.setSession(newSession);
		System.out.println("usuario "+usuario.getNombre());
		sessionsId.remove(oldSession.getId());
		sessionsId.put(newSession.getId(),newSession);
		sesionesUsuarios.put(newSession, usuario);
		roomsHandler.sendToAllConnectedSessions(usuario.getRoom(), roomsHandler.getRoomStatus(usuario.getRoom()));
		roomsHandler.sendSessionIdToUser(usuario);

	}

//	public void registerVote(float voto, int tipoVoto, Session session) {
//		Usuario usuario = sesionesUsuarios.get(session);
//		usuario.setVoto(voto);
//		usuario.setTipoVoto(tipoVoto);
//		usersWithVote.add(usuario);
//		JsonObject voteMessage = getBoardStatus();
//		sendToAllConnectedSessions(voteMessage);
//	}
//
//	private JsonObject createVoteMessage() {
//		JsonProvider provider = JsonProvider.provider();
//		JsonObjectBuilder objectBuilder = provider.createObjectBuilder();
//		JsonArray arrayBuilder = new JsonArray();
//
//		for (Usuario usuario : users) {
//			JsonObject voto = new JsonObject();
//			voto.addProperty("nombre", usuario.getNombre());
//			voto.addProperty("voto", usuario.getVoto());
//			voto.addProperty("tipoVoto", usuario.getTipoVoto());
//			arrayBuilder.add(voto);
//		}
//
//		JsonObject voteMessage = new JsonObject();
//		voteMessage.addProperty("comando", 1);
//		voteMessage.add("votos", arrayBuilder);
//		return voteMessage;
//	}
//
//	public void setConformity(Session session, boolean approved) {
//		int numAprobaciones = 0;
//		Usuario usuario = sesionesUsuarios.get(session);
//		usuario.setAceptado(approved);
//		for (Usuario user : users) {
//			if (user.isAceptado()) {
//				numAprobaciones++;
//			}
//		}
//		if (numAprobaciones >= sesionesUsuarios.size()) {
//			
//			for (Usuario user : users) {
//				user.setAceptado(false);
//				user.setVoto(-1);
//			}
//			usersWithVote.clear();
//		}
//		JsonObject addMessage = getBoardStatus();
//		sendToAllConnectedSessions(addMessage);
//	}
//
//	private com.google.gson.JsonObject getBoardStatus() {
//		com.google.gson.JsonObject jsonObject = new com.google.gson.JsonObject();
//
//		JsonArray jsonArray = new JsonArray();
//
//		for (Usuario usu : users) {
//			jsonArray.add(new JsonParser().parse(usu.getEstado()).getAsJsonObject());
//		}
//		int boardStatus=0;
//		if(usersWithVote.size()==users.size())
//			boardStatus=1;
//		jsonObject.addProperty("boardStatus", boardStatus);
//		jsonObject.add("usuarios", jsonArray);
//		return jsonObject;
//	}
//
	public void sendToAllConnectedSessions(JsonObject message) {
		Set<Session> sinUser = new HashSet<Session>();
		sinUser.addAll(sesionesUsuarios.keySet());
		sinUser.removeAll(sesionesUsuarios.keySet());
		MessageHandler.sendToAllConnectedSessions(sinUser, message);
	}
//
//	private void sendToSession(Session session, JsonObject message) {
//		try {
//			session.getBasicRemote().sendText(message.toString());
//		} catch (IOException ex) {
//			sessions.remove(session);
//			Logger.getLogger(UserSessionHandler.class.getName()).log(Level.SEVERE, null, ex);
//		}
//	}
}
