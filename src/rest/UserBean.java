package rest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ws.rs.Path;

import model.User;

@LocalBean
@Path("/hosts")
@Singleton
@Startup
public class UserBean {
	
	private ArrayList<User> activeUsers;
	private String masterIp;
	
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
		
		
		
		
		
	}
	

}
