package application;

import java.sql.*; 

//This class makes use of a 3rd party library, the SQLite JDBC Driver 
//available from https://bitbucket.org/xerial/sqlite-jdbc/downloads

/**
 * The LoginModel class contains all the methods that the LoginScreenController class uses to access and interact with the database, 
 * this class focuses on verifying the login details a potential user may enter, it also initialises the necessary tables that this 
 * application will need to function at the bare minimum if the local RestaurantDB.sqlite file is deleted and a new one is created 
 * with no tables by default. There is also a special emergency admin account inserted into the database to allow access back in if the 
 * database is deleted.
 * @author jarrod joshua
 */

public class LoginModel {
	
	private Connection connection;
	
	/**
	 * The LoginModel class constructor initialises a connection to the SQLite database and initialises tables if they are not found
	 * The methods and structure in this class and its children are derived from the tutorials available from the ProgrammingKnowledge playlist 
	 * JavaFX tutorial for beginners : https://www.youtube.com/watch?v=9YrmON6nlEw&list=PLS1QulWo1RIaUGP446_pWLgTZPiFizEMq
	 */
	LoginModel() {
		connection = SqliteConnection.Connector();
		if (connection == null) {
			System.exit(1);
		}
		
		// Create tables if they don't exist
		
		initialiseStaffTable();
		initOrdersTable();
		initMenuTable();
		initStoredOrdersTable();
		initActivityLogTable() ;
	}
	
	/**
	 * Determines if the database is connected.
	 * @return boolean : true if the database is connected, false is there is no connection found.
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
	
	/**
	 * Determines if user has staff level access only.
	 * This method takes a username password string pair entered into the login GUI and then searches the database to determine if 
	 * the login input is in the system and if it allows staff level access to the application, i.e. registering orders etc but not manager level 
	 * admin.
	 * @param user A string input that will be searched for in 'username' field in the 'staff' table
	 * @param pass A string input that will be searched for in the 'password' field in the 'staff' table
	 * @return
	 * @throws SQLException
	 */
	boolean verifyStaffLogin(String user, String pass) throws SQLException {
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		//String query = "select * from staff where username = ? and password = ?"; 
		String query = "select * from staff where username = ? and password = ? and isManager = 'false'";
		// query database for username and password from user and password arguments and for false isManager
		try {
			preparedStatement = connection.prepareStatement(query);
			
			preparedStatement.setString(1, user);
			preparedStatement.setString(2, pass);
			//preparedStatement.setString(3, "true");
			
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				return true;
			} else {
				return false;
			}
			
		} catch (Exception e) {
			return false;
		} finally {
			preparedStatement.close();
			resultSet.close();
		}
	}
	
	/**
	 * Determines is user has manager access.
	 * This method takes a username password string pair entered into the login GUI and then searches the database to determine if 
	 * the login input is in the system and if it allows manager level access to the application, full access to all features of the 
	 * application
	 * @param user A string input that will be searched for in 'username' field in the 'staff' table
	 * @param pass A string input that will be searched for in the 'password' field in the 'staff' table
	 * @return
	 * @throws SQLException
	 */
	boolean verifyManagerLogin(String user, String pass) throws SQLException {
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		//String query = "select * from staff where username = ? and password = ?"; 
		String query = "select * from staff where username = ? and password = ? and isManager = 'true'";
		// query database for username and password from user and password arguments and isManager = true
		try {
			preparedStatement = connection.prepareStatement(query);
			
			preparedStatement.setString(1, user);
			preparedStatement.setString(2, pass);
			//preparedStatement.setString(3, "true");
			
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				return true;
			} else {
				return false;
			}
			
		} catch (Exception e) {
			return false;
		} finally {
			preparedStatement.close();
			resultSet.close();
		}
	}
	
	/**
	 * This method saves a record describing an action the currently logged in user has just performed and saves an entry with
	 * the action, time and username of the currently logged in account into the database
	 * the managers can look through
	 * @param user the 'staff'username attribute of the currently logged in user
	 * @param act a string describing what the current user has just done for tracking user activity in th logs managers see
	 * @param timestamp a formatted string containing the date and time of the action
	 */
	public void saveActivityEntryToDB(String user, String act, String timestamp) {
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
		} 
	}
	
	/**
	 * Creates a staff table if one does not already exist and also inserts a record allowing a special admin account user to login
	 * This is included in case the database file is deleted and there is still an emergency way back into the application.
	 */
	void initialiseStaffTable() {
		PreparedStatement prepStmt = null;
		try {
			String query = "CREATE TABLE IF NOT EXISTS 'staff' "
					+ "( 'username'	TEXT, 'password' TEXT, 'id'	INTEGER, 'isManager' TEXT , PRIMARY KEY ('id') )";
			prepStmt = connection.prepareStatement(query);
			prepStmt.execute();
			prepStmt.close();
			insertAdminAccount();
			/*
			if (ifNoTable == true) {
				System.out.println("No table was found so we initialise it here with an admin account to fix anything : admin, password");
				prepStmt.close();
				// insert a row
				insertAdminAccount();
			} else {
				System.out.println("staff table exists.");
			}
			*/
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Inserts a special admin account into the staff table, allowing access back into the application if the database file is not found
	 */
	void insertAdminAccount() {
		PreparedStatement prepStmt = null;
		String query = "INSERT INTO staff (id, username, password, isManager) VALUES (99 , 'admin', 'password', 'true')";
		try {
			prepStmt = connection.prepareStatement(query);
			prepStmt.execute();
			prepStmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Most Likely the admin account has already been inserted and cannot be overwritten");
		} 
	}
	
	/**
	 * Creates an orders table in case it does not already exist in the event of the database file being lost.
	 * This table holds working orders of tables currently being served
	 */
	void initOrdersTable() {
		PreparedStatement prepStmt = null;
		String query = "CREATE TABLE IF NOT EXISTS 'orders' "
				+ " ('tableNo' INTEGER, 'orderList' TEXT, 'totalPrice' REAL, 'comments' TEXT, 'specialRequests' TEXT, "
				+ " 'isComplete' TEXT, 'date' TEXT, 'time' TEXT, PRIMARY KEY ('tableNo') ) ";
		try {
			prepStmt = connection.prepareStatement(query);
			prepStmt.execute();
			prepStmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	/**
	 * Creates a menu table in case it does not already exist in the event of the database file being lost.
	 * This table holds the current menu the restaurant offers.
	 */
	void initMenuTable() {
		PreparedStatement prepStmt = null;
		String query = "CREATE TABLE IF NOT EXISTS 'menu' "
				+ " ( 'menuItem' TEXT, 'price' REAL )";
		try {
			prepStmt = connection.prepareStatement(query);
			prepStmt.execute();
			prepStmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	} 
	
	/**
	 * Creates an storedOrders table in case it does not already exist in the event of the database file being lost.
	 * Ths table holds records of prevoisly completed orders or orders imported by managers, they are kept separate from 
	 * working orders so as to avoid confusion and ambiguity with working orders which are the immediate priority of the 
	 * staff to deal with.
	 */
	void initStoredOrdersTable() {
		PreparedStatement prepStmt = null;
		String query = "CREATE TABLE IF NOT EXISTS 'storedOrders' "
				+ " ('tableNo' INTEGER, 'orderList' TEXT, 'totalPrice' REAL, 'comments' TEXT, 'specialRequests' TEXT, "
				+ " 'isComplete' TEXT, 'date' TEXT, 'time' TEXT ) ";
		try {
			prepStmt = connection.prepareStatement(query);
			prepStmt.execute();
			prepStmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	/**
	 * Creates an activityLog table in case it does not already exist in the event of the database file being lost.
	 * This is where all the user information is stored and managers can track their employees behaviour on the program.
	 */
	void initActivityLogTable() {
		PreparedStatement prepStmt = null;
		String query = "CREATE TABLE IF NOT EXISTS 'activityLog' "
				+ "( 'username' TEXT, 'activityEntry' TEXT, 'time' TEXT )";
		try {
			prepStmt = connection.prepareStatement(query);
			prepStmt.execute();
			prepStmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}