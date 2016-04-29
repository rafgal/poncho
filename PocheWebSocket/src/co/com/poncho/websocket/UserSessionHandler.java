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

import co.com.poncho.model.Usuario;
import co.com.poncho.util.Command;

@ApplicationScoped
public class UserSessionHandler {

	@Inject
	private RoomSessionHandler roomsHandler;

	private Map<Session, Usuario> sesionesUsuarios = new HashMap<Session, Usuario>();
	private Map<String, Session> sessionsId = new TreeMap<String, Session>();

	public void addSession(Session session) {
		sesionesUsuarios.put(session, null);
		MessageHandler.sendToSession(session, roomsHandler.getMessageListRooms(Command.ROOMS));
	}

	public void removeSession(Session session) {
		Usuario user = sesionesUsuarios.remove(session);
		sessionsId.remove(user.getSession().getId());
		removeUserSession(session);

	}

	public void removeUserSession(Session session) {
		sesionesUsuarios.remove(session);
	}

	public void addUser(Usuario user, Session session) {
		sesionesUsuarios.put(session, user);
		sessionsId.put(session.getId(), session);
	}

	public void recoverUser(String oldSessionId, Session newSession) throws SessionNotFoundException {
		Session oldSession = sessionsId.get(oldSessionId);
		if (oldSession == null) {
			throw new SessionNotFoundException();
		}
		sessionsId.remove(oldSession.getId());
		sessionsId.put(newSession.getId(), newSession);
		System.out.println("recupera sesion");
		Usuario usuario = sesionesUsuarios.remove(oldSession);
		if (usuario != null) {
			usuario.setSession(newSession);
			System.out.println("usuario " + usuario.getNombre());
			sesionesUsuarios.put(newSession, usuario);
			roomsHandler.sendToAllConnectedSessions(usuario.getRoom(), roomsHandler.getRoomStatus(usuario.getRoom()));
			roomsHandler.sendSessionIdToUser(usuario);
		}
	}
	//
	// private JsonObject createVoteMessage() {
	// JsonProvider provider = JsonProvider.provider();
	// JsonObjectBuilder objectBuilder = provider.createObjectBuilder();
	// JsonArray arrayBuilder = new JsonArray();
	//
	// for (Usuario usuario : users) {
	// JsonObject voto = new JsonObject();
	// voto.addProperty("nombre", usuario.getNombre());
	// voto.addProperty("voto", usuario.getVoto());
	// voto.addProperty("tipoVoto", usuario.getTipoVoto());
	// arrayBuilder.add(voto);
	// }
	//
	// JsonObject voteMessage = new JsonObject();
	// voteMessage.addProperty("comando", 1);
	// voteMessage.add("votos", arrayBuilder);
	// return voteMessage;
	// }
	//

	public void sendToAllConnectedSessionsWithoutUser(JsonObject message) {
		Set<Session> sinUser = new HashSet<Session>();
		sinUser.addAll(sesionesUsuarios.keySet());
		sinUser.removeAll(sesionesUsuarios.keySet());
		MessageHandler.sendToAllConnectedSessions(sinUser, message);
	}

	public Usuario getUserBySession(Session session) {
		return sesionesUsuarios.get(session);
	}
}
