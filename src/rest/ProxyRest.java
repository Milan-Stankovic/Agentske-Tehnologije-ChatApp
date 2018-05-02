package rest;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import model.Friendship;

@LocalBean
@Path("/proxy")
@Stateless
public class ProxyRest {

	@Inject
	private UserBean nodeInfo;
	
	private boolean checkIfMaster() {
		return nodeInfo.getMasterIp().equals(nodeInfo.getCurrentIp());
	}
	
	@POST
	@Path("/newFriendship")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createFriendship(Friendship newFriendship)
    {
		if(checkIfMaster()) {
			ResteasyClient client = new ResteasyClientBuilder().build();
			ResteasyWebTarget target = client.target(
					"http://" + nodeInfo.getMasterIp() + ":8096/UserApp/friendship");
			Response response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(newFriendship, MediaType.APPLICATION_JSON));
			if(Response.Status.OK.equals(response.getStatus())) {
				return Response.status(Response.Status.OK).entity(newFriendship).build();
			}else {
				return Response.status(response.getStatus()).entity(response.getEntity()).build();
			}
		}
		else {
			//JMS
			return null;
		}

    }
	
	@POST
	@Path("/deleteFriendship")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteFriendship(Friendship toDelete)
    {
		if(checkIfMaster()) {		
			ResteasyClient client = new ResteasyClientBuilder().build();
			ResteasyWebTarget target = client.target(
					"http://" + nodeInfo.getMasterIp() + ":8096/UserApp/friendship");
			
			Response response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(toDelete, MediaType.APPLICATION_JSON));
			if(Response.Status.OK.equals(response.getStatus())) {
				return Response.status(Response.Status.OK).entity(toDelete).build();
			}else {
				return Response.status(response.getStatus()).entity(response.getEntity()).build();
			}
		}
		else {
			//JMS
			return null;
		}

    }
	
	@PUT
	@Path("/putFriendship")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response putFriendship(Friendship toDelete)
    {
		if(checkIfMaster()) {		
			ResteasyClient client = new ResteasyClientBuilder().build();
			ResteasyWebTarget target = client.target(
					"http://" + nodeInfo.getMasterIp() + ":8096/UserApp/friendship");
			
			Response response = target.request(MediaType.APPLICATION_JSON).put(Entity.entity(toDelete, MediaType.APPLICATION_JSON));
			if(Response.Status.OK.equals(response.getStatus())) {
				return Response.status(Response.Status.OK).entity(toDelete).build();
			}else {
				return Response.status(response.getStatus()).entity(response.getEntity()).build();
			}
		}
		else {
			//JMS
			return null;
		}
    }
	
}
