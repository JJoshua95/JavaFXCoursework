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
	
}