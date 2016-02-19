package co.com.poncho.websocket;

import java.util.logging.Logger;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.ibm.wsdl.util.StringUtils;

//@ApplicationScoped
@ServerEndpoint("/ponchito")
public class Websocket {
	
	private final Logger logger = Logger.getLogger(this.getClass().getName());
	 
    @OnOpen
    public void onConnectionOpen(Session session) {
        logger.info("Connection opened ... " + session.getId());
    }
 
    @OnMessage
    public String onMessage(String message) {
    	System.out.println("me lo pela " + message);
        return message;
    }
 
    @OnClose
    public void onConnectionClose(Session session) {
        logger.info("Connection close .... " + session.getId());
    }
	
	/*

	@Inject
    private UserSessionHandler sessionHandler;
	
	@OnOpen
	public void open(Session session) {
		System.out.println("open session ");
		sessionHandler.addSession(session);
	}

	@OnClose
	public void close(Session session) {
		sessionHandler.removeSession(session);
	}

	@OnError
	public void onError(Throwable error) {
		System.out.println("Error en ponchitooooooooooo");
		error.printStackTrace();
	}

	@OnMessage
	public void handleMessage(String message, Session session) {
		System.out.println("call message");
		try (JsonReader reader = Json.createReader(new StringReader(message))) {
            JsonObject jsonMessage = reader.readObject();

            if ("add".equals(jsonMessage.getString("action"))) {
            	User user = new User();
            	user.setName(jsonMessage.getString("name"));
                sessionHandler.addUser(user);
            }

            if ("remove".equals(jsonMessage.getString("action"))) {
                int id = (int) jsonMessage.getInt("id");
                sessionHandler.removeUser(id);
            }

            if ("toggle".equals(jsonMessage.getString("action"))) {
                int id = (int) jsonMessage.getInt("id");
                sessionHandler.toggleUser(id);
            }
        }
	}*/
}
