package application;

import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
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
	
	// Data holder variables
	private ObservableList<Food> menuObservableList; // Menu list of all possible food options
	private ObservableList<Food> currentOrderObservableList; 
	// Order list of the orders of the currently selected order be it stored in a table or from history
	private Food currentItemToAdd; // Currently selected food item
	private Food currentItemToRemove; // Two variables needed for selecting items to add or to remove
	// Or there is possible ambiguity when both items are selected on the lists and add or remove are pressed.
	
	// Current status variables like which table is being focused on at a particular time
	private int currentTableNo;
	
	// click count variables to reset the table editing status
	int oneClickCount;
	int twoClickCount;
	int threeClickCount;
	int fourClickCount;
	int fiveClickCount;
	int sixClickCount;
		
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
		
		// set buttons to uneditable to begin until a table is selected.
	}
	
	public void AddItemToOrder(ActionEvent event) {
		currentItemToAdd = menuListView.getSelectionModel().getSelectedItem();
		currentOrderObservableList.add(currentItemToAdd);
		currentOrderListView.setItems(currentOrderObservableList);
	}
	
	public void clearOrderListView() {
		currentOrderObservableList.clear();
		currentOrderListView.setItems(currentOrderObservableList);
	}
	
	public void RemoveItemFromOrder(ActionEvent event) {
		
		currentItemToRemove = currentOrderListView.getSelectionModel().getSelectedItem();
		currentOrderObservableList.remove(currentItemToRemove);
		currentOrderListView.setItems(currentOrderObservableList);
		
		// Deal with errors if nothing to remove
	}
	
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
			mouseEvent.consume();
			// twoClickCount = 0; // can add this and remove consume method to allow switching of editable status on subsequent clicks
		}
	}
	
	public void SaveCurrentOrder() throws SQLException {
		staffModel.SaveCurrentOrderToDatabase(3,currentOrderObservableList);
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

}
