package rest;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.websocket.server.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@LocalBean
@Path("/notify")
@Stateless
public class NotificationRest {
	
	@GET
	@Path("{username}/notifyFriendshipStart")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	private Response notifyWantedFriend(@PathParam("userName") String userName) {
		return null;
	}
	
	@GET
	@Path("{username}/notifyFriendshipEnd")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	private Response notifyFriendshipEnd(@PathParam("userName") String userName) {
		return null;
	}
	
	@GET
	@Path("{username}/notifyFriendshipStateChange")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	private Response notifyFriendshiopChanged(@PathParam("userName") String userName) {
		return null;
	}
	
	@GET
	@Path("{username}/notifyNewGroup")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	private Response notifyNewGroup(@PathParam("userName") String userName) {
		return null;
	}
	
	@GET
	@Path("{username}/notifyEndGroup")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	private Response notifyEndGroup(@PathParam("userName") String userName) {
		return null;
	}
	
	@GET
	@Path("{username}/notifyNewGroupMember")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	private Response notifyNewGroupMember(@PathParam("userName") String userName) {
		return null;
	}
	
	@GET
	@Path("{username}/notifyRemovedUser")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	private Response notifyRemovedUser(@PathParam("userName") String userName) {
		return null;
	}

}
