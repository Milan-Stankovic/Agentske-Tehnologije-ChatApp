package rest;

import java.io.IOException;

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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.bson.Document;

import com.google.gson.Gson;

import dbClasses.UserDatabase;
import model.Message;
import model.User;

@LocalBean
@Path("/users")
@Stateless
public class UserRest {
	
	@Inject
	private UserBean users;
	
	/*@Inject
	private WebSocket ws;*/
	
	@Inject 
	private UserDatabase userDatabase;
	

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
	@Path("/sendMessage")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	private void getMessage(Message m) {
		/*try {
			ws.forwardMessage(m);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
	}

}
