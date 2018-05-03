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

import org.bson.Document;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import com.google.gson.Gson;

import dbClasses.FriendshipDatabase;
import dbClasses.GroupDatabase;
import dbClasses.UserDatabase;
import encoderDecoder.MessageDecoder;
import model.FriendshipStatus;
import model.Group;
import model.Message;
import model.User;

@LocalBean
@Singleton
@ServerEndpoint(
		
	    value = "/chat/{user}/",
        decoders = {MessageDecoder.class}
		
		)
public class WebSocket {
	
	@Inject
	private UserDatabase userDatabase;
	
	@Inject
	private GroupDatabase groupDatabase;
	
	@Inject
	private FriendshipDatabase friendDatabase;
	
	private Session s;
	
	@OnMessage
	public void message(Message m,  Session client) throws IOException, EncodeException {
	 System.out.println("message: " + m.getContent());
	 
	 if(m.getGroupId().equals("")) {
		 sendPrivate(client, m);
	 	}else {
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
		 
		 if(m.getGroupId().equals("")) {
			if(!isFriend(m))
				return;
		 }
		 
		 for (Session peer : client.getOpenSessions()) {
		        	if(peer.getUserProperties().get("user").toString().equals(m.getReciver())) {
		        		peer.getBasicRemote().sendText(m.getContent());
		        		poslao = true;
		        	}
		        if(!poslao) {
		        	
		        	 Document found = (Document) userDatabase.getCollection().find(new Document("username", m.getReciver()));
		        	 if(found != null) {
		        		  Gson gson = new Gson();
		        		  User person = gson.fromJson(found.toJson(), User.class);   
		        		  ResteasyClient rClient = new ResteasyClientBuilder().build();
		  				
		        		  ResteasyWebTarget target = rClient.target(
	      						"http://" + person.getHostIp() + ":8096/ChatApp/users/sendMessage/");
		  				
		  				Response response = target.request().post(Entity.entity( m , "application/vnd.com.demo.user-management.user+xml;charset=UTF-8;version=1"));
		        	 }
		        	     
		        }
		           
		 	}
	}
	
	
	private void sendGroup(Session client, Message m) throws IOException {
		 Document found = (Document) groupDatabase.getCollection().find(new Document("id", m.getGroupId()));
		 if(found !=null) {
			Gson gson = new Gson();
   	      	Group group = gson.fromJson(found.toJson(), Group.class);   
   	      	for (User u : group.getUsers()) {
   	      		m.setReciver(u.getUsername());
				sendPrivate(client, m);
			}
		 }
	}
	
	
	public void forwardMessage(Message m) throws IOException {
		
		 for (Session peer : s.getOpenSessions()) {
	        	if(peer.getUserProperties().get("user").toString().equals(m.getReciver())) {
	        		peer.getBasicRemote().sendText(m.getContent());
	        	
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
