package application;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

// Regular employees are only allowed to see the staff Screen, staff with isManager == true are able to access manager screen

/**
 * The Staff class instantiates Staff objects that can be displayed in TableViews after retrieving the relevant records and data
 * from a database
 * The methods and structure of this class are derived from the tutorials available from the ProgrammingKnowledge playlist 
 * JavaFX tutorial for beginners : https://www.youtube.com/watch?v=9YrmON6nlEw&list=PLS1QulWo1RIaUGP446_pWLgTZPiFizEMq
 * @author jarrod joshua
 */
public class Staff {
	
	private SimpleIntegerProperty staffID;
	private SimpleStringProperty username;
	private SimpleStringProperty password;
	private SimpleStringProperty isManager;
	
	/**
	 * The class constructor takes int and String type inputs and outputs a Staff object with JavaFX properties that can
	 * be displayed in a TableView. The getters output int or Strings not properties
	 * @param staffID : an int specifying the staffID attribute of this Staff Object
	 * @param username : a String specifying the 'username' field of the Staff Object
	 * @param password : A String specifying the 'password' field of the Staff Object
	 * @param isManager : A String "true" or "false" specifying if the Staff Object allows for manager level access to the program
	 */
	public Staff(int staffID, String username, String password, String isManager) {
		super();
		this.staffID = new SimpleIntegerProperty(staffID);
		this.username = new SimpleStringProperty(username);
		this.password = new SimpleStringProperty(password);
		this.isManager = new SimpleStringProperty(isManager);
	}
	
	/**
	 * Returns the staffID property of the Staff object as an int
	 * @return staffID : int specifying the staffID property of the Staff Object
	 */
	public int getStaffID() {
		return staffID.get();
	}
	
	/**
	 * Return the username attribute of the Staff object as a String
	 * @return username : a String specifying the 'username' field of the Staff object
	 */
	public String getUsername() {
		return username.get();
	}
	
	/**
	 * Return the password attribute of the Staff object as a String
	 * @return password : a String specifying the 'password' field of the Staff object
	 */
	public String getPassword() {
		return password.get();
	}
	
	/**
	 * Return a String "true" or "false" only describing if the Staff object allows manager access to the program
	 * @return isManager : A String "true" if the corresponding staff account for this Staff object allows login to the manager screen, or "false" if not
	 */
	public String getIsManager() {
		return isManager.get();
	}

}
