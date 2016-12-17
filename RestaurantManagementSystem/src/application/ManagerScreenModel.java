package application;

import java.sql.*;
import java.util.ArrayList;

import javafx.collections.ObservableList;

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
	
	void addStaffAccountIntoDB(int id, String user, String pw, String managerVerified) {
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
	
	void deleteStaffAccountFromDB(int id, String user, String pw, String managerVerified) {
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
	
	ArrayList<ActivityLog> getActivityLogForEmployee(String user) {
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
		}
		
		return targetActivity;
	}
	
	// ========================= Menu queries ==================================
	
	void addNewDishToMenuDB(String name, double cost) {
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
		}
	}
	
	void deleteDishFromMenuDB(String name, double cost) {
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
	
	// Export queries ========================================================
	
	ArrayList<Order> getAllOrders() {
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
		}
		
		return targetOrders;
	}
	
	// Import Queries =========================================================
	
	void saveImportToDB(ObservableList<Order> csvImport) {
		try {
			for (Order o : csvImport) {
				PreparedStatement prepStmt = null;
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
		}
	}
	
}
