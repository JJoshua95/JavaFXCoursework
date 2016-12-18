package application;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * This class provides the logic (other than direct database interactions) to the LoginScreen GUI
 * @author jarrod joshua
 */
public class LoginController implements Initializable {
	
	public LoginModel loginModel = new LoginModel();
	
	@FXML
	private TextField txtUsername;
	@FXML
	private TextField txtPassword;
	@FXML
	private Label lblDatabaseStatus;
	@FXML
	private Label lblLoginStatus;
	
	public static String currentUser; // for tracking staff activity
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		if (loginModel.isDbConnected()) {
			lblDatabaseStatus.setText("Connected");
		} else {
			lblDatabaseStatus.setText("Not Connected");
		}
	}
	
	/**
	 * This method is called upon a user pressing the login button it validates whether the credentials they entered are valid
	 * by calling the verify methods from the LoginModel object and then opens the manager screen or the staff screen if the login inputs 
	 * permits that. This hides the login screen upon completion.
	 * @param event
	 */
	public void login(ActionEvent event) {
		try {
			if (loginModel.verifyStaffLogin(txtUsername.getText(), txtPassword.getText())) {
				currentUser = txtUsername.getText();
				lblLoginStatus.setText("Username and Password Correct");
				((Node)event.getSource()).getScene().getWindow().hide();
				Stage primaryStage = new Stage();
				FXMLLoader loader = new FXMLLoader();
				Pane root = loader.load(getClass().getResource("/application/StaffScreen.fxml").openStream());
				StaffScreenController staffController = (StaffScreenController)loader.getController();
				staffController.getUser(txtUsername.getText());
				staffController.storeTemporaryCredentials(txtUsername.getText(), txtPassword.getText());
				Scene scene = new Scene(root);
				scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
				primaryStage.setScene(scene);
				primaryStage.setTitle("Staff Screen");
				primaryStage.show();
				saveActivityLog("Logged in");
			} else if (loginModel.verifyManagerLogin(txtUsername.getText(), txtPassword.getText())) {
				currentUser = txtUsername.getText();
				lblLoginStatus.setText("Username and Password Correct");
				//loginModel.closeConnection();
				((Node)event.getSource()).getScene().getWindow().hide();
				Stage primaryStage = new Stage();
				FXMLLoader loader = new FXMLLoader();
				Pane root = loader.load(getClass().getResource("/application/ManagerScreen.fxml").openStream());
				ManagerScreenController managerController = (ManagerScreenController)loader.getController();
				managerController.getUser(txtUsername.getText());
				managerController.storeTemporaryCredentials(txtUsername.getText(), txtPassword.getText());
				Scene scene = new Scene(root);
				scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
				primaryStage.setScene(scene);
				primaryStage.setTitle("Manager Screen");
				primaryStage.show();
				saveActivityLog("Logged in");
			} else {
				lblLoginStatus.setText("Username and/or Password Incorrect");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("SQLerr");
			e.printStackTrace();
			lblLoginStatus.setText("Username and/or Password Incorrect");
		} catch (IOException e) {
			System.out.println("IOerr");
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	/**
	 * This takes just a simple string describing an event or action the currently logged in user performs , it generates a time
	 * string and records the username of the currently logged in user, and passes these to the LoginModel object method to 
	 * save the activity log to the database.
	 * 
	 * @param activity String
	 * @throws SQLException 
	 */
	public void saveActivityLog(String activity) throws SQLException {
		Date timeObject = new Date();
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String dateStr = dateFormat.format(timeObject);
		loginModel.saveActivityEntryToDB(currentUser, activity, dateStr);
		
	}

}
