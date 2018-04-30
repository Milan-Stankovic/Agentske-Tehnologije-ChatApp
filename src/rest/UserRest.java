package rest;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@LocalBean
@Path("/users")
@Stateless
public class UserRest {
	
	@GET
	@Path("/activeUsers")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	private Response getActiveUsers() {
		return null;
		//TODO Ovde chatapp u master cvoru primi zahtev, posalje JMS userapp-u na "" i dobije listu svih korisnika.
	}

}
