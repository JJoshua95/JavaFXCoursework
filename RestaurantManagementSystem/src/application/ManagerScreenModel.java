package application;

import java.sql.*;
import java.util.ArrayList;

import javafx.collections.ObservableList;

//This class makes use of a 3rd party library, the SQLite JDBC Driver 
//available from https://bitbucket.org/xerial/sqlite-jdbc/downloads

/**
 * This class handles the SQLite queries and database interactions needed for the manager screen, ManagerScreenController 
 * inputs are passed to methods in this class
 * The methods and structure of this class are derived from the tutorials available from the ProgrammingKnowledge playlist 
 * JavaFX tutorial for beginners : https://www.youtube.com/watch?v=9YrmON6nlEw&list=PLS1QulWo1RIaUGP446_pWLgTZPiFizEMq
 * This class makes use of a 3rd party library, the SQLite JDBC Driver 
 * available from https://bitbucket.org/xerial/sqlite-jdbc/downloads
 * @author jarrod joshua
 */
public class ManagerScreenModel extends StaffScreenModel {
	
	private Connection connection;
	
	/**
	 * The class constructor establishes a connection with the database or exits the program if no connection is found
	 */
	ManagerScreenModel() {
		connection = SqliteConnection.Connector();
		if (connection == null) {
			System.exit(1);
		}
		
	}
	
	/**
	 * returns a boolean true if the database is connected or false if it is not.
	 * @return boolean true if the database is connected or false if not
	 */
	boolean isDbConnected() {
		try {
			return !connection.isClosed();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	// ============ Staff queries =================
	
	/**
	 * Queries the database to retrieve all staff account elements and output an ArrayList containing all Staff record and objects 
	 * associated with the system
	 * @return ArrayList<Staff> targetStaff a list of all staff objects in the database
	 * @throws SQLException 
	 */
	ArrayList<Staff> getAllEmployeesFromDB() throws SQLException {
		ArrayList<Staff> targetStaff = new ArrayList<Staff>();
		PreparedStatement prepStmt = null;
		ResultSet resSet = null;
		
		String query = "SELECT * FROM staff";
		
		try {
			prepStmt = connection.prepareStatement(query);
			resSet = prepStmt.executeQuery();
			
			while (resSet.next()) {
				targetStaff.add(new Staff(resSet.getInt("id"), resSet.getString("username"), 
						resSet.getString("password"), resSet.getString("isManager")) );
			}
			
			prepStmt.close();
			resSet.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			prepStmt.close();
			resSet.close();
		}
		
		return targetStaff;
	}
	
	/**
	 * Takes an integer id number, and strings for the username, password and isManager fields of a Staff object,
	 * then add these as a new record into the database staff table.
	 * @param id : int number specifying the id attribute of this Staff object and its account
	 * @param user : String specifying the username field of the Staff object
	 * @param pw : String specifying the password field of the Staff object
	 * @param managerVerified : String specifying if the staff account has manager access permission ("true") or if not ("false")
	 * @throws SQLException 
	 */
	void addStaffAccountIntoDB(int id, String user, String pw, String managerVerified) throws SQLException {
		PreparedStatement prepStmt = null;
		String query = "INSERT OR REPLACE INTO staff (id, username, password, isManager) VALUES (?, ?, ?, ?)";
		try {
			prepStmt = connection.prepareStatement(query);
			prepStmt.setInt(1, id);
			prepStmt.setString(2, user);
			prepStmt.setString(3, pw);
			prepStmt.setString(4, managerVerified);
			prepStmt.execute();
			prepStmt.close();
			System.out.println("inserted or replaced");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			prepStmt.close();
		}
	}
	
	/**
	 * Takes an integer id number, and strings for the username, password and isManager fields of a Staff object,
	 * then finds a corresponding record in the staff table of the database and deletes the record corresponding to
	 * the all the inputs.
	 * @param id : int number specifying the id attribute of this Staff object and its account
	 * @param user : String specifying the username field of the Staff object
	 * @param pw : String specifying the password field of the Staff object
	 * @param managerVerified : String specifying if the staff account has manager access permission ("true") or if not ("false")
	 * @throws SQLException 
	 */
	void deleteStaffAccountFromDB(int id, String user, String pw, String managerVerified) throws SQLException {
		PreparedStatement prepStmt = null;
		String query = "DELETE FROM staff WHERE id = ? AND username = ? AND password = ? AND isManager = ?";
		try {
			prepStmt = connection.prepareStatement(query);
			prepStmt.setInt(1, id);
			prepStmt.setString(2, user);
			prepStmt.setString(3, pw);
			prepStmt.setString(4, managerVerified);
			prepStmt.execute();
			prepStmt.close();
			System.out.println("Record deleted");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			prepStmt.close();
		}
	}
	
	/**
	 * Takes a username string and queries the database to find a list of activity logs recorded from the corresponding staff account
	 * @param user : String specifying a username attribute of a staff record, in this case the account who's activity you want to see.
	 * @return ArrayList<ActivityLog> a list of ActivityLog objects ready for display on a TableView
	 * @throws SQLException 
	 */
	ArrayList<ActivityLog> getActivityLogForEmployee(String user) throws SQLException {
		ArrayList<ActivityLog> targetActivity = new ArrayList<ActivityLog>();
		PreparedStatement prepStmt = null;
		ResultSet resSet = null;
		
		String query = "SELECT * FROM activityLog WHERE username = ?";
		
		try {
			prepStmt = connection.prepareStatement(query);
			prepStmt.setString(1,user);
			resSet = prepStmt.executeQuery();
			while (resSet.next()) {
				targetActivity.add(new ActivityLog(resSet.getString("username"),
						resSet.getString("activityEntry"), resSet.getString("time"))
						);
			}
			prepStmt.close();
			resSet.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			prepStmt.close();
			resSet.close();
		}
		
		return targetActivity;
	}
	
	// ========================= Menu queries ==================================
	
	/**
	 * Takes a name string and price double and inserts a new Food item into the menu table of the database.
	 * @param name : String input for the name of the new Food object you want to add to the menu
	 * @param cost : double input specifying the price you want to charge for this new Food object in the menu
	 * @throws SQLException 
	 */
	void addNewDishToMenuDB(String name, double cost) throws SQLException {
		PreparedStatement prepStmt = null;
		String query = "INSERT OR REPLACE INTO menu (menuItem, price) VALUES (?, ?)";
		
		try {
			prepStmt = connection.prepareStatement(query);
			prepStmt.setString(1, name);
			prepStmt.setDouble(2, cost);
			prepStmt.execute();
			prepStmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			prepStmt.close();
		}
	}
	
	/**
	 * Takes a name string and price double then searches the table menu and 
	 * and deletes the corresponding Food item from the menu table of the database.
	 * @param name : String input for the name of the Food object you want to delete from the menu
	 * @param cost : double input specifying the price of the Food object you want removed from the menu
	 * @throws SQLException 
	 */
	void deleteDishFromMenuDB(String name, double cost) throws SQLException {
		PreparedStatement prepStmt = null;
		String query = "DELETE FROM menu WHERE menuItem = ? AND price = ?";
		try {
			prepStmt = connection.prepareStatement(query);
			prepStmt.setString(1, name);
			prepStmt.setDouble(2, cost);
			prepStmt.execute();
			prepStmt.close();
			System.out.println("Record deleted");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			prepStmt.close();
		}
	}
	
	/**
	 * Takes string inputs from the GUI prompts for the username, activity description, and time stamp, then inserts a new record
	 * with all of these into the activityLog table for managers to track.
	 * @param user : String specifying the username of a staff account
	 * @param act : String describing an activity or action the user currently logged in has performed
	 * @param timestamp : String formatted with the date and time the activity was performed
	 * @throws SQLException 
	 */
	public void saveActivityEntryToDB(String user, String act, String timestamp) throws SQLException {
		PreparedStatement prepStmt = null;
		String query = "INSERT INTO activityLog (username, activityEntry, time) VALUES (? ,?, ?)";
		try {
			prepStmt = connection.prepareStatement(query);
			prepStmt.setString(1, user);
			prepStmt.setString(2, act);
			prepStmt.setString(3, timestamp);
			prepStmt.execute();
			prepStmt.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			prepStmt.close();
		}
	}
	
	// Export queries ========================================================
	
	/**
	 * Searches both stored orders and (working) orders tables for all of their records and returns an ArrayList containing every order 
	 * in the system ready for display in a table
	 * @return ArrayList<Order> targetOrders: all the order objects in the system
	 * @throws SQLException 
	 */
	ArrayList<Order> getAllOrders() throws SQLException {
		ArrayList<Order> targetOrders = new ArrayList<Order>();
		PreparedStatement prepStmt = null;
		ResultSet resSet = null;
		
		String query = "SELECT * FROM orders UNION ALL SELECT * FROM storedOrders";
		
		try {
			prepStmt = connection.prepareStatement(query);
			resSet = prepStmt.executeQuery();
			
			while (resSet.next()) {
				targetOrders.add(new Order(resSet.getInt("tableNo"), resSet.getString("orderList"),
						resSet.getString("totalPrice"), resSet.getString("specialRequests"),
						resSet.getString("comments"), resSet.getString("isComplete"), resSet.getString("date"),
						resSet.getString("time")));
			}
			
			prepStmt.close();
			resSet.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			prepStmt.close();
			resSet.close();
		}
		
		return targetOrders;
	}
	
	// Import Queries =========================================================
	
	/**
	 * Takes a list of order objects (in this context imported from a CSV file) and then saves each of them into the stored records
	 * table in the database
	 * @param csvImport : an ObservableList<Order> holding all of the orders you want to save to the stored orders table 
	 * (orders in this context were read from a CSV file opened on the import page with a FileChooser)
	 * @throws SQLException 
	 */
	void saveImportToDB(ObservableList<Order> csvImport) throws SQLException {
		PreparedStatement prepStmt = null;
		try {
			for (Order o : csvImport) {
				String query = "INSERT INTO storedOrders (tableNo, orderList, totalPrice, specialRequests, comments, isComplete,"
						+ " date, time) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
				prepStmt = connection.prepareStatement(query);
				prepStmt.setInt(1, o.getTableNo());
				prepStmt.setString(2, o.getOrderList());
				prepStmt.setDouble(3, Double.parseDouble(o.getTotalPrice()));
				prepStmt.setString(4, o.getSpecialRequests());
				prepStmt.setString(5, o.getComments());
				prepStmt.setString(6, o.getCompleted());
				prepStmt.setString(7, o.getDate());
				prepStmt.setString(8, o.getTime());
				prepStmt.execute();
				prepStmt.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			prepStmt.close();
		}
	}
	
}
