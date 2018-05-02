package rest;

import java.io.IOException;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import model.Message;

@LocalBean
@Path("/users")
@Stateless
public class UserRest {
	
	@Inject
	private UserBean users;
	
	@Inject
	private WebSocket ws;
	
	@GET
	@Path("/activeUsers")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	private Response getActiveUsers() {
		return null;
		//TODO Ovde chatapp u master cvoru primi zahtev, posalje JMS userapp-u na "" i dobije listu svih korisnika.
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
