package application;

import javafx.beans.property.SimpleStringProperty;

public class ActivityLog {
	
	private SimpleStringProperty username;
	private SimpleStringProperty activityEntry;
	private SimpleStringProperty time;
	
	public ActivityLog(String username, String activityEntry, String time) {
		super();
		this.username = new SimpleStringProperty(username);
		this.activityEntry = new SimpleStringProperty(activityEntry);
		this.time = new SimpleStringProperty(time);
	}
	
	public String getUsername() {
		return username.get();
	}
	
	public String getActivityEntry() {
		return activityEntry.get();
	}
	
	public String getTime() {
		return time.get();
	}
	
}
