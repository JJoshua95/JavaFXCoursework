package application;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * The Order class is used to instantiate Order objects with simple property type varaibles that are required for displaying
 * them in tableviews.
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
	 * The constructor takes ints and strings and outputs new Order Objects with simple javafx properties allowing them to be displayed in
	 * TableViews. The getters output ints or strings not properties
	 * @param tableNo
	 * @param orderList
	 * @param totalPrice
	 * @param specialRequests
	 * @param comments
	 * @param completed
	 * @param date
	 * @param time
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

	public int getTableNo() {
		return tableNo.get();
	}

	public String getOrderList() {
		return orderList.get();
	}

	public String getTotalPrice() {
		return totalPrice.get();
	}

	public String getSpecialRequests() {
		return specialRequests.get();
	}

	public String getComments() {
		return comments.get();
	}

	public String getCompleted() {
		return completed.get();
	}

	public String getDate() {
		return date.get();
	}

	public String getTime() {
		return time.get();
	}

}