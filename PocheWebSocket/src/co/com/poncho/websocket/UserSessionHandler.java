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
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.spi.JsonProvider;
import javax.websocket.Session;

import co.com.poncho.model.Usuario;

@ApplicationScoped
public class UserSessionHandler {

   private Set<Session> sessions = new HashSet<>();
   private Set<Usuario> users = new HashSet<>();
   private Map<String, Usuario> sesionesUsuarios = new HashMap<String, Usuario>();

   public void addSession( Session session )
   {
      System.out.println( "todo bien en add session" );
      sessions.add( session );
//      for ( Usuario user : users ) {
//         JsonObject addMessage = createAddMessage( );
//         sendToSession( session, addMessage );
//      }
   }

   public void removeSession( Session session )
   {
      sessions.remove( session );
   }

   public List getUsers()
   {
      return new ArrayList<>( users );
   }

   public void addUser( Usuario user, Session session )
   {
      System.out.println( "adduser" );
      users.add( user );
      JsonObject addMessage = createAddMessage();

      sesionesUsuarios.put( session.getId(), user );

      sendToAllConnectedSessions( addMessage );
   }

   public void registerVote( int voto, int tipoVoto, Session session )
   {
      Usuario usuario = sesionesUsuarios.get( session.getId() );
      usuario.setVoto( voto );
      usuario.setTipoVoto( tipoVoto );
      JsonObject voteMessage = createVoteMessage();
      
      sendToAllConnectedSessions( voteMessage );
   }

   private JsonObject createVoteMessage()
   {
      JsonProvider provider = JsonProvider.provider();
      JsonObjectBuilder objectBuilder = provider.createObjectBuilder();
      JsonArrayBuilder arrayBuilder = provider.createArrayBuilder();

      for ( Usuario usuario : users ) {
         JsonObject voto = objectBuilder.add( "nombre", usuario.getNombre() )
            .add( "voto", usuario.getVoto() ).add( "tipoVoto", usuario.getTipoVoto() ).build();
         arrayBuilder.add( voto );
      }

      JsonObject voteMessage = objectBuilder.add( "comando", 1 ).add( "votos", arrayBuilder )
         .build();
      return voteMessage;
   }
   
   public void aprobarVotacion( Session session ) {
      int numAprobaciones = 0;
      Usuario usuario = sesionesUsuarios.get( session.getId() );
      usuario.setAceptado( true );
      for ( Usuario user : users ) {
         if ( user.isAceptado() ) {
            numAprobaciones++;
         }
      }
      if ( numAprobaciones < sesionesUsuarios.size() ) {
         JsonObject approveMessage = createApproveMessage();
         sendToAllConnectedSessions( approveMessage );
      } else {
         for ( Usuario user : users ) {
            user.setAceptado( false );
         }
         JsonObject addMessage = createAddMessage();
         sendToAllConnectedSessions( addMessage );
      }
   }
   
   private JsonObject createApproveMessage() {
      JsonProvider provider = JsonProvider.provider();
      JsonObjectBuilder objectBuilder = provider.createObjectBuilder();
      JsonArrayBuilder arrayBuilder = provider.createArrayBuilder();

      for ( Usuario usuario : users ) {
         JsonObject voto = objectBuilder.add( "nombre", usuario.getNombre() )
            .add( "aprobado", usuario.isAceptado() ).build();
         arrayBuilder.add( voto );
      }

      JsonObject voteMessage = objectBuilder.add( "comando", 2 ).add( "aprobaciones", arrayBuilder )
         .build();
      
      return voteMessage;
   }

   // public void removeUser(int id) {
   // Usuario user = getUserById(id);
   // if (user != null) {
   // users.remove(user);
   // JsonProvider provider = JsonProvider.provider();
   // JsonObject removeMessage = provider.createObjectBuilder().add("action", "remove").add("id",
   // id).build();
   // sendToAllConnectedSessions(removeMessage);
   // }
   // }

   private JsonObject createAddMessage()
   {
      JsonProvider provider = JsonProvider.provider();
      JsonObjectBuilder objectBuilder = provider.createObjectBuilder();
      JsonArrayBuilder arrayBuilder = provider.createArrayBuilder();

      for ( Usuario usuario : users ) {
         arrayBuilder.add( usuario.getNombre() );
      }

      JsonObject addMessage = objectBuilder.add( "comando", 0 ).add( "usuarios", arrayBuilder )
         .build();
      return addMessage;
   }

   private void sendToAllConnectedSessions( JsonObject message )
   {
      System.out.println( "bcast" );
      for ( Session session : sessions ) {
         sendToSession( session, message );
      }
   }

   private void sendToSession( Session session, JsonObject message )
   {
      try {
         session.getBasicRemote().sendText( message.toString() );
      }
      catch ( IOException ex ) {
         sessions.remove( session );
         Logger.getLogger( UserSessionHandler.class.getName() ).log( Level.SEVERE, null, ex );
      }
   }
}
