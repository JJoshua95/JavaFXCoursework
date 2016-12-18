package application;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

// Regular employees are only allowed to see the staff Screen, staff with isManager == true are able to access manager screen

/**
 * The staff class instantiates Staff objects that can be displayed in Tableviews after retriving the relevent data
 * from a database
 * @author jarrod joshua
 */
public class Staff {
	
	private SimpleIntegerProperty staffID;
	private SimpleStringProperty username;
	private SimpleStringProperty password;
	private SimpleStringProperty isManager;
	
	/**
	 * The class constructor takes int andString type inputs and outputs a Staff object with javafx properties that can
	 * be displayed in a tableview. The getters output ints or Strings not properties
	 * @param staffID
	 * @param username
	 * @param password
	 * @param isManager
	 */
	public Staff(int staffID, String username, String password, String isManager) {
		super();
		this.staffID = new SimpleIntegerProperty(staffID);
		this.username = new SimpleStringProperty(username);
		this.password = new SimpleStringProperty(password);
		this.isManager = new SimpleStringProperty(isManager);
	}
	
	public int getStaffID() {
		return staffID.get();
	}
	
	public String getUsername() {
		return username.get();
	}
	
	public String getPassword() {
		return password.get();
	}
	
	public String getIsManager() {
		return isManager.get();
	}

}
