package rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.bson.Document;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.mongodb.client.FindIterable;

import dbClasses.FriendshipDatabase;
import dbClasses.GroupDatabase;
import dbClasses.MessageDatabase;
import dbClasses.UserDatabase;
import model.Friendship;
import model.FriendshipStatus;
import model.Group;
import model.Message;
import model.NotificationDTO;
import model.NotificationType;
import model.User;
import rest.dto.ErrorDTO;

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
	private FriendshipDatabase friendDb;
	
	
	@Inject
	private MessageDatabase messageDb;
	
	@Inject
	private GroupDatabase groupDb;

	
	@Inject
	private UserBean nodeInfo;
	
	private boolean checkIfMaster() {
		return !nodeInfo.getMasterIp().equals(nodeInfo.getCurrentIp());
	}

	
	
	@GET
    @Path("/test")
    @Produces(MediaType.TEXT_PLAIN)
    public String Test(){


        return "TEST!";
    }
	
	@GET
	@Path("/group/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public Response notify(@PathParam("id") String grid) {
		Document searchBy = new Document("id", grid);
		Document found = (Document)groupDb.getCollection().find(searchBy).first();
		
		Gson gson = new Gson();
 	    Group person = gson.fromJson(found.toJson(), Group.class);
		if(found!=null) return Response.status(Response.Status.OK).entity(person).build();
		
		return Response.status(Response.Status.NOT_FOUND).entity(new ErrorDTO("No such group.")).build();
	
	}
      
	
	
	
	@GET
	@Path("/notifyFriend/{userName}/firend/{user2}/{tip}")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public void notify(@PathParam("userName") String userName, @PathParam("user2") String user2, @PathParam("tip") String tip) {
	
		 System.out.println("RADI NOTIFY FRIEND U CHAT APPU");
		  NotificationDTO temp = new NotificationDTO();
		  temp.setRecieverId(user2);
		  temp.setUserId(userName);
		  if(!tip.equals("LOGIN"))
			  temp.setType(NotificationType.LOGOUT);
		  else
			  temp.setType(NotificationType.LOGIN);
		  wsPushNotif.pushNotification(temp);
		
	}
	
	
	
	

	@GET
	@Path("/addActive/{userName}/ip/{ip}")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public void getActive(@PathParam("userName") String userName, @PathParam("ip") String ip) {
		
		String returnMessage="";
	    
   	 Document found = (Document) userDatabase.getCollection().find(new Document("username", userName)).first();
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
	public Response getMessages(@PathParam("userName") String userName, @PathParam("id") String id, @PathParam("isGroup") String isG) {
		ArrayList<Message> messages = new ArrayList<>();
		Gson gson = new Gson();
		if(isG.equals("Y")) {

			FindIterable<Document> docs = messageDb.getCollection().find(new Document("groupId", id)).sort(new Document("created_at",1));
		    for (Document doc : docs) {
	  	    	 Message m = gson.fromJson(doc.toJson(), Message.class);
	  	    	 messages.add(m);
	  	     }
		  	
			
		}else {

			System.out.println("OSOBE SU : "+userName + " I : " + id);
			
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
			System.out.println("QUERY ZA PORUKE JE : " + query);
		
			
			 @SuppressWarnings("unchecked")
	  	     FindIterable<Document> docs = messageDb.getCollection().find(query).sort(new Document("created_at",1));//valjda sortira
	  	     for (Document doc : docs) {
	  	    	 
	  	    	System.out.println("NASAO PORUKU");
	  	    	 Message m = gson.fromJson(doc.toJson(), Message.class);
	  	    	 messages.add(m);
	  	     }
		 	
			
		}
		
		
		return Response.status(Response.Status.OK).entity(messages).build();
		
		
	}
	
	
	
	
	@GET
    @Path("/getFriends/{id}")
    @Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
    public ArrayList<User> getFriends(@PathParam("id") String id){
		ArrayList<User> users = new ArrayList<>();
		

		
	  	Document or1 = new Document(
	  		  "$and", Arrays.asList(
	  		    new Document("sender", id),
	  		    new Document("status", FriendshipStatus.ACCEPTED.toString())
	  		  )
	  		);
	  	
	  	
		Document or2 = new Document(
		  		  "$and", Arrays.asList(
		  		    new Document("reciever", id),
		  		    new Document("status", FriendshipStatus.ACCEPTED.toString())
		  		  )
		  		);
		
		Document query = new Document(
		  		  "$or", Arrays.asList(
		  		  or1,
		  		  or2
		  		  )
		  		);
		
		System.out.println(query);
		
		Gson gson= new Gson();
		 @SuppressWarnings("unchecked")
  	     FindIterable<Document> docs = friendDb.getCollection().find(query);//valjda sortira .sort(new Document("created_at",1))
		 
  	     for (Document doc : docs) {

  	    	 
  	    	 Friendship m = gson.fromJson(doc.toJson(), Friendship.class);
  	    	 
  	    	 String user="";
  	    	 if(m.getReciever().equals(id))
  	    		 user=m.getSender();
  	    	 else
  	    		 user=m.getReciever();
  	    	 
  	    	 
  	    	 
  	    	 
  	    	 Document found = (Document) userDatabase.getCollection().find(new Document("username", user)).first();
  	     	 if(found != null) {

  	     		 User u = gson.fromJson(found.toJson(), User.class);
  	     		 users.add(u);
  	     		 
  	     	 }
  	    	 
  	    	
  	     }
		
		return users;
		
	}
	
	
	@GET
    @Path("/getGroups/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<Group> getGroups(@PathParam("username") String userName){
		ArrayList<Group> groups = new ArrayList<>();
		
		FindIterable<Document> docs = groupDb.getCollection().find();
		Gson gson = new Gson();
		for (Document document : docs) {
			Group g = gson.fromJson(document.toJson(), Group.class);
			for (User u : g.getUsers()) {
				if(u.getUsername().equals(userName)) 
					groups.add(g);
					
			}
 	    	 
		}
  	    
		return groups;
		
	}
	
	
	
	
	@POST
    @Path("/login/{userName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String login(@PathParam("userName") String userName, User user112){
		String password = user112.getPassword();
		
		System.out.println("U LOGINU SAM I PASS JE : " +password );
		String returnStr="";
	 	Document found = (Document) userDatabase.getCollection().find(new Document("username", userName)).first();
	 	if(found != null) {
	 		
	 		
	 		userDatabase.getCollection().updateOne(found,new Document("$set", new Document("hostIp", users.getCurrentIp())));
	 		
	   		  Gson gson = new Gson();
	   	      User user = gson.fromJson(found.toJson(), User.class);  
	   	      if(user.getPassword().equals(password)) {
	   	    	 
	   	    	 
		   	    	ResteasyClient client = new ResteasyClientBuilder().build();
		 			
		 			ResteasyWebTarget target = client.target(
		 					"http://" + users.getMasterIp() + ":8096/UserApp/rest/users/login");
		 			Response response = target.request(MediaType.TEXT_PLAIN).post(Entity.entity(user,MediaType.APPLICATION_JSON));
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
	 	   	      System.out.println("U loginu iznad!!!");
	 	   	      //alertFriends(userName, false);
	 			
	 			
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
		 	   	   
		 	   	    //  alertFriends(userName, true);
		 		
		   	      
		 	}else
		 		returnStr="NO USER";

			
			return returnStr;
		 
	 }
	
	
	@DELETE
	@Path("/removeActive/{userName}")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public void removeActive(@PathParam("userName") String userName) {
		
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
		@Path("/forwardMessage")
		@Consumes(MediaType.APPLICATION_JSON)
	    @Produces(MediaType.APPLICATION_JSON)
		public void forward(Message m) {
	 			Document found = (Document) userDatabase.getCollection().find(new Document("username", m.getReciver())).first();
	 			if(found!=null) {
	 				Gson gson = new Gson();
		 		   	User user = gson.fromJson(found.toJson(), User.class);  
		 		   	if(user.getHostIp().equals(users.getCurrentIp())) {
						try {
							ws.forwardMessage(m);
						} catch (IOException e) {

							e.printStackTrace();
						}
		 		   	}else {
		 		   		ResteasyClient client = new ResteasyClientBuilder().build();
		 		   		ResteasyWebTarget target = client.target(
			 					"http://" + user.getHostIp() + ":8096/ChatApp/rest/users/sendMessage");
		 		   		Response response = target.request(MediaType.TEXT_PLAIN).post(Entity.entity(m, MediaType.APPLICATION_JSON));
		 		   		
		 		   	}
		 					
	 			}

		}
	 	
	 	
		@POST
		@Path("/forwardGroupMessage")
		@Consumes(MediaType.APPLICATION_JSON)
	    @Produces(MediaType.APPLICATION_JSON)
		public void forwardGroup(Message m) {
			Document found = (Document) groupDb.getCollection().find(new Document("id", m.getGroupId())).first();
			if(found!=null) {
				
				 ObjectMapper mapper = new ObjectMapper();
		         String json="";
				try {
					json = mapper.writeValueAsString(m);
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		         messageDb.getCollection().insertOne(Document.parse(json));
				
				
 				Gson gson = new Gson();
	 		   	Group g = gson.fromJson(found.toJson(), Group.class);
	 		   	for (User u : g.getUsers()) {
	 		   		if(!u.getUsername().equals(m.getSender())) {
	 		   			m.setReciver(u.getUsername());
	 		   			forward(m);
	 		   		}
	 		   			
				}
	 		   	
			}
			
		}
	
	
	
	@POST
	@Path("/sendMessage")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public void getMessage(Message m) {
	
		System.out.println("EVO GA CAK U SEND MESSAGE");
		
		for (User u : users.getActiveUsers()) {
			if(u.getUsername().equals(m.getReciver()))
				return;
			
		}
		    try {  	
				ws.forwardMessage(m);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		
	}

}
