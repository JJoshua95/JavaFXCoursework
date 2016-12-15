package application;

import java.sql.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class ManagerScreenModel extends StaffScreenModel {
	
private Connection connection;
	
	ManagerScreenModel() {
		connection = SqliteConnection.Connector();
		if (connection == null) {
			System.exit(1);
		}
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
	
	// ============ Staff queries =================
	
	ArrayList<Staff> getAllEmployeesFromDB() {
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
		}
		
		return targetStaff;
	}
	
	void AddStaffAccountIntoDB(int id, String user, String pw, String managerVerified) {
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
		}
	}
	
	void DeleteStaffAccountFromDB(int id, String user, String pw, String managerVerified) {
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
		}
	}
	
	// ========================= Menu queries ==================================
	
	ArrayList<Food> getAllFoodFromMenu() {
		ArrayList<Food> targetFoods = new ArrayList<Food>();
		PreparedStatement prepStmt = null;
		ResultSet resSet = null;
		
		String query = "SELECT * FROM menu";
		
		try {
			prepStmt = connection.prepareStatement(query);
			resSet = prepStmt.executeQuery();
			
			while (resSet.next()) {
				targetFoods.add(new Food(resSet.getString("menuItem"), resSet.getDouble("price")));
			}
			prepStmt.close();
			resSet.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		} 
		
		return targetFoods;
	}
	
}
