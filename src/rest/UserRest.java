package rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.websocket.server.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.bson.Document;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import com.google.gson.Gson;
import com.mongodb.client.FindIterable;

import dbClasses.UserDatabase;
import jms.JMSQueue;
import jms.JMSStatus;
import jms.jmsDTO;
import model.Friendship;
import model.FriendshipStatus;
import model.Message;
import model.NotificationDTO;
import model.NotificationType;
import model.User;

@LocalBean
@Path("/users")
@Stateless
public class UserRest {
	
	@Inject
	private UserBean users;
	
	@Inject
	private WebSocket ws;
	
	@Inject 
	private UserDatabase userDatabase;
	
	@Inject
	private PushNotifications wsPushNotif;
	
	@Inject
	private UserBean nodeInfo;
	
	private boolean checkIfMaster() {
		return !nodeInfo.getMasterIp().equals(nodeInfo.getCurrentIp());
	}
	

	@GET
	@Path("/addActive/{userName}/ip/{ip}")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	private void getActive(@PathParam("userName") String userName, @PathParam("ip") String ip) {
		
		String returnMessage="";
	    
   	 Document found = (Document) userDatabase.getCollection().find(new Document("username", userName));
   	 if(found != null) {
   		  Gson gson = new Gson();
   	      User person = gson.fromJson(found.toJson(), User.class);   
   	      
   	      boolean active= false;
   	      for (User user2 : users.getActiveUsers()) {
				if(user2.getUsername().equals(person.getUsername())) {
					user2.setHostIp(ip);
					active=true;
				}
					
			}
   	      if(!active) {
   	    	  
   	    	users.getActiveUsers().add(person);
   	    	
   	      }
   	      returnMessage="ACTIVE";
   	      
   	        
   	 }else {
   		 returnMessage="NOUSER";
   	 }
		
		
	}
	
	
	@GET
	@Path("/getMessages/user/{userName}/from/{isGroup}/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	private Response getMessages(@PathParam("userName") String userName, @PathParam("id") String id, @PathParam("isGroup") String isG) {
		ArrayList<Message> messages = new ArrayList<>();
		Gson gson = new Gson();
		if(isG.equals("Y")) {

			FindIterable<Document> docs = userDatabase.getCollection().find(new Document("groupId", id)).sort(new Document("created_at",1));
		    for (Document doc : docs) {
	  	    	 Message m = gson.fromJson(doc.toJson(), Message.class);
	  	    	 messages.add(m);
	  	     }
		  	
			
		}else {

		  	Document or1 = new Document(
		  		  "$and", Arrays.asList(
		  		    new Document("sender", userName),
		  		    new Document("reciver", id),
		  		    new Document("groupId", "")
		  		  )
		  		);
		  	
		  	Document or2 = new Document(
			  		  "$and", Arrays.asList(
			  		    new Document("sender", id),
			  		    new Document("reciver", userName),
			  		    new Document("groupId", "")
			  		  )
			  		);
			Document query = new Document(
			  		  "$or", Arrays.asList(
			  		  or1,
			  		  or2
			  		  )
			  		);
			
		
			
			 @SuppressWarnings("unchecked")
	  	     FindIterable<Document> docs = userDatabase.getCollection().find(query).sort(new Document("created_at",1));//valjda sortira
	  	     for (Document doc : docs) {
	  	    	 Message m = gson.fromJson(doc.toJson(), Message.class);
	  	    	 messages.add(m);
	  	     }
		 	
			
		}
		
		
		return Response.status(Response.Status.OK).entity(messages).build();
		
		
	}
	
	
	private void alertFriends(String userName, boolean remove) {
		
		Gson gson = new Gson();
		
	    Document search1 = new Document();
  	     search1.append("sender", userName);
  	     search1.append("status", FriendshipStatus.ACCEPTED);
  	     
  	     @SuppressWarnings("unchecked")
  	     FindIterable<Document> docs = userDatabase.getCollection().find(search1);
  	     for (Document doc : docs) {
	      Friendship friend = gson.fromJson(doc.toJson(), Friendship.class);
	      for (User u : users.getActiveUsers()) {
	    	  if(u.getUsername().equals(friend.getReciever())) {
	    		  NotificationDTO temp = new NotificationDTO();
	    		  temp.setRecieverId(u.getUsername());
	    		  temp.setUserId(userName);
	    		  if(remove)
	    			  temp.setType(NotificationType.LOGOUT);
	    		  else
	    			  temp.setType(NotificationType.LOGIN);
	    		  wsPushNotif.pushNotification(temp);
	    	  }
			
	      }
  	    }
  	     
  	     
  	     Document search2 = new Document();
  	     search2.append("reciever", userName);
  	     search2.append("status", FriendshipStatus.ACCEPTED);
  	     
  	    @SuppressWarnings("unchecked")
 	     FindIterable<Document> docs2 = userDatabase.getCollection().find(search2);
 	     for (Document doc : docs2) {
	      Friendship friend = gson.fromJson(doc.toJson(), Friendship.class);
	      for (User u : users.getActiveUsers()) {
	    	  if(u.getUsername().equals(friend.getSender())) {
	    		  NotificationDTO temp = new NotificationDTO();
	    		  temp.setRecieverId(u.getUsername());
	    		  temp.setUserId(userName);
	    		  if(remove)
	    			  temp.setType(NotificationType.LOGOUT);
	    		  else
	    			  temp.setType(NotificationType.LOGIN);
	    		  wsPushNotif.pushNotification(temp);
	    	  }
			
	      }
 	    }
		
	}
	
	
	@POST
    @Path("/login/{userName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String login(@PathParam("userName") String userName, String password){
		String returnStr="";
	 	Document found = (Document) userDatabase.getCollection().find(new Document("username", userName));
	 	if(found != null) {
	   		  Gson gson = new Gson();
	   	      User user = gson.fromJson(found.toJson(), User.class);  
	   	      if(user.getUsername().equals(password)) {
	   	    	 
	   	    	 
		   	    	ResteasyClient client = new ResteasyClientBuilder().build();
		 			
		 			ResteasyWebTarget target = client.target(
		 					"http://" + users.getMasterIp() + ":8096/UserApp/rest/login");
		 			Response response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(user,MediaType.APPLICATION_JSON));
		 			returnStr = response.readEntity(String.class);
	   	    	 
	 			  boolean active= false;
	 	   	      for (User user2 : users.getActiveUsers()) {
	 					if(user2.getUsername().equals(user.getUsername())) {
	 						user2.setHostIp(users.getCurrentIp()); // ako ga nisi postavio smoricu se
	 						active=true;
	 					}
	 						
	 				}
	 	   	      if(!active) {
	 	   	    	  
	 	   	    	users.getActiveUsers().add(user);
	 	   	      }
	 	   	      alertFriends(userName, false);
	 			
	 			
	   	      }else
	   	    	  returnStr="WRONG PASSWORD";
	   	      
	 	}else
	 		returnStr="WRONG USERNAME";

		
		return returnStr;
	}
	
	
		@POST
	    @Path("/logout")
	    @Consumes(MediaType.APPLICATION_JSON)
	    @Produces(MediaType.TEXT_PLAIN)
	    public String logout(String userName){
			
			String returnStr="";
		 	Document found = (Document) userDatabase.getCollection().find(new Document("username", userName));
		 	if(found != null) {
		   		  Gson gson = new Gson();
		   	      User user = gson.fromJson(found.toJson(), User.class);  
		  
		   	    	ResteasyClient client = new ResteasyClientBuilder().build();
		 			
		 			ResteasyWebTarget target = client.target(
		 					"http://" + users.getMasterIp() + ":8096/UserApp/rest/logout");
		 			Response response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(user, MediaType.APPLICATION_JSON));
		 			returnStr = response.readEntity(String.class);
		 		
		 	   	      for (User user2 : users.getActiveUsers()) {
		 					if(user2.getUsername().equals(user.getUsername())) {
		 						users.getActiveUsers().remove(user2);
		 						break;
		 					}
		 						
		 				}
		 	   	   
		 	   	      alertFriends(userName, true);
		 		
		   	      
		 	}else
		 		returnStr="NO USER";

			
			return returnStr;
		 
	 }
	
	
	@DELETE
	@Path("/removeActive/{userName}")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	private void removeActive(@PathParam("userName") String userName) {
		
		String returnMessage="";
	    
   	 Document found = (Document) userDatabase.getCollection().find(new Document("username", userName));
   	 if(found != null) {
   		  Gson gson = new Gson();
   	      User person = gson.fromJson(found.toJson(), User.class);   
   	      
   	      boolean active= false;
   	      for (User user2 : users.getActiveUsers()) {
				if(user2.getUsername().equals(person.getUsername())) {
					active=true;
				}
					
			}
   	      if(active) {
   	    	users.getActiveUsers().remove(person);
   	    	returnMessage="REMOVED";
   	      }else
   	      returnMessage="NOUSER";
   	      
   	        
   	 }else {
   		 returnMessage="NOUSER";
   	 }
		
		
	}
	
	
	 	@POST
	    @Path("/register")
	    @Consumes(MediaType.APPLICATION_JSON)
	    @Produces(MediaType.TEXT_PLAIN)
	    public String register(User user) {
	 		String returnStr ="";
	 		boolean allOk= false;
	 		if(!user.getUsername().equals("") && !user.getLastName().equals("") && !user.getName().equals("") && !user.getPassword().equals(""))
	 			if(user.getUsername().length()>0 && user.getLastName().length()>0 && user.getName().length()>0 && user.getPassword().length()>0 && user.getPassword().length()>0)
	 				allOk=true;
	 		
	 		if(allOk) {
	 		ResteasyClient client = new ResteasyClientBuilder().build();
 			
	 		user.setHostIp(users.getCurrentIp());
	 		System.out.println("MASTER IP : " + users.getMasterIp()); 
	 		System.out.println("CURENT IP : " + users.getCurrentIp()); 
	 		System.out.println("http://" + users.getMasterIp() + ":8096/UserApp/rest/register");
 			ResteasyWebTarget target = client.target(
 					"http://" + users.getMasterIp() + ":8096/UserApp/rest/users/register");
 			System.out.println("DOSAO SAM TU");
 			Response response = target.request(MediaType.TEXT_PLAIN).post(Entity.entity(user, MediaType.APPLICATION_JSON));
 			returnStr = response.readEntity(String.class);
 			response.close();
	 		}else
	 			returnStr="BAD INPUT";
	 		return returnStr;
	 		
	 	}
	

	
	
	@POST
	@Path("/sendMessage")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	private void getMessage(Message m) {
		try {
			ws.forwardMessage(m);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
