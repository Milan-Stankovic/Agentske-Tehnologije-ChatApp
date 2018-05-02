package rest;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.websocket.server.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.bson.Document;

import dbClasses.UserDatabase;
import model.Friendship;

@LocalBean
@Path("/notify")
@Stateless
public class NotificationRest {
	
	@Inject
	private UserDatabase userDatabase;
	
	@POST
	@Path("{username}/notifyFriendshipStart")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	private Response notifyWantedFriend(@PathParam("userName") String userName, Friendship addedFriendship) {
		Document foundSender = (Document) userDatabase.getCollection().find(new Document("username", addedFriendship.getSender())).first();
		Document foundReciver = (Document) userDatabase.getCollection().find(new Document("username", addedFriendship.getSender())).first();
		
		if(foundReciver==null||foundSender==null) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}else {
			//Notify via websocket
			return null;
		}
	}
	
	@POST
	@Path("{username}/notifyFriendshipEnd")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	private Response notifyFriendshipEnd(@PathParam("userName") String userName, Friendship deleted) {
		Document foundSender = (Document) userDatabase.getCollection().find(new Document("username", deleted.getSender())).first();
		Document foundReciver = (Document) userDatabase.getCollection().find(new Document("username", deleted.getSender())).first();
		
		if(foundReciver==null||foundSender==null) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}else {
			//Notify via websocket
			return null;
		}
	}
	
	@POST
	@Path("{username}/notifyFriendshipStateChange")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	private Response notifyFriendshiopChanged(@PathParam("userName") String userName, Friendship updated) {
		Document foundSender = (Document) userDatabase.getCollection().find(new Document("username", updated.getSender())).first();
		Document foundReciver = (Document) userDatabase.getCollection().find(new Document("username", updated.getSender())).first();
		
		if(foundReciver==null||foundSender==null) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}else {
			//Notify via websocket
			return null;
		}
	}
	
	@POST
	@Path("{username}/notifyNewGroup")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	private Response notifyNewGroup(@PathParam("userName") String userName) {
		//notify via websocket
		return null;
	}
	
	@POST
	@Path("{username}/notifyEndGroup")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	private Response notifyEndGroup(@PathParam("userName") String userName) {
		//notify via websocket
		return null;
	}
	
	@POST
	@Path("{username}/notifyNewGroupMember")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	private Response notifyNewGroupMember(@PathParam("userName") String userName) {
		return null;
	}
	
	@POST
	@Path("{username}/notifyRemovedUser")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	private Response notifyRemovedUser(@PathParam("userName") String userName) {
		return null;
	}

}
