package application;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * The Order class is used to instantiate Order objects with simple property type variables that are required for displaying
 * order records in TableViews.
 * The methods and structure of this class are derived from the tutorials available from the ProgrammingKnowledge playlist 
 * JavaFX tutorial for beginners : https://www.youtube.com/watch?v=9YrmON6nlEw&list=PLS1QulWo1RIaUGP446_pWLgTZPiFizEMq
 * @author jarrod joshua
 */
public class Order {
	
	private final SimpleIntegerProperty tableNo;
	private final SimpleStringProperty orderList;
	private final SimpleStringProperty totalPrice;
	private final SimpleStringProperty specialRequests;
	private final SimpleStringProperty comments;
	private final SimpleStringProperty completed;
	private final SimpleStringProperty date;
	private final SimpleStringProperty time;
	
	/**
	 * The constructor takes integers and strings and outputs new Order Objects with simple JavaFX properties allowing them to be displayed in
	 * TableViews. (The getters output integers or strings not properties)
	 * @param tableNo : int specifying the table number the order was registered for
	 * @param orderList : a list of orders written as a string in the format [foodPame:price, foodPame:price, ... } where the price is a number written to 2dp
	 * @param totalPrice : a string specifying the total bill an order adds up to written as a number to 2dp
	 * @param specialRequests : a string specifying any special requests a customer may have made like allergies
	 * @param comments : any comments the staff felt were relevant to record when registering the order
	 * @param completed : a String 'true' or 'false' only specifying if the order is indeed completed or not
	 * @param date : a String specifying the date the order was first registered (not overwritten when editing orders) in the format "yyyy-mm-dd"
	 * @param time : a String specifying the time the order was first registered (not overwritten when editing orders) in the format "hh:mm:ss"
	 */
	public Order(int tableNo, String orderList, String totalPrice, String specialRequests, String comments,
			String completed, String date, String time) {
		super();
		this.tableNo = new SimpleIntegerProperty(tableNo);
		this.orderList = new SimpleStringProperty(orderList);
		this.totalPrice = new SimpleStringProperty(totalPrice);
		this.specialRequests = new SimpleStringProperty(specialRequests);
		this.comments = new SimpleStringProperty(comments);
		this.completed = new SimpleStringProperty(completed);
		this.date = new SimpleStringProperty(date);
		this.time = new SimpleStringProperty(time);
	}
	
	/**
	 * Returns the tableNo property of the Order object as an int
	 * @return tableNo: an integer specifying the table number of the Order object
	 */
	public int getTableNo() {
		return tableNo.get();
	}
	
	/**
	 * Returns the orderList string of the Order Object
	 * @return orderList : a String detailing a list of Food Objects customers ordered, in the format they are stored in the database as
	 */
	public String getOrderList() {
		return orderList.get();
	}
	
	/**
	 * Returns the total bill for the Order object
	 * @return totalPrice : a String specifying the total the Order summed to.
	 */
	public String getTotalPrice() {
		return totalPrice.get();
	}
	
	/**
	 * Returns any special requests that were recorded in the Order object as a String
	 * @return specialRequests : a String detailing any special requests a customers order may have contained
	 */
	public String getSpecialRequests() {
		return specialRequests.get();
	}
	
	/**
	 * Returns any comments the staff member recorded in the Order
	 * @return comments : a String detailing any comments the staff recorded when registering the order
	 */
	public String getComments() {
		return comments.get();
	}
	
	/**
	 * Returns a string "true" or "false" depending on whether the order has been marked as complete by the staff
	 * @return completed : a String "true" or "false" specifying if the order has been served and completed
	 */
	public String getCompleted() {
		return completed.get();
	}

	/**
	 * Returns a String specifying the date the order was first registered to the system
	 * @return date : a String specifying the date the order was first made in the format "yyyy-mm-dd"
	 */
	public String getDate() {
		return date.get();
	}
	
	/**
	 * Returns a String specifying the time the order was first made at 
	 * @return time : a String specifying the tie the order was first made in the format "hh:mm:ss"
	 */
	public String getTime() {
		return time.get();
	}

}