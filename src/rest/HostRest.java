package rest;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.bson.Document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dbClasses.HostDatabase;
import model.Friendship;
import model.Host;
import rest.dto.ErrorDTO;

@LocalBean
@Path("/hosts")
@Stateless
public class HostRest {

	@Inject
	private HostDatabase hostDatabase;
	
	@POST
    @Path("/registerHost")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerHost(Host newHost)
    {
        Document searchBy = new Document();
        searchBy.append("address", newHost.getAddress());

        Document found = (Document) hostDatabase.getCollection().find(searchBy).first();
     
        if(found!=null) {
        	return Response.status(Response.Status.CONFLICT).entity(new ErrorDTO("Host with same ip adress already exists.")).build();
        }else {
        	ObjectMapper mapper = new ObjectMapper();
            String json = null;
            try {
                json = mapper.writeValueAsString(newHost);
                hostDatabase.getCollection().insertOne(Document.parse(json));

                //TODO notify other nodes about new node.

                return Response.status(Response.Status.OK).entity(newHost).build();
            } catch (JsonProcessingException e) {
                return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorDTO("Error while parsing JSON.")).build();
            }
        }
    }
}
