package application;

import javafx.beans.property.SimpleStringProperty;

public class Order {
	
	private final SimpleStringProperty tableNo;
	private final SimpleStringProperty orderList;
	private final SimpleStringProperty totalPrice;
	private final SimpleStringProperty specialRequests;
	private final SimpleStringProperty comments;
	private final SimpleStringProperty completed;
	private final SimpleStringProperty date;
	private final SimpleStringProperty time;
	// private final SimpleStringProperty orderNo; Needed if we implement replacing current orders with stored orders
	
	public Order(String tableNo, String orderList, String totalPrice, String specialRequests, String comments,
			String completed, String date, String time /*,String orderNo*/) {
		super();
		this.tableNo = new SimpleStringProperty(tableNo);
		this.orderList = new SimpleStringProperty(orderList);
		this.totalPrice = new SimpleStringProperty(totalPrice);
		this.specialRequests = new SimpleStringProperty(specialRequests);
		this.comments = new SimpleStringProperty(comments);
		this.completed = new SimpleStringProperty(completed);
		this.date = new SimpleStringProperty(date);
		this.time = new SimpleStringProperty(time);
		//this.orderNo = new SimpleStringProperty(orderNo);
	}

	public String getTableNo() {
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
	
	/*
	public String getOrderNo() {
		return time.get();
	}
	*/
}