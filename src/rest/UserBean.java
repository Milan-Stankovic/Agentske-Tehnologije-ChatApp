package rest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ws.rs.Path;

import model.Host;
import model.User;
import java.net.*;
import java.io.*;

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
			
			java.nio.file.Path p = Paths.get(".").toAbsolutePath().normalize();
			br = new BufferedReader(new FileReader(p.toString()+"\\config.txt"));
			
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();

		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		    }
		    masterIp = sb.toString();
		    
		    URL whatismyip = new URL("http://checkip.amazonaws.com");
		    BufferedReader in = new BufferedReader(new InputStreamReader(
		                    whatismyip.openStream()));

		    String ip = in.readLine();
		    
		    System.out.println("I am registring myself.\n Master IP is: "+masterIp+"My IP(global) is: "+ip+"\nMy IP(local) is: " + getLocalHostLANAddress().toString());
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
	
	/**
	 * Returns an <code>InetAddress</code> object encapsulating what is most likely the machine's LAN IP address.
	 * <p/>
	 * This method is intended for use as a replacement of JDK method <code>InetAddress.getLocalHost</code>, because
	 * that method is ambiguous on Linux systems. Linux systems enumerate the loopback network interface the same
	 * way as regular LAN network interfaces, but the JDK <code>InetAddress.getLocalHost</code> method does not
	 * specify the algorithm used to select the address returned under such circumstances, and will often return the
	 * loopback address, which is not valid for network communication. Details
	 * <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4665037">here</a>.
	 * <p/>
	 * This method will scan all IP addresses on all network interfaces on the host machine to determine the IP address
	 * most likely to be the machine's LAN address. If the machine has multiple IP addresses, this method will prefer
	 * a site-local IP address (e.g. 192.168.x.x or 10.10.x.x, usually IPv4) if the machine has one (and will return the
	 * first site-local address if the machine has more than one), but if the machine does not hold a site-local
	 * address, this method will return simply the first non-loopback address found (IPv4 or IPv6).
	 * <p/>
	 * If this method cannot find a non-loopback address using this selection algorithm, it will fall back to
	 * calling and returning the result of JDK method <code>InetAddress.getLocalHost</code>.
	 * <p/>
	 *
	 * @throws UnknownHostException If the LAN address of the machine cannot be found.
	 */
	private static InetAddress getLocalHostLANAddress() throws UnknownHostException {
	    try {
	        InetAddress candidateAddress = null;
	        // Iterate all NICs (network interface cards)...
	        for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements();) {
	            NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
	            // Iterate all IP addresses assigned to each card...
	            for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements();) {
	                InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
	                if (!inetAddr.isLoopbackAddress()) {

	                    if (inetAddr.isSiteLocalAddress()) {
	                        // Found non-loopback site-local address. Return it immediately...
	                        return inetAddr;
	                    }
	                    else if (candidateAddress == null) {
	                        // Found non-loopback address, but not necessarily site-local.
	                        // Store it as a candidate to be returned if site-local address is not subsequently found...
	                        candidateAddress = inetAddr;
	                        // Note that we don't repeatedly assign non-loopback non-site-local addresses as candidates,
	                        // only the first. For subsequent iterations, candidate will be non-null.
	                    }
	                }
	            }
	        }
	        if (candidateAddress != null) {
	            // We did not find a site-local address, but we found some other non-loopback address.
	            // Server might have a non-site-local address assigned to its NIC (or it might be running
	            // IPv6 which deprecates the "site-local" concept).
	            // Return this non-loopback candidate address...
	            return candidateAddress;
	        }
	        // At this point, we did not find a non-loopback address.
	        // Fall back to returning whatever InetAddress.getLocalHost() returns...
	        InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
	        if (jdkSuppliedAddress == null) {
	            throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
	        }
	        return jdkSuppliedAddress;
	    }
	    catch (Exception e) {
	        UnknownHostException unknownHostException = new UnknownHostException("Failed to determine LAN address: " + e);
	        unknownHostException.initCause(e);
	        throw unknownHostException;
	    }
	}
	

}
