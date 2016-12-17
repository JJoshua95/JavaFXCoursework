package application;

import java.sql.*;

public class LoginModel {
	
	private Connection connection;
	
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
	
	boolean isDbConnected() {
		try {
			return !connection.isClosed();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	boolean verifyStaffLogin(String user, String pass) throws SQLException {
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		//String query = "select * from staff where username = ? and password = ?"; 
		String query = "select * from staff where username = ? and password = ? and isManager = 'false'";
		// query database for username and password from user and password args and for false isManager
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
		} 
	}
	
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
	
	void initActivityLogTable() {
		PreparedStatement prepStmt = null;
		String query = "CREATE TABLE IF NOT EXISTS 'activityLog' "
				+ " '( username' TEXT, 'activityEntry' TEXT, 'time' TEXT )";
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