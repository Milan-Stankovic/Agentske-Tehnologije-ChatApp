package rest;

import java.io.IOException;

import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import dbClasses.FriendshipDatabase;
import dbClasses.GroupDatabase;
import dbClasses.UserDatabase;
import encoderDecoder.MessageDecoder;
import encoderDecoder.NotificationDTODecoder;
import encoderDecoder.NotificationDTOEncoder;
import model.Message;
import model.NotificationDTO;

@LocalBean
@Singleton
@ServerEndpoint(
		
	    value = "/notification/{user}",
        decoders = {NotificationDTODecoder.class},
	    encoders = {NotificationDTOEncoder.class}
		
		)
public class PushNotifications {
	
	
	@Inject
	private UserDatabase userDatabase;
	
	@Inject
	private GroupDatabase groupDatabase;
	
	@Inject
	private FriendshipDatabase friendDatabase;
	
	private Session s;
	
	@OnMessage
	public void message(NotificationDTO n,  Session client) throws IOException, EncodeException {
	 //System.out.println("message: " + n.getType());
		
		switch (n.getType()) {
		case ACCEPTED:
			System.out.println("ACCEPTED : " + n.getUserId());
			
			break;
		case GROUPADD:
			System.out.println("ADDED TO GROUP : " + n.getGroupId());
			
			break;
		case GROUPREMOVE:
			System.out.println("REMOVED FROM GROUP : " + n.getGroupId());
	
			break;
		case LOGIN:
			System.out.println("LOGGED IN : " + n.getUserId() );
	
			break;
		case LOGOUT:
			System.out.println("LOGGED OUT : " + n.getUserId());
	
			break;
		case REMOVED:
			System.out.println("REMOVED FRIEND : " + n.getUserId());
			
			break;
			
		case GROUPNEWUSER:
			System.out.println("ADDED NEW USER : " + n.getUserId());
			
			break;
			
		case PENDING:
			System.out.println("PENDING FRIEND : " + n.getUserId());
			
			break;

		default:
			break;
		}
	 
	 }
	
	public void pushNotification(NotificationDTO n) {
		
		System.out.println(n);
		System.out.println(s);
		
		 for (Session peer : s.getOpenSessions()) {
	        	if(peer.getUserProperties().get("user").toString().equals(n.getRecieverId())) {

	        		peer.getAsyncRemote().sendText(n.toString());
	        		peer.getAsyncRemote().sendObject(n);
	        		
	        	
	        }
		 }
	}
	
	
	 @OnOpen
	 public void userConnectedCallback(@PathParam("user") String user, Session s) {
		 
		 System.out.println("UPAO JE U OPEN");
		  s.getUserProperties().put("user", user);
		  System.out.println("Ovo je user : " + user);
		  System.out.println(s);
		  this.s= s;
		  
	    }
	 
	 @OnClose
	 public void closeConnection(@PathParam("user") String user, Session s ) {
		 for (Session temp : s.getOpenSessions()) {
			if(s.getUserProperties().get("user").equals(user)) {
				s.getOpenSessions().remove(temp);
				break;
			}
		}
		 
	 }
	 
	

}
