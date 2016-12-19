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
 * The methods and structure of this class are derived from the tutorials available from the ProgrammingKnowledge playlist 
 * JavaFX tutorial for beginners : https://www.youtube.com/watch?v=9YrmON6nlEw&list=PLS1QulWo1RIaUGP446_pWLgTZPiFizEMq
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
	 * permit either level of access to the program. This hides the login screen upon completion.
	 * @param event : an action the methods waits for, in this instance user clicking the corresponding login button
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
	 * This takes just a simple string 'activity' describing an event or action the currently logged in user performs , it instantaneously 
	 * generates a formated time string at the time of calling the method,
	 * and records the username of the currently logged in user, and passes these to the LoginModel object method to 
	 * save the activity log to the database.
	 * 
	 * @param activity : String describing a users action the managers can look at in the records
	 * @throws SQLException 
	 */
	public void saveActivityLog(String activity) throws SQLException {
		Date timeObject = new Date();
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String dateStr = dateFormat.format(timeObject);
		loginModel.saveActivityEntryToDB(currentUser, activity, dateStr);
		
	}

}
