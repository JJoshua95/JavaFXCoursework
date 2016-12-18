package application;

import java.sql.*;
import java.util.Date;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javafx.collections.ObservableList;

//This class makes use of a 3rd party library, the SQLite JDBC Driver 
//available from https://bitbucket.org/xerial/sqlite-jdbc/downloads

/**
 * This class handles the database interactions needed for the StaffScreenController
 * @author jarrod joshua
 */
public class StaffScreenModel extends LoginModel {
	
	private Connection connection;
	
	/**
	 * The class constructor checks whether the connection to the database is active, if not the
	 * program is closed.
	 */
	StaffScreenModel() {
		connection = SqliteConnection.Connector();
		if (connection == null) {
			System.exit(1);
		}
	}
	
	/**
	 * Returns true if the database is connected, false if it is not.
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
	 * Checks if the input tableNo already has a working order in the database, if it does it updates all fields apart from the time of order, 
	 * (and the tableNo as that wouldn't make sense). If there is not working order for the inputted table then the record is simply inserted.
	 * @param tableNum
	 * @param orderList
	 * @param totalBill
	 * @param specialReqs
	 * @param comments
	 * @param isComplete
	 * @param inDate
	 * @throws SQLException
	 */
	void saveCurrentOrderToDatabase(int tableNum, ObservableList<Food> orderList, double totalBill, String specialReqs,
			String comments, String isComplete, Date inDate) throws SQLException {

		// String[] foodItemArr; // array to hold strings of the items in the
		// form [name:cost, name:cost, ... ]
		ArrayList<String> foodItemList = new ArrayList<String>();

		// Store the food price and costs into a single array that we will write
		// to a database cell as a string
		for (Food f : orderList) {
			String foodNameString;
			String foodPriceString;
			foodNameString = f.getMenuItemName();
			DecimalFormat df = new DecimalFormat("#.00"); // to print the price
															// to 2dp i.e. to
															// the nearest penny
			foodPriceString = df.format(f.getPrice());
			foodItemList.add("" + foodNameString + ":" + foodPriceString);
		}

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = dateFormat.format(inDate);
		DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		String timeString = timeFormat.format(inDate);

		PreparedStatement preparedStatement = null; // to hold the query
		ResultSet resultSet = null;
		String query;

		try {
			// find if record does not exist

			query = "SELECT * FROM orders WHERE tableNo = ?";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, tableNum);
			resultSet = preparedStatement.executeQuery();

			// if there is no record for this table insert one
			if (!resultSet.next()) {
				preparedStatement.close();
				resultSet.close();
				System.out.println("No order for this table was found so new record created");

				query = "INSERT INTO orders "
						+ "(tableNo, orderList, totalPrice, specialRequests, comments, isComplete, date, time) "
						+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

				preparedStatement = connection.prepareStatement(query);
				preparedStatement.setInt(1, tableNum);
				preparedStatement.setString(2, foodItemList.toString());
				preparedStatement.setDouble(3, totalBill);
				preparedStatement.setString(4, specialReqs);
				preparedStatement.setString(5, comments);
				preparedStatement.setString(6, isComplete);
				preparedStatement.setString(7, dateString);
				preparedStatement.setString(8, timeString);

				preparedStatement.execute();
				preparedStatement.close();

			} else { // If there is already a record for this table update
						// all but the time and date
				System.out.println("Table order exists, updating it now");
				preparedStatement.close();
				resultSet.close();

				query = "UPDATE orders "
						+ "SET orderList = ?, totalPrice = ?, specialRequests = ?, comments = ?, isComplete = ? "
						+ "WHERE tableNo = ?";

				preparedStatement = connection.prepareStatement(query);
				preparedStatement.setString(1, foodItemList.toString());
				preparedStatement.setDouble(2, totalBill);
				preparedStatement.setString(3, specialReqs);
				preparedStatement.setString(4, comments);
				preparedStatement.setString(5, isComplete);
				preparedStatement.setInt(6, tableNum);

				preparedStatement.execute();
				preparedStatement.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			preparedStatement.close();
			resultSet.close();
		}

	}
	
	/**
	 * This takes a table number and queries the database to find the food items saved in that table order and puts them in 
	 * an ArrayList that can be passed to the listviews and displayed with ease.
	 * @param tableNoVal
	 * @return ArrayList<Food> targetOrder
	 * @throws SQLException
	 */
	ArrayList<Food> retrieveATableOrderFromDB(int tableNoVal) throws SQLException {
		String targetOrder = null;
		String[] stringOrderArray = null;
		PreparedStatement prepStmt = null;
		ResultSet resSet = null;
		ArrayList<Food> orderArrayList = new ArrayList<Food>();

		try {

			String query = "SELECT orderList FROM orders WHERE tableNo = ?";
			prepStmt = connection.prepareStatement(query);
			prepStmt.setInt(1, tableNoVal);
			resSet = prepStmt.executeQuery();

			if (resSet.next()) {

				// System.out.println(resSet.getString(1));
				targetOrder = resSet.getString("orderList"); // .getString(1);
																// // get the
																// first string
																// in the column
																// as there
																// should only
																// be a single
																// string there
				targetOrder = targetOrder.replace("[", "");
				targetOrder = targetOrder.replace("]", ""); // get rid of square
															// brackets
				stringOrderArray = targetOrder.split(","); // split the string
															// into individual
															// name:price pairs
				// System.out.println(targetOrder);

				for (int i = 0; i < stringOrderArray.length; i++) {
					String words[];
					words = stringOrderArray[i].split(":");
					String loopName = words[0];
					double loopPrice = Double.parseDouble(words[1]);
					Food f = new Food(loopName, loopPrice);
					orderArrayList.add(f);
				}

			} else {
				System.out.println("No order found for that table");
			}

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			prepStmt.close();
			resSet.close();
		}

		return orderArrayList;

	}
	
	/**
	 * Takes a table number input and then retrieves the total bill associated with that tables order from 
	 * the database as a double.
	 * @param tableNum
	 * @return Double totalFromDB the total price of the ordered items from an order
	 */
	Double getTotalPriceFromDB(int tableNum) {
		double totalFromDB = 0.0;
		PreparedStatement prepStmt = null;
		ResultSet resSet = null;
		String query = "SELECT totalPrice FROM orders WHERE tableNo = ?";
		try {
			prepStmt = connection.prepareStatement(query);
			prepStmt.setInt(1, tableNum);
			resSet = prepStmt.executeQuery();
			totalFromDB = resSet.getDouble("totalPrice");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return totalFromDB;
	}
	
	/**
	 * Takes a table number input and then fetches the special requests entry of that tables order from the database as a string.
	 * @param tableNum
	 * @return specialReqs String
	 */
	String getSpecialRequestsFromDB(int tableNum) {
		String specialReqs = null;
		PreparedStatement prepStmt = null;
		ResultSet resSet = null;
		String query = "SELECT specialRequests FROM orders WHERE tableNo = ?";
		try {
			prepStmt = connection.prepareStatement(query);
			prepStmt.setInt(1, tableNum);
			resSet = prepStmt.executeQuery();
			specialReqs = resSet.getString("specialRequests");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return specialReqs;
	}
	
	/**
	 * Takes a table number input and then retrieves the comments from an order as a sring
	 * @param tableNum
	 * @return comments String
	 */
	String getCommentsFromDB(int tableNum) {
		String comment = null;
		PreparedStatement prepStmt = null;
		ResultSet resSet = null;
		String query = "SELECT comments FROM orders WHERE tableNo = ?";
		try {
			prepStmt = connection.prepareStatement(query);
			prepStmt.setInt(1, tableNum);
			resSet = prepStmt.executeQuery();
			comment = resSet.getString("comments");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return comment;
	}
	
	/**
	 * Takes a table number input and finds whether the order has been marked as complete or not as a string.
	 * @param tableNum
	 * @return String isComplete, either true or false , not boolean but string outputs.
	 */
	String getIsComplete(int tableNum) {
		String complete = null;
		PreparedStatement prepStmt = null;
		ResultSet resSet = null;
		String query = "SELECT isComplete FROM orders WHERE tableNo = ?";
		try {
			prepStmt = connection.prepareStatement(query);
			prepStmt.setInt(1, tableNum);
			resSet = prepStmt.executeQuery();
			complete = resSet.getString("isComplete");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return complete;
	}
	
	/**
	 * Takes a table number input and then outputs a date and a time that the order was first saved at, as a single string
	 * @param tableNum
	 * @return date String
	 */
	String getDateToDisplay(int tableNum) {
		String dateStringForLabel = null;
		PreparedStatement prepStmt = null;
		ResultSet resSet = null;
		String query = "SELECT date, time FROM orders WHERE tableNo = ?";
		try {
			prepStmt = connection.prepareStatement(query);
			prepStmt.setInt(1, tableNum);
			resSet = prepStmt.executeQuery();
			String dateStr = resSet.getString("date");
			String timeStr = resSet.getString("time");
			dateStringForLabel = "" + dateStr + " " + timeStr;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dateStringForLabel;
	}
	
	/**
	 * Takes a table number input, finds the corresponding working order and then removes it completely from the system.
	 * @param tableNum
	 */
	void deleteCurrentOrderFromDB(int tableNum) {
		PreparedStatement prepStmt = null;

		String query = "DELETE FROM orders WHERE tableNo = ?";
		try {
			prepStmt = connection.prepareStatement(query);
			prepStmt.setInt(1, tableNum);
			prepStmt.execute();
			// connection.commit();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Failed to delete record");
			e.printStackTrace();

		}

	}
	
	/**
	 * Takes all the order components and then inserts the order into the stored orders (not the current working orders)
	 * this is combined with deleting the same order from the current orders therefore having the effect of moving the order 
	 * from working to stored.
	 * @param tableNum
	 * @param orderList
	 * @param totalBill
	 * @param specialReqs
	 * @param comments
	 * @param isComplete
	 * @param inDate
	 * @throws SQLException
	 */
	void moveCurrentOrderToStorage(int tableNum, ObservableList<Food> orderList, double totalBill, String specialReqs,
			String comments, String isComplete, Date inDate) throws SQLException {

		// String[] foodItemArr; // array to hold strings of the items in the
		// form [name:cost, name:cost, ... ]
		ArrayList<String> foodItemList = new ArrayList<String>();

		// Store the food price and costs into a single array that we will write
		// to a database cell as a string
		for (Food f : orderList) {
			String foodNameString;
			String foodPriceString;
			foodNameString = f.getMenuItemName();
			DecimalFormat df = new DecimalFormat("#.00"); // to print the price
															// to 2dp i.e. to
															// the nearest penny
			foodPriceString = df.format(f.getPrice());
			foodItemList.add("" + foodNameString + ":" + foodPriceString);
		}

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = dateFormat.format(inDate);
		DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		String timeString = timeFormat.format(inDate);

		PreparedStatement preparedStatement = null; // to hold the query

		String query;
		// if the order is marked as complete we can move it out from the
		// current orders and store it
		try {
			if (isComplete.equals("true")) {
				query = "INSERT INTO storedOrders "
						+ "(tableNo, orderList, totalPrice, specialRequests, comments, isComplete, date, time) "
						+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

				preparedStatement = connection.prepareStatement(query);
				preparedStatement.setInt(1, tableNum);
				preparedStatement.setString(2, foodItemList.toString());
				preparedStatement.setDouble(3, totalBill);
				preparedStatement.setString(4, specialReqs);
				preparedStatement.setString(5, comments);
				preparedStatement.setString(6, isComplete);
				preparedStatement.setString(7, dateString);
				preparedStatement.setString(8, timeString);

				preparedStatement.execute(); // store the order into the stored
												// orders

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// ------------------------------ Deal with TableView queries ------------------------
	
	/**
	 * Takes a table number integer input, and takes a string input group which determines which table in the database to search ('stored orders'
	 * or just 'orders' for the working orders). This method then queries the database to retrieve any orders with the inputted table number
	 * @param group
	 * @param tableNum
	 * @return ArrayList<Order> targetOrders: a list of orders which correspond with the inputs to look for
	 */
	ArrayList<Order> searchByTableNo(String group, int tableNum) {
		ArrayList<Order> targetOrders = new ArrayList<Order>();
		PreparedStatement prepStmt = null;
		ResultSet resSet = null;

		try {
			if (group.equals("Current Orders")) {
				String query = "SELECT * FROM orders WHERE tableNo = ?";
				prepStmt = connection.prepareStatement(query);
				prepStmt.setInt(1, tableNum);
				resSet = prepStmt.executeQuery();
				while (resSet.next()) {
					targetOrders.add(new Order(resSet.getInt("tableNo"), resSet.getString("orderList"),
							resSet.getString("totalPrice"), resSet.getString("specialRequests"),
							resSet.getString("comments"), resSet.getString("isComplete"), resSet.getString("date"),
							resSet.getString("time")));
				}
				prepStmt.close();
				resSet.close();
			} else {
				String query = "SELECT * FROM storedOrders WHERE tableNo = ?";
				prepStmt = connection.prepareStatement(query);
				prepStmt.setInt(1, tableNum);
				resSet = prepStmt.executeQuery();
				while (resSet.next()) {
					targetOrders.add(new Order(resSet.getInt("tableNo"), resSet.getString("orderList"),
							resSet.getString("totalPrice"), resSet.getString("specialRequests"),
							resSet.getString("comments"), resSet.getString("isComplete"), resSet.getString("date"),
							resSet.getString("time")));
				}
				prepStmt.close();
				resSet.close();
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return targetOrders;
	}
	
	/**
	 * Takes a string input which is aimed at finding orders with specified food items, 
	 * and takes a string input group which determines which table in the database to search ('stored orders'
	 * or just 'orders' for the working orders). This method then queries the database to retrieve any orders which contain 
	 * specified food items in their orders.
	 * @param group
	 * @param tableNum
	 * @return ArrayList<Order> targetOrders: a list of orders which correspond with the inputs to look for
	 */
	ArrayList<Order> searchByFoodItem(String group, String inputItem) {
		ArrayList<Order> targetOrders = new ArrayList<Order>();
		PreparedStatement prepStmt = null;
		ResultSet resSet = null;
		String targetString = "%" + inputItem + "%";
		//System.out.println(targetString);
		try {
			if (group.equals("Current Orders")) {
				String query = "SELECT * FROM orders WHERE orderList LIKE ?";
				prepStmt = connection.prepareStatement(query);
				prepStmt.setString(1, targetString);
				resSet = prepStmt.executeQuery();
				while (resSet.next()) {
					targetOrders.add(new Order(resSet.getInt("tableNo"), resSet.getString("orderList"),
							resSet.getString("totalPrice"), resSet.getString("specialRequests"),
							resSet.getString("comments"), resSet.getString("isComplete"), resSet.getString("date"),
							resSet.getString("time")));
				}
				prepStmt.close();
				resSet.close();
			} else {
				String query = "SELECT * FROM storedOrders WHERE orderList LIKE ?";
				prepStmt = connection.prepareStatement(query);
				prepStmt.setString(1, targetString);
				resSet = prepStmt.executeQuery();
				while (resSet.next()) {
					targetOrders.add(new Order(resSet.getInt("tableNo"), resSet.getString("orderList"),
							resSet.getString("totalPrice"), resSet.getString("specialRequests"),
							resSet.getString("comments"), resSet.getString("isComplete"), resSet.getString("date"),
							resSet.getString("time")));
				}
				prepStmt.close();
				resSet.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println(targetOrders.toString());
		return targetOrders;
	}
	
	/**
	 * Takes a string input which is aimed at finding orders with specified special requests, 
	 * and takes a string input group which determines which table in the database to search ('stored orders'
	 * or just 'orders' for the working orders). This method then queries the database to retrieve any orders which contain
	 * the specified special requests strings in their orders.
	 * @param group
	 * @param tableNum
	 * @return ArrayList<Order> targetOrders: a list of orders which correspond with the inputs to look for
	 */
	ArrayList<Order> searchBySpecialRequest(String group, String inputRequest) {
		ArrayList<Order> targetOrders = new ArrayList<Order>();
		PreparedStatement prepStmt = null;
		ResultSet resSet = null;
		String targetString = "%" + inputRequest + "%";
		//System.out.println(targetString);
		try {
			if (group.equals("Current Orders")) {
				String query = "SELECT * FROM orders WHERE specialRequests LIKE ?";
				prepStmt = connection.prepareStatement(query);
				prepStmt.setString(1, targetString);
				resSet = prepStmt.executeQuery();
				while (resSet.next()) {
					targetOrders.add(new Order(resSet.getInt("tableNo"), resSet.getString("orderList"),
							resSet.getString("totalPrice"), resSet.getString("specialRequests"),
							resSet.getString("comments"), resSet.getString("isComplete"), resSet.getString("date"),
							resSet.getString("time")));
				}
				prepStmt.close();
				resSet.close();
			} else {
				String query = "SELECT * FROM storedOrders WHERE specialRequests LIKE ?";
				prepStmt = connection.prepareStatement(query);
				prepStmt.setString(1, targetString);
				resSet = prepStmt.executeQuery();
				while (resSet.next()) {
					targetOrders.add(new Order(resSet.getInt("tableNo"), resSet.getString("orderList"),
							resSet.getString("totalPrice"), resSet.getString("specialRequests"),
							resSet.getString("comments"), resSet.getString("isComplete"), resSet.getString("date"),
							resSet.getString("time")));
				}
				prepStmt.close();
				resSet.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return targetOrders;

	}
	
	/**
	 * Takes a string input which is aimed at finding orders with specified comments, 
	 * and takes a string input group which determines which table in the database to search ('stored orders'
	 * or just 'orders' for the working orders). This method then queries the database to retrieve any orders which contain
	 * the specified comments strings in their orders.
	 * @param group
	 * @param tableNum
	 * @return ArrayList<Order> targetOrders: a list of orders which correspond with the inputs to look for
	 */
	ArrayList<Order> searchByComments(String group, String inputComment) {
		ArrayList<Order> targetOrders = new ArrayList<Order>();
		PreparedStatement prepStmt = null;
		ResultSet resSet = null;
		String targetString = "%" + inputComment + "%";
		//System.out.println(targetString);
		try {
			if (group.equals("Current Orders")) {
				String query = "SELECT * FROM orders WHERE comments LIKE ?";
				prepStmt = connection.prepareStatement(query);
				prepStmt.setString(1, targetString);
				resSet = prepStmt.executeQuery();
				while (resSet.next()) {
					targetOrders.add(new Order(resSet.getInt("tableNo"), resSet.getString("orderList"),
							resSet.getString("totalPrice"), resSet.getString("specialRequests"),
							resSet.getString("comments"), resSet.getString("isComplete"), resSet.getString("date"),
							resSet.getString("time")));
				}
				prepStmt.close();
				resSet.close();
			} else {
				String query = "SELECT * FROM storedOrders WHERE comments LIKE ?";
				prepStmt = connection.prepareStatement(query);
				prepStmt.setString(1, targetString);
				resSet = prepStmt.executeQuery();
				while (resSet.next()) {
					targetOrders.add(new Order(resSet.getInt("tableNo"), resSet.getString("orderList"),
							resSet.getString("totalPrice"), resSet.getString("specialRequests"),
							resSet.getString("comments"), resSet.getString("isComplete"), resSet.getString("date"),
							resSet.getString("time")));
				}
				prepStmt.close();
				resSet.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return targetOrders;

	}
	
	/**
	 * Takes a string input which is aimed at finding orders whih bills fall within a specified price range, 
	 * and takes a string input group which determines which table in the database to search ('stored orders'
	 * or just 'orders' for the working orders). This method then queries the database to retrieve any orders which contain
	 * the specified special requests strings in their orders.
	 * @param group
	 * @param tableNum
	 * @return ArrayList<Order> targetOrders: a list of orders which correspond with the inputs to look for
	 */
	ArrayList<Order> searchByPrice(String group, double inputStart, double inputEnd) {
		ArrayList<Order> targetOrders = new ArrayList<Order>();
		PreparedStatement prepStmt = null;
		ResultSet resSet = null;
		try {
			if (group.equals("Current Orders")) {
				String query = "SELECT * FROM orders WHERE totalPrice BETWEEN ? AND ?";
				prepStmt = connection.prepareStatement(query);
				prepStmt.setDouble(1, inputStart);
				prepStmt.setDouble(2, inputEnd);
				resSet = prepStmt.executeQuery();
				while (resSet.next()) {
					targetOrders.add(new Order(resSet.getInt("tableNo"), resSet.getString("orderList"),
							resSet.getString("totalPrice"), resSet.getString("specialRequests"),
							resSet.getString("comments"), resSet.getString("isComplete"), resSet.getString("date"),
							resSet.getString("time")));
				}
				prepStmt.close();
				resSet.close();
			} else {
				String query = "SELECT * FROM storedOrders WHERE totalPrice BETWEEN ? AND ?";
				prepStmt = connection.prepareStatement(query);
				prepStmt.setDouble(1, inputStart);
				prepStmt.setDouble(2, inputEnd);
				resSet = prepStmt.executeQuery();
				while (resSet.next()) {
					targetOrders.add(new Order(resSet.getInt("tableNo"), resSet.getString("orderList"),
							resSet.getString("totalPrice"), resSet.getString("specialRequests"),
							resSet.getString("comments"), resSet.getString("isComplete"), resSet.getString("date"),
							resSet.getString("time")));
				}
				prepStmt.close();
				resSet.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return targetOrders;
	}
	
	/**
	 * Takes a string input ('true', or 'false') which is aimed at finding orders which have been marked as complete or not complete, 
	 * and takes a string input group which determines which table in the database to search ('stored orders'
	 * or just 'orders' for the working orders). This method then queries the database to retrieve any orders which contain
	 * the specified special requests strings in their orders.
	 * @param group
	 * @param tableNum
	 * @return ArrayList<Order> targetOrders: a list of orders which correspond with the inputs to look for
	 */
	ArrayList<Order> searchByIfComplete(String group, String inputWhetherComplete) {
		ArrayList<Order> targetOrders = new ArrayList<Order>();
		PreparedStatement prepStmt = null;
		ResultSet resSet = null;
		try {
			if (group.equals("Current Orders")) {
				String query = "SELECT * FROM orders WHERE isComplete = ?";
				prepStmt = connection.prepareStatement(query);
				prepStmt.setString(1, inputWhetherComplete);
				resSet = prepStmt.executeQuery();
				while (resSet.next()) {
					targetOrders.add(new Order(resSet.getInt("tableNo"), resSet.getString("orderList"),
							resSet.getString("totalPrice"), resSet.getString("specialRequests"),
							resSet.getString("comments"), resSet.getString("isComplete"), resSet.getString("date"),
							resSet.getString("time")));
				}
				prepStmt.close();
				resSet.close();
			} else {
				String query = "SELECT * FROM storedOrders WHERE isComplete = ?";
				prepStmt = connection.prepareStatement(query);
				prepStmt.setString(1, inputWhetherComplete);
				resSet = prepStmt.executeQuery();
				while (resSet.next()) {
					targetOrders.add(new Order(resSet.getInt("tableNo"), resSet.getString("orderList"),
							resSet.getString("totalPrice"), resSet.getString("specialRequests"),
							resSet.getString("comments"), resSet.getString("isComplete"), resSet.getString("date"),
							resSet.getString("time")));
				}
				prepStmt.close();
				resSet.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return targetOrders;
	}
	
	/**
	 * Takes a date formatted string string which is aimed at finding orders which were registered on a specified date, 
	 * and takes a string input group which determines which table in the database to search ('stored orders'
	 * or just 'orders' for the working orders). This method then queries the database to retrieve any orders which contain
	 * the specified date strings in their orders.
	 * @param group
	 * @param tableNum
	 * @return ArrayList<Order> targetOrders: a list of orders which correspond with the inputs to look for
	 */
	ArrayList<Order> searchByDate(String group, String inputDate) {
		ArrayList<Order> targetOrders = new ArrayList<Order>();
		PreparedStatement prepStmt = null;
		ResultSet resSet = null;
		try {
			if (group.equals("Current Orders")) {
				String query = "SELECT * FROM orders WHERE date = ?";
				prepStmt = connection.prepareStatement(query);
				prepStmt.setString(1, inputDate);
				resSet = prepStmt.executeQuery();
				while (resSet.next()) {
					targetOrders.add(new Order(resSet.getInt("tableNo"), resSet.getString("orderList"),
							resSet.getString("totalPrice"), resSet.getString("specialRequests"),
							resSet.getString("comments"), resSet.getString("isComplete"), resSet.getString("date"),
							resSet.getString("time")));
				}
				prepStmt.close();
				resSet.close();
			} else {
				String query = "SELECT * FROM storedOrders WHERE date = ?";
				prepStmt = connection.prepareStatement(query);
				prepStmt.setString(1, inputDate);
				resSet = prepStmt.executeQuery();
				while (resSet.next()) {
					targetOrders.add(new Order(resSet.getInt("tableNo"), resSet.getString("orderList"),
							resSet.getString("totalPrice"), resSet.getString("specialRequests"),
							resSet.getString("comments"), resSet.getString("isComplete"), resSet.getString("date"),
							resSet.getString("time")));
				}
				prepStmt.close();
				resSet.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return targetOrders;
	}
	
	/**
	 * Takes a time formatted string string which is aimed at finding orders which were registered within specified time intervals, 
	 * and takes a string input group which determines which table in the database to search ('stored orders'
	 * or just 'orders' for the working orders). This method then queries the database to retrieve any orders which contain
	 * the specified time strings in their orders.
	 * @param group
	 * @param tableNum
	 * @return ArrayList<Order> targetOrders: a list of orders which correspond with the inputs to look for
	 */
	ArrayList<Order> searchByTime(String group, String beginHour, String beginMinute, String endHour, String endMinute) {
		ArrayList<Order> targetOrders = new ArrayList<Order>();
		PreparedStatement prepStmt = null;
		ResultSet resSet = null;
		// SQLite queries won't work with 1:00 for instance it needs to be in the form 01:00 etc
		if (beginHour.length() < 2) {
			beginHour = "0" + beginHour;
		} else if (endHour.length() < 2) {
			endHour = "0" + endHour;
		} 
		
		String beginTime = "" + beginHour + ":" + beginMinute;
		String endTime = "" + endHour + ":" + endMinute;
		try {
			if (group.equals("Current Orders")) {
				String query = "SELECT * FROM orders WHERE time BETWEEN ? AND ?";
				prepStmt = connection.prepareStatement(query);
				prepStmt.setString(1, beginTime);
				prepStmt.setString(2, endTime);
				resSet = prepStmt.executeQuery();
				while (resSet.next()) {
					targetOrders.add(new Order(resSet.getInt("tableNo"), resSet.getString("orderList"),
							resSet.getString("totalPrice"), resSet.getString("specialRequests"),
							resSet.getString("comments"), resSet.getString("isComplete"), resSet.getString("date"),
							resSet.getString("time")));
				}
				prepStmt.close();
				resSet.close();
			} else {
				String query = "SELECT * FROM storedOrders WHERE time BETWEEN ? AND ?";
				prepStmt = connection.prepareStatement(query);
				prepStmt.setString(1, beginTime);
				prepStmt.setString(2, endTime);
				resSet = prepStmt.executeQuery();
				while (resSet.next()) {
					targetOrders.add(new Order(resSet.getInt("tableNo"), resSet.getString("orderList"),
							resSet.getString("totalPrice"), resSet.getString("specialRequests"),
							resSet.getString("comments"), resSet.getString("isComplete"), resSet.getString("date"),
							resSet.getString("time")));
				}
				prepStmt.close();
				resSet.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return targetOrders;
	}
	
	/**
	 * Takes a string instructing the method what set or 'group' of orders to query, and then outputs all the orders from that group 
	 * as an ArrayList
	 * @param group
	 * @return ArrayList<Order> targetOrders: all the order objects from specified group of orders
	 */
	ArrayList<Order> getAllOrders(String group) {
		ArrayList<Order> targetOrders = new ArrayList<Order>();
		PreparedStatement prepStmt = null;
		ResultSet resSet = null;
		try {
			if (group.equals("Current Orders")) {
				String query = "SELECT * FROM orders";
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
			} else {
				String query = "SELECT * FROM storedOrders";
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
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return targetOrders;
	}
	
	// Menu queries
	
	/**
	 * Retrieves all food objects from the menu which might have been edited by a manager
	 * @return ArrayList<Food> : all the food objects in the menu
	 */
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
	
	/**
	 * Takes three strings, a username, activity description, and a timestamps, and then registers a new record
	 * into the activityLog
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

}
