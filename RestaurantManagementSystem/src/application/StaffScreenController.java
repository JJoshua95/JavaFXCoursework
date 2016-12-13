package application;

import java.net.URL;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Callback;

public class StaffScreenController implements Initializable {
	
	public StaffScreenModel staffModel = new StaffScreenModel();
	
	// UI Components
	@FXML
	private Label lblUser;
	@FXML
	private Circle circleTable1;
	@FXML
	private Circle circleTable2;
	@FXML
	private Circle circleTable3;
	@FXML
	private Circle circleTable4;
	@FXML
	private Circle circleTable5;
	@FXML
	private Circle circleTable6;
	@FXML
	private ListView<Food> menuListView;
	@FXML
	private Button addFoodItemBtn;
	@FXML
	private ListView<Food> currentOrderListView;
	@FXML
	private Button removeFoodItemFromOrder;
	@FXML
	private Button saveOrderBtn;
	@FXML
	private Label lblCurrentTable;
	@FXML 
	private Button deleteCurrentOrderBtn;
	@FXML 
	private Label timestampLbl;
	@FXML
	private TextArea specialRequestsTxt;
	@FXML
	private TextArea commentsTxt;
	@FXML
	private CheckBox orderCompleteCheckBox;
	@FXML 
	private Label totalPriceLbl;
	@FXML
	private Button calculateTotalBtn;
	
	// Data holder variables
	private ObservableList<Food> menuObservableList; // Menu list of all possible food options
	private ObservableList<Food> currentOrderObservableList; 
	// Order list of the orders of the currently selected order be it stored in a table or from history
	private Food currentItemToAdd; // Currently selected food item
	private Food currentItemToRemove; // Two variables needed for selecting items to add or to remove
	// Or there is possible ambiguity when both items are selected on the lists and add or remove are pressed.
	private Date dateTimeOfCurrentOrder;
	private String specialRequestsString;
	private String commentsString;
	private String isOrderCompleteString;
	private double orderTotalPrice;
	
	// Current status variables like which table is being focused on at a particular time
	private int currentTableNo;
	
	// click count variables to reset the table editing status when clicking a new table
	private int oneClickCount;
	private int twoClickCount;
	private int threeClickCount;
	private int fourClickCount;
	private int fiveClickCount;
	private int sixClickCount;
		
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		// TODO Auto-generated method stub
		
		menuObservableList = FXCollections.observableArrayList();
		// add some food to the menu
		menuObservableList.addAll(
				new Food("Chicken", 3.60),
				new Food("Beef", 4.30),
				new Food("Lamb", 5.00)
				);
		
		menuListView.setItems(menuObservableList);
		menuListView.setCellFactory(new Callback<ListView<Food>, ListCell<Food>>() {
			@Override
			public ListCell<Food> call(ListView<Food> p) {
				ListCell<Food> cell = new ListCell<Food>() {
					@Override
					protected void updateItem(Food f, boolean bool) {
						super.updateItem(f, bool);
						if (f != null) {
							DecimalFormat df = new DecimalFormat("#.00"); // to print the price to 2dp i.e. to the nearest penny
							setText(f.getMenuItemName() + " : £" + df.format(f.getPrice()));
						} else {
							setText(null);
						}
					}
				};
				
				return cell;
			}
		});
		
		currentOrderListView.setCellFactory(new Callback<ListView<Food>, ListCell<Food>>() {
			@Override
			public ListCell<Food> call(ListView<Food> p) {
				ListCell<Food> cell = new ListCell<Food>() {
					@Override
					protected void updateItem(Food f, boolean bool) {
						super.updateItem(f, bool);
						if (f != null) {
							DecimalFormat df = new DecimalFormat("#.00"); // to print the price to 2dp i.e. to the nearest penny
							setText(f.getMenuItemName() + " : £" + df.format(f.getPrice()));
						} else {
							setText(null);
						}
					}
				};
				
				return cell;
			}
		});
		
		currentOrderObservableList = FXCollections.observableArrayList(); // initialise the current order list view
		
		lblCurrentTable.setText("");
		timestampLbl.setText("");
		totalPriceLbl.setText("");
		
		// set buttons to uneditable to begin until a table is selected.
		addFoodItemBtn.setDisable(true);
		removeFoodItemFromOrder.setDisable(true);
		saveOrderBtn.setDisable(true);
		deleteCurrentOrderBtn.setDisable(true);
		calculateTotalBtn.setDisable(true);
		orderCompleteCheckBox.setDisable(true);
		// make the text areas uneditable
		specialRequestsTxt.setEditable(false);
		commentsTxt.setEditable(false);
		
	}
	
	// ------------------------------------------------------- Current Order GUI Controls ----------------------------------------------
	
	public void AddItemToOrder(ActionEvent event) {
		currentItemToAdd = menuListView.getSelectionModel().getSelectedItem();
		currentOrderObservableList.add(currentItemToAdd);
		currentOrderListView.setItems(currentOrderObservableList);
	}
	
	public void clearOrderListView() {
		currentOrderObservableList.clear();
		currentOrderListView.setItems(currentOrderObservableList);
		// clear the comments and set the CheckBox to null
	}
	
	public void RemoveItemFromOrder(ActionEvent event) {
		
		currentItemToRemove = currentOrderListView.getSelectionModel().getSelectedItem();
		currentOrderObservableList.remove(currentItemToRemove);
		currentOrderListView.setItems(currentOrderObservableList);
		
		// Deal with errors if nothing to remove
	}
	
	public void resetOrderViews() {
		
	}
	
	// condense some of the following into smaller component methods to be called repeatedly
	public void getTableOneOrder(MouseEvent mouseEvent) throws SQLException {
		// reset all click counts of other tables to avoid confusing editable statuses when switching tables
		// or else you may click back on a new table one and it is immediately editable
		//oneClickCount = 0;
		twoClickCount = 0;
		threeClickCount = 0;
		fourClickCount = 0;
		fiveClickCount = 0;
		sixClickCount = 0;
		// stop editing of orders on 1st click (enable editing of orders after a second click)
		addFoodItemBtn.setDisable(true);
		removeFoodItemFromOrder.setDisable(true);
		saveOrderBtn.setDisable(true);
		deleteCurrentOrderBtn.setDisable(true);
		calculateTotalBtn.setDisable(true);
		orderCompleteCheckBox.setDisable(true);
		specialRequestsTxt.setEditable(false);
		commentsTxt.setEditable(false);
		currentTableNo = 1;
		lblCurrentTable.setText("Table " + currentTableNo);
		clearOrderListView();
		ArrayList<Food> arr1 = staffModel.RetrieveATableOrderFromDB(1);
		currentOrderObservableList.addAll(arr1);
		currentOrderListView.setItems(currentOrderObservableList);
		oneClickCount++;
		if (oneClickCount > 1) {
			// on second click
			// enable editing
			addFoodItemBtn.setDisable(false);
			removeFoodItemFromOrder.setDisable(false);
			saveOrderBtn.setDisable(false);
			deleteCurrentOrderBtn.setDisable(false);
			calculateTotalBtn.setDisable(false);
			orderCompleteCheckBox.setDisable(false);
			specialRequestsTxt.setEditable(true);
			commentsTxt.setEditable(true);
			mouseEvent.consume();
			// oneClickCount = 0; // can add this and remove consume method to allow switching of editable status on subsequent clicks
		}
	}
	
	public void getTableTwoOrder(MouseEvent mouseEvent) throws SQLException {
		oneClickCount = 0;
		//twoClickCount = 0;
		threeClickCount = 0;
		fourClickCount = 0;
		fiveClickCount = 0;
		sixClickCount = 0;
		currentTableNo = 2;
		addFoodItemBtn.setDisable(true);
		removeFoodItemFromOrder.setDisable(true);
		saveOrderBtn.setDisable(true);
		deleteCurrentOrderBtn.setDisable(true);
		calculateTotalBtn.setDisable(true);
		orderCompleteCheckBox.setDisable(true);
		specialRequestsTxt.setEditable(false);
		commentsTxt.setEditable(false);
		lblCurrentTable.setText("Table " + currentTableNo);
		clearOrderListView();
		ArrayList<Food> arr2 = staffModel.RetrieveATableOrderFromDB(2);
		currentOrderObservableList.addAll(arr2);
		currentOrderListView.setItems(currentOrderObservableList);
		twoClickCount++;
		if (twoClickCount > 1) {
			// enable editing
			addFoodItemBtn.setDisable(false);
			removeFoodItemFromOrder.setDisable(false);
			saveOrderBtn.setDisable(false);
			deleteCurrentOrderBtn.setDisable(false);
			calculateTotalBtn.setDisable(false);
			orderCompleteCheckBox.setDisable(false);
			specialRequestsTxt.setEditable(true);
			commentsTxt.setEditable(true);
			mouseEvent.consume();
			// twoClickCount = 0; // can add this and remove consume method to allow switching of editable status on subsequent clicks
		}
	}
	
	public void getTableThreeOrder(MouseEvent mouseEvent) throws SQLException {
		// count resetting
		oneClickCount = 0;
		twoClickCount = 0;
		//threeClickCount = 0;
		fourClickCount = 0;
		fiveClickCount = 0;
		sixClickCount = 0;
		// change working table 
		currentTableNo = 3;
		addFoodItemBtn.setDisable(true);
		removeFoodItemFromOrder.setDisable(true);
		saveOrderBtn.setDisable(true);
		deleteCurrentOrderBtn.setDisable(true);
		calculateTotalBtn.setDisable(true);
		orderCompleteCheckBox.setDisable(true);
		specialRequestsTxt.setEditable(false);
		commentsTxt.setEditable(false);
		lblCurrentTable.setText("Table " + currentTableNo);
		clearOrderListView();
		ArrayList<Food> arr3 = staffModel.RetrieveATableOrderFromDB(3);
		currentOrderObservableList.addAll(arr3);
		currentOrderListView.setItems(currentOrderObservableList);
		threeClickCount++;
		if (threeClickCount > 1) {
			// enable editing
			addFoodItemBtn.setDisable(false);
			removeFoodItemFromOrder.setDisable(false);
			saveOrderBtn.setDisable(false);
			deleteCurrentOrderBtn.setDisable(false);
			calculateTotalBtn.setDisable(false);
			orderCompleteCheckBox.setDisable(false);
			specialRequestsTxt.setEditable(true);
			commentsTxt.setEditable(true);
			mouseEvent.consume();
			// twoClickCount = 0; // can add this and remove consume method to allow switching of editable status on subsequent clicks
		}
	}
	
	public void getTableFourOrder(MouseEvent mouseEvent) throws SQLException {
		// count resetting
		oneClickCount = 0;
		twoClickCount = 0;
		threeClickCount = 0;
		//fourClickCount = 0;
		fiveClickCount = 0;
		sixClickCount = 0;
		// change working table 
		currentTableNo = 4;
		addFoodItemBtn.setDisable(true);
		removeFoodItemFromOrder.setDisable(true);
		saveOrderBtn.setDisable(true);
		deleteCurrentOrderBtn.setDisable(true);
		calculateTotalBtn.setDisable(true);
		orderCompleteCheckBox.setDisable(true);
		specialRequestsTxt.setEditable(false);
		commentsTxt.setEditable(false);
		lblCurrentTable.setText("Table " + currentTableNo);
		clearOrderListView();
		ArrayList<Food> arr4 = staffModel.RetrieveATableOrderFromDB(4);
		currentOrderObservableList.addAll(arr4);
		currentOrderListView.setItems(currentOrderObservableList);
		fourClickCount++;
		if (fourClickCount > 1) {
			// enable editing
			addFoodItemBtn.setDisable(false);
			removeFoodItemFromOrder.setDisable(false);
			saveOrderBtn.setDisable(false);
			deleteCurrentOrderBtn.setDisable(false);
			calculateTotalBtn.setDisable(false);
			orderCompleteCheckBox.setDisable(false);
			specialRequestsTxt.setEditable(true);
			commentsTxt.setEditable(true);
			mouseEvent.consume();
			// twoClickCount = 0; // can add this and remove consume method to allow switching of editable status on subsequent clicks
		}
	}
	
	public void getTableFiveOrder(MouseEvent mouseEvent) throws SQLException {
		// count resetting
		oneClickCount = 0;
		twoClickCount = 0;
		threeClickCount = 0;
		fourClickCount = 0;
		//fiveClickCount = 0;
		sixClickCount = 0;
		// change working table 
		currentTableNo = 5;
		addFoodItemBtn.setDisable(true);
		removeFoodItemFromOrder.setDisable(true);
		saveOrderBtn.setDisable(true);
		deleteCurrentOrderBtn.setDisable(true);
		calculateTotalBtn.setDisable(true);
		orderCompleteCheckBox.setDisable(true);
		specialRequestsTxt.setEditable(false);
		commentsTxt.setEditable(false);
		lblCurrentTable.setText("Table " + currentTableNo);
		clearOrderListView();
		ArrayList<Food> arr5 = staffModel.RetrieveATableOrderFromDB(5);
		currentOrderObservableList.addAll(arr5);
		currentOrderListView.setItems(currentOrderObservableList);
		fiveClickCount++;
		if (fiveClickCount > 1) {
			// enable editing
			addFoodItemBtn.setDisable(false);
			removeFoodItemFromOrder.setDisable(false);
			saveOrderBtn.setDisable(false);
			deleteCurrentOrderBtn.setDisable(false);
			calculateTotalBtn.setDisable(false);
			orderCompleteCheckBox.setDisable(false);
			specialRequestsTxt.setEditable(true);
			commentsTxt.setEditable(true);
			mouseEvent.consume();
			// twoClickCount = 0; // can add this and remove consume method to allow switching of editable status on subsequent clicks
		}
	}
	
	public void getTableSixOrder(MouseEvent mouseEvent) throws SQLException {
		// count resetting
		oneClickCount = 0;
		twoClickCount = 0;
		threeClickCount = 0;
		fourClickCount = 0;
		fiveClickCount = 0;
		//sixClickCount = 0;
		// change working table 
		currentTableNo = 6;
		addFoodItemBtn.setDisable(true);
		removeFoodItemFromOrder.setDisable(true);
		saveOrderBtn.setDisable(true);
		deleteCurrentOrderBtn.setDisable(true);
		calculateTotalBtn.setDisable(true);
		orderCompleteCheckBox.setDisable(true);
		specialRequestsTxt.setEditable(false);
		commentsTxt.setEditable(false);
		lblCurrentTable.setText("Table " + currentTableNo);
		clearOrderListView();
		ArrayList<Food> arr6 = staffModel.RetrieveATableOrderFromDB(6);
		currentOrderObservableList.addAll(arr6);
		currentOrderListView.setItems(currentOrderObservableList);
		sixClickCount++;
		if (sixClickCount > 1) {
			// enable editing
			addFoodItemBtn.setDisable(false);
			removeFoodItemFromOrder.setDisable(false);
			saveOrderBtn.setDisable(false);
			deleteCurrentOrderBtn.setDisable(false);
			calculateTotalBtn.setDisable(false);
			orderCompleteCheckBox.setDisable(false);
			specialRequestsTxt.setEditable(true);
			commentsTxt.setEditable(true);
			mouseEvent.consume();
			// twoClickCount = 0; // can add this and remove consume method to allow switching of editable status on subsequent clicks
		}
	}
	
	public void SaveCurrentOrder() throws SQLException {
		staffModel.SaveCurrentOrderToDatabase(currentTableNo,currentOrderObservableList);
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		// Date date = new Date();
		dateTimeOfCurrentOrder = new Date();
		timestampLbl.setText(dateFormat.format(dateTimeOfCurrentOrder));
		System.out.println(dateFormat.format(dateTimeOfCurrentOrder));
	}
	
	public void DeleteCurrentOrder() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirm Delete");
		alert.setHeaderText("Are you sure? Deleting this order cannot be undone");
		alert.setContentText("Continue with deleting this current order?");
		
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			staffModel.DeleteCurrentOrderFromDB(currentTableNo);
			// and clear the listview
			clearOrderListView();
		} else {
			alert.close();
		}
		
	}
	
	public void getOrderPriceTotal() {
		double total = 0;
		for (Food f : currentOrderObservableList) {
			total += f.getPrice();
		}
		DecimalFormat df = new DecimalFormat("#.00"); // to print the price to 2dp i.e. to the nearest penny		
		totalPriceLbl.setText("£" + df.format(total));
		orderTotalPrice = total;
		System.out.println(total);
	}
	
	void GetUser(String user) {
		lblUser.setText(user);
	}
	
	public void SignOut(ActionEvent event) {
		try {
			((Node)event.getSource()).getScene().getWindow().hide();
			Stage primaryStage = new Stage();
			FXMLLoader loader = new FXMLLoader();
			Pane root = loader.load(getClass().getResource("/application/Login.fxml").openStream());
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
			
		} catch (Exception e) {
			System.err.println("Exception Caught");
			e.printStackTrace();
		}
	}
	
	// -------------------------------------------------- Search current orders or past orders -------------------------------------------------

}
