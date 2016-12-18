package application;

import javafx.beans.property.SimpleStringProperty;

/**
 * This class is needed to instantiate Activity objects to display info fetched from the database
 * in the tables of the manager screen using javafx properties.
 * @author jarrod joshua
 */
public class ActivityLog {
	
	private SimpleStringProperty username;
	private SimpleStringProperty activityEntry;
	private SimpleStringProperty time;
	
	/**
	 * The class constructor takes standard inut types but outputs an object with property variables
	 * which are required for tables. The getters outputs strings only not properties
	 * @param username
	 * @param activityEntry
	 * @param time
	 */
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
