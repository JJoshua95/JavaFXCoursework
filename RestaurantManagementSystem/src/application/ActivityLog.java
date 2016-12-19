package application;

import javafx.beans.property.SimpleStringProperty;

/**
 * This class is needed to instantiate Activity objects to display info fetched from the database
 * in the tables of the manager screen using JavaFX properties.
 * The methods and structure of this class are derived from the tutorials available from the ProgrammingKnowledge playlist 
 * JavaFX tutorial for beginners : https://www.youtube.com/watch?v=9YrmON6nlEw&list=PLS1QulWo1RIaUGP446_pWLgTZPiFizEMq
 * @author jarrod joshua
 */
public class ActivityLog {
	
	private SimpleStringProperty username;
	private SimpleStringProperty activityEntry;
	private SimpleStringProperty time;
	
	/**
	 * The class constructor takes standard input types but outputs an object with property variables
	 * which are required for tables. The getters outputs strings only not properties
	 * @param username
	 * @param activityEntry : a String describing an action the currently logged in user has performed.
	 * @param time : an instantaneous time stamp string generated at the moment a saveActivity method was called.
	 */
	public ActivityLog(String username, String activityEntry, String time) {
		super();
		this.username = new SimpleStringProperty(username);
		this.activityEntry = new SimpleStringProperty(activityEntry);
		this.time = new SimpleStringProperty(time);
	}
	
	/**
	 * Getter for the 'username' property
	 * @return username : String equivalent of the username property of the object.
	 */
	public String getUsername() {
		return username.get();
	}
	
	/**
	 * Getter for the 'activityEntry' property of this object.
	 * @return activityEntry : String equivalent of the activityEntry property of this object
	 */
	public String getActivityEntry() {
		return activityEntry.get();
	}
	
	/**
	 * Getter for the 'time' property of this object
	 * @return time : String equivalent of the time property of this object
	 */
	public String getTime() {
		return time.get();
	}
	
}
