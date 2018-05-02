package rest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import model.Host;
import model.User;

@LocalBean
@Path("/hosts")
@Singleton
@Startup
public class UserBean {
	
	private ArrayList<User> activeUsers;
	private ArrayList<Host> registeredHosts;
	private String masterIp;
	private String currentIp;
	
	
	
	
	
	
	public String getCurrentIp() {
		return currentIp;
	}




	public void setCurrentIp(String currentIp) {
		this.currentIp = currentIp;
	}




	public ArrayList<User> getActiveUsers() {
		return activeUsers;
	}




	public void setActiveUsers(ArrayList<User> activeUsers) {
		this.activeUsers = activeUsers;
	}




	public String getMasterIp() {
		return masterIp;
	}




	public void setMasterIp(String masterIp) {
		this.masterIp = masterIp;
	}




	public ArrayList<Host> getRegisteredHosts() {
		return registeredHosts;
	}




	public void setRegisteredHosts(ArrayList<Host> registeredHosts) {
		this.registeredHosts = registeredHosts;
	}




	@PostConstruct
	private void init() {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader("config.txt"));
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();

		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		    }
		    masterIp = sb.toString();
		    System.out.println("I am registring myself.\n My IP is: "+masterIp);
		} catch (Exception e) {
			System.out.println("Error while loading config.txt");
		}
		
		/*ResteasyClient client = new ResteasyClientBuilder().build();
		
		ResteasyWebTarget target = client.target(
				"http://" + masterIp + ":8096/UserApp/hosts/registerHost/");
		
		Response response = target.request(MediaType.APPLICATION_JSON).get();
		
		if(Response.Status.OK.ordinal()==response.getStatus()) {
			String listString= response.readEntity(String.class);
			Gson gson=new Gson();
			Type type = new TypeToken<List<String>>(){}.getType();
			registeredHosts = gson.fromJson(listString, type);
			
			ResteasyClient client1 = new ResteasyClientBuilder().build();
			
			ResteasyWebTarget target1 = client1.target(
					"http://" + masterIp + ":8096/UserApp/users/activeUsers");
			
			Response response1 = target1.request(MediaType.APPLICATION_JSON).get();
			
			String listString1= response1.readEntity(String.class);
			Gson gson1=new Gson();
			Type type1 = new TypeToken<List<String>>(){}.getType();
			activeUsers = gson1.fromJson(listString1, type1);
		}
		else {
			System.out.println("Host aready exsists.");
		}
		*/
		
	}
	

}
