package rest;

import java.util.ArrayList;
import java.util.Arrays;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.bson.Document;

import com.google.gson.Gson;
import com.mongodb.client.FindIterable;

import dbClasses.FriendshipDatabase;
import model.Friendship;
import model.FriendshipStatus;

@LocalBean
@Path("/front")
@Stateless
public class FrontEndRests {
	
	@Inject
	FriendshipDatabase friendDb;
	
	@GET
	@Path("/friendsOfUser/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public Response notify(@PathParam("id") String grid) {
		ArrayList<Friendship> retval = new ArrayList<Friendship>();
		Document or1 = new Document(
		  		  "$and", Arrays.asList(
		  		    new Document("sender", grid),
		  		    new Document("status", FriendshipStatus.ACCEPTED.toString())
		  		  )
		  		);
		  	
		  	
			Document or2 = new Document(
			  		  "$and", Arrays.asList(
			  		    new Document("reciever", grid),
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
	  	     FindIterable<Document> docs = friendDb.getCollection().find(query);
	     for (Document doc : docs) {
	    	 Friendship friend = gson.fromJson(doc.toJson(), Friendship.class);
	    	 if(friend.getStatus()==FriendshipStatus.ACCEPTED) {
	    		 retval.add(friend);
	    	 }
	     }
	     
	     return Response.status(Response.Status.OK).entity(retval).build();
	}
	
	@GET
	@Path("/friendsOfUserPending/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public Response notifyp(@PathParam("id") String grid) {
		ArrayList<Friendship> retval = new ArrayList<Friendship>();
		Document or1 = new Document(
		  		  "$and", Arrays.asList(
		  		    new Document("sender", grid),
		  		    new Document("status", FriendshipStatus.ACCEPTED.toString())
		  		  )
		  		);
		  	
		  	
			Document or2 = new Document(
			  		  "$and", Arrays.asList(
			  		    new Document("reciever", grid),
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
	  	     FindIterable<Document> docs = friendDb.getCollection().find(query);
	     for (Document doc : docs) {
	    	 Friendship friend = gson.fromJson(doc.toJson(), Friendship.class);
	    	 if(friend.getStatus()==FriendshipStatus.PENDING) {
	    		 retval.add(friend);
	    	 }
	     }
	     
	     return Response.status(Response.Status.OK).entity(retval).build();
	}

}
