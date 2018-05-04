package rest;

import java.io.IOException;

import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

import org.bson.Document;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import dbClasses.FriendshipDatabase;
import dbClasses.GroupDatabase;
import dbClasses.MessageDatabase;
import dbClasses.UserDatabase;
import encoderDecoder.MessageDecoder;
import encoderDecoder.MessageEncoder;
import encoderDecoder.NotificationDTOEncoder;
import model.FriendshipStatus;
import model.Group;
import model.Message;
import model.User;

@LocalBean
@Singleton
@ServerEndpoint(
		
	    value = "/chat/{user}/",
        decoders = {MessageDecoder.class},
	    encoders = {MessageEncoder.class}
		
		)
public class WebSocket {
	
	@Inject
	private UserDatabase userDatabase;
	
	@Inject
	private GroupDatabase groupDatabase;
	
	@Inject
	private FriendshipDatabase friendDatabase;
	
	@Inject 
	private MessageDatabase messageDb;
	
	private Session s;
	
	@OnMessage
	public void message(Message m,  Session client) throws IOException, EncodeException {
	 System.out.println("message: " + m.getContent());
	 
	 if(m.getGroupId().equals("")) {
		 System.out.println("SALJE PRIVATNU");
		 sendPrivate(client, m);
	 	}else {
	 		 System.out.println("SALJE GRUPNU");
	 		sendGroup(client, m);
	 	}
	 }
	
	private boolean isFriend(Message m) {
		Document searchBy = new Document();
        searchBy.append("sender", m.getSender());
        searchBy.append("reciever", m.getReciver());
        searchBy.append("status", FriendshipStatus.ACCEPTED);
        Document found = (Document) friendDatabase.getCollection().find(searchBy).first();
        
        return found!=null;
		
	}
	
	
	private void sendPrivate(Session client, Message m) throws IOException {
		 boolean poslao = false;
		 
		 System.out.println("UPAO U PRIVATNE");
		 if(m.getGroupId().equals("")) {
			if(!isFriend(m))
				return;
		 }
		 
		 
		  ObjectMapper mapper = new ObjectMapper();
         
              String json = mapper.writeValueAsString(m);

              messageDb.getCollection().insertOne(Document.parse(json));
		 
		 
		 for (Session peer : client.getOpenSessions()) {
		        	if(peer.getUserProperties().get("user").toString().equals(m.getReciver())) {
		        		try {
		        			 System.out.println("SALJE PREKO REMOTE-A");
		        			
							peer.getBasicRemote().sendObject(m);
						} catch (EncodeException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		        		poslao = true;
		        	}
		        if(!poslao) {
		        	System.out.println("SALJE PREKO RESTEASY");
		        	
		        	 Document found = (Document) userDatabase.getCollection().find(new Document("username", m.getReciver())).first();
		        	 if(found != null) {
		        		  Gson gson = new Gson();
		        		  User person = gson.fromJson(found.toJson(), User.class);   
		        		  ResteasyClient rClient = new ResteasyClientBuilder().build();
		  				
		        		  System.out.println( "SALJE SE NA "+	"http://" + person.getHostIp() + ":8096/ChatApp/users/sendMessage/");
		        		  ResteasyWebTarget target = rClient.target(
	      						"http://" + person.getHostIp() + ":8096/ChatApp/users/sendMessage");
		  				
		        		  Response response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(m, MediaType.APPLICATION_JSON));
		        	 }
		        	     
		        }
		           
		 	}
	}
	
	
	private void sendGroup(Session client, Message m) throws IOException {
		 Document found = (Document) groupDatabase.getCollection().find(new Document("id", m.getGroupId())).first();
		 if(found !=null) {
			Gson gson = new Gson();
   	      	Group group = gson.fromJson(found.toJson(), Group.class);   
   	      	for (User u : group.getUsers()) {
   	      		System.out.println("SALJE OVOJ OSOBI : "+ u.getUsername());
   	      		
   	      		m.setReciver(u.getUsername());
				sendPrivate(client, m);
			}
		 }
	}
	
	
	public void forwardMessage(Message m) throws IOException {
		
		 for (Session peer : s.getOpenSessions()) {
	        	if(peer.getUserProperties().get("user").toString().equals(m.getReciver())) {
	        		try {
						peer.getBasicRemote().sendObject(m);
					} catch (EncodeException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        	
	        }
		 }
		
	}
	
	
	  @OnOpen
	  public void userConnectedCallback(@PathParam("user") String user, Session s) {
		  s.getUserProperties().put("user", user);
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
