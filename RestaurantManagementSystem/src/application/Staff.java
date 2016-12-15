package application;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

// Regular employees are only allowed to see the staff Screen, staff with isManager == true are able to access manager screen

public class Staff {
	
	private SimpleIntegerProperty staffID;
	private SimpleStringProperty username;
	private SimpleStringProperty password;
	private SimpleStringProperty isManager;
	
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
