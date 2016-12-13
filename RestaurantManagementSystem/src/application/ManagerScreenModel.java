package application;

import java.sql.Connection;
import java.sql.SQLException;

public class ManagerScreenModel {
	
private Connection connection;
	
	ManagerScreenModel() {
		connection = SqliteConnection.Connector();
		if (connection == null) {
			System.exit(1);
		}
	}
	
	boolean isDbConnected() {
		try {
			return !connection.isClosed();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

}
