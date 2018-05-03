package jms;

import java.util.HashMap;
import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import model.Friendship;
import model.Group;
import model.NotificationDTO;
import model.NotificationType;
import model.User;
import rest.PushNotifications;


@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "java:jboss/exported/jms/queue/mojQueue1") })

public class PrimalacQueueMDB implements MessageListener {

	@Inject
	PushNotifications wsPush;
	
	Logger log = Logger.getLogger("Primalac MDB");


	public void onMessage(Message msg) {
		log.info("ChatApp");
		ObjectMessage omsg = (ObjectMessage) msg;
		try {
			jmsDTO aclMessage = (jmsDTO) omsg.getObject();
			
			NotificationDTO n = new NotificationDTO();
			
			switch (aclMessage.getStatus()) {
			case NEW_FRIENDSHIP:
				n.setRecieverId(((Friendship)aclMessage.getContent()).getReciever());
				n.setUserId(((Friendship)aclMessage.getContent()).getSender());
				n.setType(NotificationType.PENDING);
				wsPush.pushNotification(n);
				break;
			case DELETE_FRIENDSHIP:
				n.setRecieverId(((Friendship)aclMessage.getContent()).getReciever());
				n.setUserId(((Friendship)aclMessage.getContent()).getSender());
				n.setType(NotificationType.REMOVED);
				wsPush.pushNotification(n);
				break;
			case PUT_FRIENDSHIP:
				n.setRecieverId(((Friendship)aclMessage.getContent()).getReciever());
				n.setUserId(((Friendship)aclMessage.getContent()).getSender());
				n.setType(NotificationType.ACCEPTED);
				wsPush.pushNotification(n);
				break;
			case GET_GROUP:
				
				
				break;
			case NEW_GROUP:
				for(User u: ((Group)aclMessage.getContent()).getUsers()) {
					n.setRecieverId(u.getUsername());
					n.setGroupId(((Group)aclMessage.getContent()).getId());
					n.setType(NotificationType.GROUPADD);
					wsPush.pushNotification(n);
				}
				
				break;
			case DELETE_GROUP:
				for(User u: ((Group)aclMessage.getContent()).getUsers()) {
					n.setRecieverId(u.getUsername());
					n.setGroupId(((Group)aclMessage.getContent()).getId());
					n.setType(NotificationType.GROUPREMOVE);
					wsPush.pushNotification(n);
				}
				break;
			case ADD_USER:
				for(User u: ((Group)aclMessage.getContent()).getUsers()) {
					n.setRecieverId(u.getUsername());
					n.setGroupId(((Group)aclMessage.getContent()).getId());
					n.setUserId(aclMessage.getInfo());
					n.setType(NotificationType.GROUPNEWUSER);
					wsPush.pushNotification(n);
				}
				break;
			case REMOVE_USER_GROUP:
				for(User u: ((Group)aclMessage.getContent()).getUsers()) {
					n.setRecieverId(u.getUsername());
					n.setGroupId(((Group)aclMessage.getContent()).getId());
					n.setUserId(aclMessage.getInfo());
					n.setType(NotificationType.GROUPREMOVEUSER);
					wsPush.pushNotification(n);
				}
				break;

			default:
				break;
			}
			
		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	
}