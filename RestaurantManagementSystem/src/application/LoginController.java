package application;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
	
	@FXML
	private Label lblStatus;
	@FXML
	private TextField txtUsername;
	@FXML
	private TextField txtPassword;
	@FXML
	private Button btnLogin;
	
	
	public void Login() throws IOException {
		if ( txtUsername.getText().equals("user") && txtPassword.getText().equals("pass") ) {
			lblStatus.setText("Successful Login");
			
			Stage primaryStage = new Stage();
			Parent root = FXMLLoader.load(getClass().getResource("/application/OpeningScreen.fxml"));
			Scene scene = new Scene(root,300,300);
			//scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
			
		} else {
			lblStatus.setText("Failed to login");
		}
		
	}

}
