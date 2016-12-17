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
	
	public void Login(ActionEvent event) {
		try {
			if (loginModel.verifyStaffLogin(txtUsername.getText(), txtPassword.getText())) {
				currentUser = txtUsername.getText();
				lblLoginStatus.setText("Username and Password Correct");
				((Node)event.getSource()).getScene().getWindow().hide();
				Stage primaryStage = new Stage();
				FXMLLoader loader = new FXMLLoader();
				Pane root = loader.load(getClass().getResource("/application/StaffScreen.fxml").openStream());
				StaffScreenController staffController = (StaffScreenController)loader.getController();
				staffController.GetUser(txtUsername.getText());
				staffController.StoreTemporaryCredentials(txtUsername.getText(), txtPassword.getText());
				Scene scene = new Scene(root);
				scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
				primaryStage.setScene(scene);
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
				managerController.GetUser(txtUsername.getText());
				managerController.StoreTemporaryCredentials(txtUsername.getText(), txtPassword.getText());
				Scene scene = new Scene(root);
				scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
				primaryStage.setScene(scene);
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
	
	public void saveActivityLog(String activity) {
		Date timeObject = new Date();
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String dateStr = dateFormat.format(timeObject);
		loginModel.saveActivityEntryToDB(currentUser, activity, dateStr);
	}

}
