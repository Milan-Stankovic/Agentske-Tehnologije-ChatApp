package model;

import com.google.gson.Gson;

public class NotificationDTO {
	
	private String userId=""; 
	
	private String groupId=""; 
	
	private String recieverId = "";
	
	private NotificationType type;
	

	public String getRecieverId() {
		return recieverId;
	}

	public void setRecieverId(String recieverId) {
		this.recieverId = recieverId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public NotificationType getType() {
		return type;
	}

	public void setType(NotificationType type) {
		this.type = type;
	}
	
	public NotificationDTO() {
		
	}
	
	public NotificationDTO(String text) {
		Gson test = new Gson();
		NotificationDTO temp = test.fromJson(text, NotificationDTO.class);
		this.groupId=temp.groupId;
		this.type=temp.type;
		this.userId=temp.userId;
	}
	

}
