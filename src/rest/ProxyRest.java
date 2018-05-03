package rest;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import jms.JMSQueue;
import jms.JMSStatus;
import jms.jmsDTO;
import model.Friendship;
import model.Group;
import model.User;

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
			return target.request(MediaType.APPLICATION_JSON).post(Entity.entity(newFriendship, MediaType.APPLICATION_JSON));
			/*if(Response.Status.OK.equals(response.getStatus())) {
				return Response.status(Response.Status.OK).entity(newFriendship).build();
			}else {
				return Response.status(response.getStatus()).entity(response.getEntity()).build();
			}*/
		}
		else {
			new JMSQueue(new jmsDTO("", JMSStatus.NEW_FRIENDSHIP, newFriendship));
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
			
			return target.request(MediaType.APPLICATION_JSON).post(Entity.entity(toDelete, MediaType.APPLICATION_JSON));
			/*if(Response.Status.OK.equals(response.getStatus())) {
				return Response.status(Response.Status.OK).entity(toDelete).build();
			}else {
				return Response.status(response.getStatus()).entity(response.getEntity()).build();
			}*/
		}
		else {
			new JMSQueue(new jmsDTO("", JMSStatus.DELETE_FRIENDSHIP, toDelete));
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
			
			return target.request(MediaType.APPLICATION_JSON).put(Entity.entity(toDelete, MediaType.APPLICATION_JSON));
			/*if(Response.Status.OK.equals(response.getStatus())) {
				return Response.status(Response.Status.OK).entity(toDelete).build();
			}else {
				return Response.status(response.getStatus()).entity(response.getEntity()).build();
			}*/
		}
		else {
			new JMSQueue(new jmsDTO("", JMSStatus.PUT_FRIENDSHIP, toDelete));
			return null;
		}
    }
	
	@GET
    @Path("/groups/{groupId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGroup(@PathParam("groupId") String groupId) {
		if(checkIfMaster()) {		
			ResteasyClient client = new ResteasyClientBuilder().build();
			ResteasyWebTarget target = client.target(
					"http://" + nodeInfo.getMasterIp() + ":8096/UserApp/groups/group/"+groupId);
			
			return target.request(MediaType.APPLICATION_JSON).get();
			/*if(Response.Status.OK.equals(response.getStatus())) {
				return Response.status(Response.Status.OK).entity(response.getEntity()).build();
			}else {
				return Response.status(response.getStatus()).entity(response.getEntity()).build();
			}*/
		}
		else {
			new JMSQueue(new jmsDTO("", JMSStatus.GET_GROUP, groupId));
			return null;
		}
    }
	
	@POST
	@Path("/groups")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public Response newGroup(Group toCreate) {
		if(checkIfMaster()) {		
			ResteasyClient client = new ResteasyClientBuilder().build();
			ResteasyWebTarget target = client.target(
					"http://" + nodeInfo.getMasterIp() + ":8096/UserApp/groups/group/");
			
			return target.request(MediaType.APPLICATION_JSON).put(Entity.entity(toCreate, MediaType.APPLICATION_JSON));
			/*if(Response.Status.OK.equals(response.getStatus())) {
				return Response.status(Response.Status.OK).entity(response.getEntity()).build();
			}else {
				return Response.status(response.getStatus()).entity(response.getEntity()).build();
			}*/
		}
		else {
			new JMSQueue(new jmsDTO("", JMSStatus.GET_GROUP, toCreate));
			return null;
		}
	}
	
	@POST
	@Path("/groups/delete")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public Response deleteGroup(Group toDelete) {
		if(checkIfMaster()) {		
			ResteasyClient client = new ResteasyClientBuilder().build();
			ResteasyWebTarget target = client.target(
					"http://" + nodeInfo.getMasterIp() + ":8096/UserApp/groups/group/delete");
			
			return target.request(MediaType.APPLICATION_JSON).put(Entity.entity(toDelete, MediaType.APPLICATION_JSON));
			/*if(Response.Status.OK.equals(response.getStatus())) {
				return Response.status(Response.Status.OK).entity(response.getEntity()).build();
			}else {
				return Response.status(response.getStatus()).entity(response.getEntity()).build();
			}*/
		}
		else {
			new JMSQueue(new jmsDTO("", JMSStatus.DELETE_GROUP, toDelete));
			return null;
		}
	}
	
	@POST
    @Path("/group/{groupId}/users")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addUser(@PathParam("groupId") String groupId,User toAdd) {
		if(checkIfMaster()) {		
			ResteasyClient client = new ResteasyClientBuilder().build();
			ResteasyWebTarget target = client.target(
					"http://" + nodeInfo.getMasterIp() + ":8096/UserApp/groups/group/"+groupId+"/users");
			
			return target.request(MediaType.APPLICATION_JSON).put(Entity.entity(toAdd, MediaType.APPLICATION_JSON));
			/*if(Response.Status.OK.equals(response.getStatus())) {
				return Response.status(Response.Status.OK).entity(response.getEntity()).build();
			}else {
				return Response.status(response.getStatus()).entity(response.getEntity()).build();
			}*/
		}
		else {
			new JMSQueue(new jmsDTO(groupId, JMSStatus.DELETE_GROUP, toAdd));
			return null;
		}
	}
	
	@DELETE
    @Path("/group/{groupId}/users/{userId}/sender/{sendingId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeUser(@PathParam("groupId") String groupId, @PathParam("userId") String userId, @PathParam("sendingId") String sendingId) {
		if(checkIfMaster()) {		
			ResteasyClient client = new ResteasyClientBuilder().build();
			ResteasyWebTarget target = client.target(
					"http://" + nodeInfo.getMasterIp() + ":8096/UserApp/groups/group/"+groupId+"/users/"+userId+"/sender/"+sendingId);
			
			return target.request(MediaType.APPLICATION_JSON).delete();
			/*if(Response.Status.OK.equals(response.getStatus())) {
				return Response.status(Response.Status.OK).entity(response.getEntity()).build();
			}else {
				return Response.status(response.getStatus()).entity(response.getEntity()).build();
			}*/
		}
		else {
			//JMS
			return null;
		}
	
	}
	
	
}
