package co.com.poncho.websocket;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.Session;

import com.google.gson.JsonObject;

import co.com.poncho.model.Usuario;
import co.com.poncho.util.Command;

@ApplicationScoped
public class UserSessionHandler {
	
	@Inject
	private RoomSessionHandler roomsHandler;

	private Set<Session> sessions = new HashSet<>();
	private Map<Session, Usuario> sesionesUsuarios = new HashMap<Session, Usuario>();

	public void addSession(Session session) {
		sessions.add(session);
		MessageHandler.sendToSession(session, roomsHandler.getMessageListRooms(Command.ROOMS));
	}

	public void removeSession(Session session) {
		sessions.remove(session);
		removeUserSession(session);
	}
	
	public void removeUserSession(Session session) {
		sesionesUsuarios.remove(session);
	}

	public void addUser(Usuario user, Session session) {
		sesionesUsuarios.put(session, user);
	}
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

	public void sendToAllConnectedSessionsWithoutUser(JsonObject message) {
		Set<Session> sinUser = new HashSet<Session>();
		sinUser.addAll(sessions);
		sinUser.removeAll(sesionesUsuarios.keySet());
		MessageHandler.sendToAllConnectedSessions(sinUser, message);
	}
	
	public Usuario getUserBySession(Session session){
		return sesionesUsuarios.get(session);
	}
}
