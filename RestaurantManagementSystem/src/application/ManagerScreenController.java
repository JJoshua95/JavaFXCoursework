package application;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import com.opencsv.*; // this class makes use of the free openCSV parser 3rd party library from http://opencsv.sourceforge.net/ 

// This class makes use of the opencsv library available from : http://opencsv.sourceforge.net/

/**
 * This class provides the logic (besides any direct database interactions) behind the Manager Screen GUI controls
 * Makes use of the opencsv java library available from: http://opencsv.sourceforge.net/
 * The methods and structure of this class are derived from the tutorials available from the ProgrammingKnowledge playlist 
 * JavaFX tutorial for beginners : https://www.youtube.com/watch?v=9YrmON6nlEw&list=PLS1QulWo1RIaUGP446_pWLgTZPiFizEMq
 * This class makes use of the opencsv library available from : http://opencsv.sourceforge.net/
 * @author jarrod joshua
 */
public class ManagerScreenController implements Initializable {
	
	private ManagerScreenModel managerModel = new ManagerScreenModel(); // To access the database
	
	// Hold login details of current user for activity logging
	private String usernameStr;
	private String passwordStr;
	
	@FXML 
	private Label lblUser;
	@FXML 
	private Button switchToStaffBtn;
	@FXML
	private TableView<Staff> staffTableView;
	@FXML
	private TableColumn<Staff, Integer> staffIdCol;
	@FXML
	private TableColumn<Staff, String> usernameCol;
	@FXML
	private TableColumn<Staff, String> passwordCol;
	@FXML
	private TableColumn<Staff, String> managerColumn;
	@FXML 
	private TextField manageUsernameTxt;
	@FXML
	private TextField managePasswordTxt;
	@FXML
	private TextField manageStaffIdTxt;
	@FXML
	private ComboBox<String> isManagerCB;
	@FXML
	private Button addStaffBtn;
	@FXML
	private Button removeStaffbtn;
	@FXML
	private TextArea manageStaffStatus;
	@FXML
	private ListView<Food> managerMenuListView;
	@FXML
	private TextField manageItemNameTxt;
	@FXML
	private TextField manageItemCostTxt;
	@FXML
	private Button addItemBtn;
	@FXML
	private Button removeItemBtn;
	@FXML
	private TextArea manageMenuStatus;
	@FXML
	private ListView<Staff> staffUsernameListView;
	@FXML 
	private Button searchSelectedActivityBtn;
	@FXML
	private TableView<ActivityLog> activityLogTableView;
	@FXML
	private TableColumn<ActivityLog, String> activityUsernameTableColumn;
	@FXML
	private TableColumn<ActivityLog, String> activityEntryTableColumn;
	@FXML
	private TableColumn<ActivityLog, String> activityTimeTableColumn;
	@FXML
	private TextArea activitySearchStatus;
	
	// variables for the staff table
	private ObservableList<Staff> staffTableViewObsList;
	// variables for the editable food list
	private ObservableList<Food> editableMenuObsList;
	// variables for the activity log lists
	private ObservableList<ActivityLog> activityLogObsList;
	
	// Export Orders Variables ====================================================================
	
	@FXML
	private TableView<Order> exportTableView;
	@FXML
	private TableColumn<Order, String> exportTableNoCol;
	@FXML
	private TableColumn<Order, String> exportOrderListCol;
	@FXML
	private TableColumn<Order, String> exportTotalBillCol;
	@FXML
	private TableColumn<Order, String> exportSpecialReqsCol;
	@FXML
	private TableColumn<Order, String> exportCommentsCol;
	@FXML
	private TableColumn<Order, String> exportCompletedCol;
	@FXML
	private TableColumn<Order, String> exportDateCol;
	@FXML
	private TableColumn<Order, String> exportTimeCol;
	@FXML
	private Button exportSelectionBtn;
	@FXML 
	private TextArea exportStatusTxt;
	@FXML
	private TextField exportFilenameTxt;
	
	// variables for exporting orders to CSV
	private ObservableList<Order> ordersForPossibleExport; // all the stored and current orders from database (can export anything)
	
	// Import Orders Variables ======================================================================
	private ObservableList<Order> ordersForPossibleImport; // array to hold orders from a CSV file, can be saved in stored orders 
	
	@FXML
	private TableView<Order> importTableView;
	@FXML
	private TableColumn<Order, String> importTableNoCol;
	@FXML
	private TableColumn<Order, String> importOrderListCol;
	@FXML
	private TableColumn<Order, String> importTotalBillCol;
	@FXML
	private TableColumn<Order, String> importSpecialReqsCol;
	@FXML
	private TableColumn<Order, String> importCommentsCol;
	@FXML
	private TableColumn<Order, String> importCompletedCol;
	@FXML
	private TableColumn<Order, String> importDateCol;
	@FXML
	private TableColumn<Order, String> importTimeCol;
	@FXML
	private Button importCsvBtn;
	@FXML 
	private TextArea importStatusTxt;
	@FXML
	private Button saveImportToDbBtn;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		// Set up the TableView first
		staffTableViewObsList = FXCollections.observableArrayList();
		staffIdCol.setCellValueFactory(new PropertyValueFactory<Staff, Integer>("staffID"));
		usernameCol.setCellValueFactory(new PropertyValueFactory<Staff, String>("username"));
		passwordCol.setCellValueFactory(new PropertyValueFactory<Staff, String>("password"));
		managerColumn.setCellValueFactory(new PropertyValueFactory<Staff, String>("isManager"));
		
		try {
			staffTableViewObsList.addAll(managerModel.getAllEmployeesFromDB());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		staffTableView.setItems(staffTableViewObsList);
		
		// Set up the ComboBox for giving manager status
		ObservableList<String> isManagerComboBoxObsList = FXCollections.observableArrayList("true",
				"false");
		
		isManagerCB.setItems(isManagerComboBoxObsList);
		
		addStaffBtn.setDisable(true); // disable the add until the ComboBox is selected
		
		// Set up the editable menu
		
		editableMenuObsList = FXCollections.observableArrayList();
		managerMenuListView.setCellFactory(new Callback<ListView<Food>, ListCell<Food>>() {
			@Override
			public ListCell<Food> call(ListView<Food> p) {
				ListCell<Food> cell = new ListCell<Food>() {
					@Override
					protected void updateItem(Food f, boolean bool) {
						super.updateItem(f, bool);
						if (f != null) {
							DecimalFormat df = new DecimalFormat("#.00");
							// to print the price to 2dp i.e. to the nearest
							// penny
							setText(f.getMenuItemName() + " : £" + df.format(f.getPrice()));
						} else {
							setText(null);
						}
					}
				};

				return cell;
			}
		});
		try {
			editableMenuObsList.addAll(managerModel.getAllFoodFromMenu());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		managerMenuListView.setItems(editableMenuObsList);
		
		// set up the staff name ListView
		
		staffUsernameListView.setCellFactory(new Callback<ListView<Staff>, ListCell<Staff>>() {
			@Override
			public ListCell<Staff> call(ListView<Staff> p) {
				ListCell<Staff> cell = new ListCell<Staff>() {
					@Override
					protected void updateItem(Staff s, boolean bool) {
						super.updateItem(s, bool);
						if (s != null) {
							setText(s.getUsername());
						} else {
							setText(null);
						}
					}
				};

				return cell;
			}
		});
		staffUsernameListView.setItems(staffTableViewObsList);
		
		// set up the activity table view next
		
		activityLogObsList = FXCollections.observableArrayList();
		activityUsernameTableColumn.setCellValueFactory(new PropertyValueFactory<ActivityLog, String>("username"));
		activityEntryTableColumn.setCellValueFactory(new PropertyValueFactory<ActivityLog, String>("activityEntry"));
		activityTimeTableColumn.setCellValueFactory(new PropertyValueFactory<ActivityLog, String>("time"));
		
		activityLogTableView.setItems(activityLogObsList);
		
		// Set up exporting table
		
		ordersForPossibleExport = FXCollections.observableArrayList();
		exportTableNoCol.setCellValueFactory(new PropertyValueFactory<Order, String>("tableNo"));
		exportOrderListCol.setCellValueFactory(new PropertyValueFactory<Order, String>("orderList"));
		exportTotalBillCol.setCellValueFactory(new PropertyValueFactory<Order, String>("totalPrice"));
		exportSpecialReqsCol.setCellValueFactory(new PropertyValueFactory<Order, String>("specialRequests"));
		exportCommentsCol.setCellValueFactory(new PropertyValueFactory<Order, String>("comments"));
		exportCompletedCol.setCellValueFactory(new PropertyValueFactory<Order, String>("completed"));
		exportDateCol.setCellValueFactory(new PropertyValueFactory<Order, String>("date"));
		exportTimeCol.setCellValueFactory(new PropertyValueFactory<Order, String>("time"));
		
		exportTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE); // can select multiple rows
		try {
			ordersForPossibleExport.addAll(managerModel.getAllOrders());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		exportTableView.setItems(ordersForPossibleExport);
		
		// set up import table
		
		ordersForPossibleImport = FXCollections.observableArrayList();
		importTableNoCol.setCellValueFactory(new PropertyValueFactory<Order, String>("tableNo"));
		importOrderListCol.setCellValueFactory(new PropertyValueFactory<Order, String>("orderList"));
		importTotalBillCol.setCellValueFactory(new PropertyValueFactory<Order, String>("totalPrice"));
		importSpecialReqsCol.setCellValueFactory(new PropertyValueFactory<Order, String>("specialRequests"));
		importCommentsCol.setCellValueFactory(new PropertyValueFactory<Order, String>("comments"));
		importCompletedCol.setCellValueFactory(new PropertyValueFactory<Order, String>("completed"));
		importDateCol.setCellValueFactory(new PropertyValueFactory<Order, String>("date"));
		importTimeCol.setCellValueFactory(new PropertyValueFactory<Order, String>("time"));
		
	}
		
	// Handle managing staff accounts
	
	/**
	 * enables clicking the add staff account button, which is disabled initially to prevent user errors
	 */
	public void enableAddAccount() {
		addStaffBtn.setDisable(false);
		// System.out.println(isManagerCB.getValue());
	}
	
	/**
	 * Reads from user inputs from the GUI and then inputs a username, password, id number, and whether the new account has manager 
	 * level access or not, and then passes these to a ManagerModel object method which then queries the database and inserts a new corresponding 
	 * record.
	 */
	public void addNewStaffAccount() {
		try {
		String newUsername = manageUsernameTxt.getText();
		String newPassword = managePasswordTxt.getText();
		int newStaffID = Integer.parseInt(manageStaffIdTxt.getText());
		String managerLevel = isManagerCB.getValue().toString();
		
		// check that there are username and password entered that aren't empty
		// check that the id is an int greater than 0
		if (newUsername != "" && newPassword != "" && newStaffID > 0) {
			System.out.println("Valid fields entered");
			managerModel.addStaffAccountIntoDB(newStaffID, newUsername, newPassword, managerLevel);
			staffTableViewObsList.clear();
			staffTableViewObsList.addAll(managerModel.getAllEmployeesFromDB());
			staffTableView.setItems(staffTableViewObsList);
			manageStaffStatus.setText("Account saved into database");
			saveActivityLog("Created/Updated an account: " + newUsername);
			staffUsernameListView.setItems(staffTableViewObsList);
		} else {
			manageStaffStatus.setText("Please fill all the fields before trying to add a new staff account");
		}
		} catch (Exception e) {
			e.printStackTrace();
			manageStaffStatus.setText("Invalid staff fields entered");
		}
	}
	
	/**
	 * Retrieves the staff account credentials from a selected staff TableView row, and then passes these to a ManagerModel 
	 * method to query the database to delete the corresponding record from the database and the system and then update the table.
	 */
	public void deletedSelectedAccount() {
		try {
		if  (staffTableView.getSelectionModel().getSelectedItem() == null ) { 
			manageStaffStatus.setText("No staff account selected, please select a row from the table.");
		} else {
			Staff staffToDelete = staffTableView.getSelectionModel().getSelectedItem();
			int targetID = staffToDelete.getStaffID();
			String targetUser = staffToDelete.getUsername();
			String targetPass = staffToDelete.getPassword();
			String targetIsManager = staffToDelete.getIsManager();
			managerModel.deleteStaffAccountFromDB(targetID, targetUser, targetPass, targetIsManager);
			staffTableViewObsList.clear();
			staffTableViewObsList.addAll(managerModel.getAllEmployeesFromDB());
			staffTableView.setItems(staffTableViewObsList);
			saveActivityLog("Removed a staff account from the records : " +  targetUser);
			staffUsernameListView.setItems(staffTableViewObsList);
			activityLogTableView.setItems(activityLogObsList);
			manageStaffStatus.setText("Account removed");
		}
		} catch (Exception e) {
			e.printStackTrace();
			manageStaffStatus.setText("Error encountered");
		}
	}
	
	// View activity logs
	
	/**
	 * Gets the username string from the selected ListView staff object and then queries the database using a ManagerModel method
	 * to retrieve all activity log entries for that account and display them to the activity TableView
	 * @throws SQLException 
	 */
	public void getSelectedEmployeeActivity() throws SQLException {
		if (staffUsernameListView.getSelectionModel().getSelectedItem() == null) {
			activitySearchStatus.setText("No staff account selected from listview");
		} else {
			Staff listViewSelectedStaff = staffUsernameListView.getSelectionModel().getSelectedItem();
			String selectedStaffName = listViewSelectedStaff.getUsername();
			activityLogObsList.clear();
			activityLogObsList.addAll(managerModel.getActivityLogForEmployee(selectedStaffName));
			activitySearchStatus.setText(selectedStaffName + " activity log found");
			
		}
		
	}
	
	// =========================== Handle editing menu ===================================
	
	/**
	 * Takes the user inputed name from the GUI and price for a new food item, and then uses a ManagerModel method to 
	 * insert the new item into the menu table of the database.
	 */
	public void saveNewDishToMenu() {
		try {
			String newItemName = manageItemNameTxt.getText();
			double newItemPrice = Double.parseDouble(manageItemCostTxt.getText());
			// carry on if name isn't an empty string or if the price is not a
			// negative double

			if (newItemName != "" && newItemPrice > 0.0) {
				managerModel.addNewDishToMenuDB(newItemName, newItemPrice);
				editableMenuObsList.clear();
				editableMenuObsList.addAll(managerModel.getAllFoodFromMenu());
				managerMenuListView.setItems(editableMenuObsList);
				manageMenuStatus.setText("New dish added.");
				saveActivityLog("Created/Updated a dish: " + newItemName);
			} else {
				manageMenuStatus
						.setText("Invalid dish fields entered, please enter a normal name and a reasonable price");
			}
		} catch (NumberFormatException e) {
			manageMenuStatus.setText("Invalid name or price entered");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Retrieves the name and price for a food object in a selected ListView row and then passes these to a 
	 * ManagerModel method to query the database and delete the corresponding record from the menu.
	 * @throws SQLException 
	 */
	public void deleteDishFromMenu() throws SQLException {
		if (managerMenuListView.getSelectionModel().getSelectedItem() == null) {
			manageMenuStatus.setText("No item was selected from the menu, please click a row from the list and"
					+ " press delete again to remove it from the system");
		} else { 
			Food foodToDelete = managerMenuListView.getSelectionModel().getSelectedItem();
			String targetFoodName = foodToDelete.getMenuItemName();
			Double targetFoodPrice = foodToDelete.getPrice();
			try {
				managerModel.deleteDishFromMenuDB(targetFoodName, targetFoodPrice);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			editableMenuObsList.clear();
			try {
				editableMenuObsList.addAll(managerModel.getAllFoodFromMenu());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			managerMenuListView.setItems(editableMenuObsList);
			manageMenuStatus.setText("Dish was removed.");
			saveActivityLog("Removed a dish from the menu: " + targetFoodName);
		}
	}
	
	// Export handling ===================================================================
	
	/**
	 * Takes a selection of orders from the TableView and puts them into a list, these are then looped through
	 * and their components written to a file in CSV format. This method also takes the filename you want to write to.
	 * Careful not to enter filenames you don't want to overwrite.
	 * @throws SQLException 
	 */
	public void exportSelectedOrders() throws SQLException {
		
		ObservableList<Order> selectedOrders;
		String userFilename = exportFilenameTxt.getText();
		if (userFilename.equals("")) {
			exportStatusTxt.setText("Invalid filename, please enter another.");
		} else if (exportTableView.getSelectionModel().getSelectedItem() == null || 
				exportTableView.getSelectionModel().getSelectedItems() == null) {
			exportStatusTxt.setText("No orders selected to export, please select some rows.");
		} else {
			try {
				selectedOrders = exportTableView.getSelectionModel().getSelectedItems();
				String lineToWrite;
				FileWriter fw = new FileWriter(userFilename + ".csv");
				BufferedWriter bw = new BufferedWriter(fw);
				for (Order o : selectedOrders) {
					// replace commas in order list with plus signs to separate food:price pairs 
					// otherwise csv parsing will separate all order items
					// surround anything which may have commas with speech marks
					lineToWrite = o.getTableNo() + "," + "\"" + o.getOrderList().trim().replaceAll("\\s","") + "\"" + "," + o.getTotalPrice() 
					+ "," + o.getDate() + "," + o.getTime() + "," + "\"" + o.getSpecialRequests() + "\""
					+ "," +  "\"" + o.getComments() + "\"" + "," + o.getCompleted() ; 
					bw.write(lineToWrite); 
					// The whitespace within the orders was confusing the excel csv parser
					// System.out.println(lineToWrite.replaceAll("\\s",""));
					bw.newLine(); // new line for next order
				}
				bw.close();
				exportStatusTxt.setText("Selection of orders exported.");
				saveActivityLog("Exported a list of orders as a CSV file.");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	
	// Import Orders handling ============================================================
	
	/**
	 * This method uses a 3rd party library available from http://opencsv.sourceforge.net/
	 * This takes a CSV file chosen from a JavaFX FileChooser and parses it to retrieve the individual order
	 * components (the inputs for the properties of an Order object) on each line (every new line is a single order) 
	 * and passes each of these arguments to an order constructor
	 * then all the orders are put in an observable list and displayed in a table.
	 */
	public void importCsvFormattedOrder() {
		ArrayList<Order> csvFileOrders = new ArrayList<Order>();
		FileChooser fc = new FileChooser();
		FileChooser.ExtensionFilter exFilter = new FileChooser.ExtensionFilter("Comma separated value files ( .csv)",
				"*.csv");
		fc.getExtensionFilters().add(exFilter);
		File selectedFile = fc.showOpenDialog(null);
		if (selectedFile == null) {
			importStatusTxt.setText("Invalid file chosen.");
		} else if (selectedFile.exists() && selectedFile.canRead()) {
			CSVReader reader;
			try {
				reader = new CSVReader(new FileReader(selectedFile));
				String[] lineToRead;
				while ((lineToRead = reader.readNext()) != null) {
					int tableNoInput = Integer.parseInt(lineToRead[0].trim());
					String orderListInput = lineToRead[1].replaceAll("\\+", ",");
					String totalPriceInput = lineToRead[2];
					String specReqsInput = lineToRead[5];
					String commentsInput = lineToRead[6];
					String dateInput = lineToRead[3];
					String timeInput = lineToRead[4];
					String isCompInput = lineToRead[7];
					csvFileOrders.add(new Order(tableNoInput, orderListInput, totalPriceInput, specReqsInput, commentsInput,
							isCompInput, dateInput, timeInput));
				}
				reader.close();
				ordersForPossibleImport.clear();
				ordersForPossibleImport.addAll(csvFileOrders);
				importTableView.setItems(ordersForPossibleImport);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				importStatusTxt.setText("File not found error");
			}
		}
	}
	
	/**
	 * This takes the observable list of imported orders (displayed in the TableView in this context) then saves and appends them to the
	 * stored orders table in the database.
	 * @throws SQLException 
	 */
	public void saveImportedCsvOrderToSystem() throws SQLException {
		if (ordersForPossibleImport.isEmpty()) {
			importStatusTxt.setText("No CSV file has been imported into the system yet.");
		} else {
			// add imported orders to stored order table in database
			managerModel.saveImportToDB(ordersForPossibleImport);
			// clear the import table
			ordersForPossibleImport.clear();
			importTableView.setItems(ordersForPossibleImport);
			// refresh the export table with all the orders
			ordersForPossibleExport.clear();
			ordersForPossibleExport.addAll(managerModel.getAllOrders());
			exportTableView.setItems(ordersForPossibleExport);
			importStatusTxt.setText("Order saved into the system");
			saveActivityLog("Imported orders from a CSV file into the system ");
		}
	}
	
	// ======================= Log outs etc switching screens etc ========================
	
	/**
	 * Holds onto the username entry of the current user from the login screen and displays it on the GUI
	 * @param user : a String specifying a 'username' field of a staff account record in the database
	 */
	void getUser(String user) {
		lblUser.setText(user);
		//System.out.println(user);
	}
	
	/**
	 * Holds onto the account credentials of the current user when switching between staff and manager screens or vice versa
	 * So you can reverify the account credentials before allowing access back in
	 * @param user : a String specifying a 'username' field of a staff account record in the database
	 * @param pw : a String specifying a 'password' field of a staff account record in the database
	 */
	void storeTemporaryCredentials(String user, String pw) {
		usernameStr = user;
		passwordStr = pw;
	}
	
	// to allow manager to use staff screen no verification needed from this direction
	/**
	 * Upon clicking the staff screen button an event is passed to this method which then hides the manager screen and displays 
	 * the staff screen so the manager can also do some serving if needed or edit current orders.
	 * @param event : an action this method waits for before being called, in this case the clicking of the switch to staff screen button
	 */
	public void switchToStaffScreen(ActionEvent event) {
		try {
			((Node)event.getSource()).getScene().getWindow().hide();
			Stage primaryStage = new Stage();
			FXMLLoader loader = new FXMLLoader();
			Pane root = loader.load(getClass().getResource("/application/StaffScreen.fxml").openStream());
			StaffScreenController staffController = (StaffScreenController)loader.getController();
			staffController.getUser(usernameStr);
			staffController.storeTemporaryCredentials(usernameStr, passwordStr);
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("Staff Screen");
			primaryStage.show();
		} catch (Exception e) {
			System.err.println("Exception Caught");
			e.printStackTrace();
		}
	}
	
	/**
	 * Upon clicking the sign out button the manager screen is hidden, the login screen opened, and the current user logged out, 
	 * so his activity no longer logged.
	 * @param event : an action the method waits for before being called, in this case the user clicking the sign out button
	 */
	public void signOut(ActionEvent event) {
		try {
			saveActivityLog("Logged out");
			LoginController.currentUser = null; // no user logged in now
			((Node)event.getSource()).getScene().getWindow().hide();
			Stage primaryStage = new Stage();
			FXMLLoader loader = new FXMLLoader();
			Pane root = loader.load(getClass().getResource("/application/Login.fxml").openStream());
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("Restaurant Management System");
			primaryStage.show();
			
		} catch (Exception e) {
			System.err.println("Exception Caught");
			e.printStackTrace();
		}
	}
	
	/**
	 * Takes a string description of a user activity or event, generates a date string, and keeps a note
	 * of the currently logged in user, and passes them all to a manager model method to save a record to
	 * the activityLog
	 * @param activity : a String description of an action or event the current user has performed.
	 * @throws SQLException 
	 */
	public void saveActivityLog(String activity) throws SQLException {
		Date timeObject = new Date();
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String dateStr = dateFormat.format(timeObject);
		managerModel.saveActivityEntryToDB(LoginController.currentUser, activity, dateStr);
	}
}