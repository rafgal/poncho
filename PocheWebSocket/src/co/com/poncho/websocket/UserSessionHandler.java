package co.com.poncho.websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonObjectBuilder;
import javax.json.spi.JsonProvider;
import javax.websocket.Session;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import co.com.poncho.model.Usuario;

@ApplicationScoped
public class UserSessionHandler {

	private Set<Session> sessions = new HashSet<>();
	private Set<Usuario> users = new HashSet<>();
	private Set<Usuario> usersWithVote=new HashSet<>();
	private Map<String, Usuario> sesionesUsuarios = new HashMap<String, Usuario>();

	public void addSession(Session session) {
		System.out.println("todo bien en add session");
		sessions.add(session);
		System.out.println(sessions);

	}

	public void removeSession(Session session) {
		System.out.println("removeeeee");
		sessions.remove(session);
		Usuario usuario = sesionesUsuarios.get(session.getId());
		users.remove(usuario);
		usersWithVote.remove(usuario);
		sesionesUsuarios.remove(session.getId());
		JsonObject addMessage = getBoardStatus();
		sendToAllConnectedSessions(addMessage);
	}

	public List getUsers() {
		return new ArrayList<>(users);
	}

	public void addUser(Usuario user, Session session) {
		System.out.println("adduser");
		users.add(user);
		JsonObject addMessage = getBoardStatus();

		sesionesUsuarios.put(session.getId(), user);

		sendToAllConnectedSessions(addMessage);
	}

	public void registerVote(int voto, int tipoVoto, Session session) {
		Usuario usuario = sesionesUsuarios.get(session.getId());
		usuario.setVoto(voto);
		usuario.setTipoVoto(tipoVoto);
		usersWithVote.add(usuario);
		JsonObject voteMessage = getBoardStatus();
		sendToAllConnectedSessions(voteMessage);
	}

	private JsonObject createVoteMessage() {
		JsonProvider provider = JsonProvider.provider();
		JsonObjectBuilder objectBuilder = provider.createObjectBuilder();
		JsonArray arrayBuilder = new JsonArray();

		for (Usuario usuario : users) {
			JsonObject voto = new JsonObject();
			voto.addProperty("nombre", usuario.getNombre());
			voto.addProperty("voto", usuario.getVoto());
			voto.addProperty("tipoVoto", usuario.getTipoVoto());
			arrayBuilder.add(voto);
		}

		JsonObject voteMessage = new JsonObject();
		voteMessage.addProperty("comando", 1);
		voteMessage.add("votos", arrayBuilder);
		return voteMessage;
	}

	public void setConformity(Session session, boolean approved) {
		int numAprobaciones = 0;
		Usuario usuario = sesionesUsuarios.get(session.getId());
		usuario.setAceptado(approved);
		for (Usuario user : users) {
			if (user.isAceptado()) {
				numAprobaciones++;
			}
		}
		if (numAprobaciones >= sesionesUsuarios.size()) {
			
			for (Usuario user : users) {
				user.setAceptado(false);
				user.setVoto(-1);
			}
			usersWithVote.clear();
		}
		JsonObject addMessage = getBoardStatus();
		sendToAllConnectedSessions(addMessage);
	}

//	 public void removeUser(int id) {
//	 Usuario user = getUserById(id);
//	 if (user != null) {
//	 users.remove(user);
//	 JsonProvider provider = JsonProvider.provider();
//	 JsonObject removeMessage = provider.createObjectBuilder().add("action",
//	 "remove").add("id",
//	 id).build();
//	 sendToAllConnectedSessions(removeMessage);
//	 }
//	 }

	private com.google.gson.JsonObject getBoardStatus() {
		com.google.gson.JsonObject jsonObject = new com.google.gson.JsonObject();

		JsonArray jsonArray = new JsonArray();

		for (Usuario usu : users) {
			jsonArray.add(new JsonParser().parse(usu.getEstado()).getAsJsonObject());
		}
		int boardStatus=0;
		if(usersWithVote.size()==users.size())
			boardStatus=1;
		jsonObject.addProperty("boardStatus", boardStatus);
		jsonObject.add("usuarios", jsonArray);
		return jsonObject;
	}

	private void sendToAllConnectedSessions(JsonObject message) {
		for (Session session : sessions) {
			sendToSession(session, message);
		}
	}

	private void sendToSession(Session session, JsonObject message) {
		try {
			session.getBasicRemote().sendText(message.toString());
		} catch (IOException ex) {
			sessions.remove(session);
			Logger.getLogger(UserSessionHandler.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
