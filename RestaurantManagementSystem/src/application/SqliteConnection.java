package application;

import java.sql.*; 

// This class makes use of a 3rd party library, the SQLite JDBC Driver 
// available from https://bitbucket.org/xerial/sqlite-jdbc/downloads

/**
 * This class establishes a connection with a local SQLite database file called 'RestaurantDB.sqlite'
 * It contains a method Connector() which
 * either makes use of this file in the same directory, or if the file is not found it will create an empty database of the same name
 * This class makes use of a 3rd party library, the SQLite JDBC Driver 
 * available from https://bitbucket.org/xerial/sqlite-jdbc/downloads
 * 
 * @author jarrod joshua
 */

public class SqliteConnection {
	
	/**
	 * The Connector method establishes a link with a locally saved database named RestaurantDB.sqlite which contains 
	 * information like passwords and accounts details, this allows the java program to access data values stored in it and to 
	 * store further data in it.
	 * If there is no database named "RestaurantDB.sqlite" then calling this method will create a blank database with the 
	 * same name in the working directory.
	 * Needed to download latest version of SQLite JDBC driver from https://bitbucket.org/xerial/sqlite-jdbc/downloads and add 
	 * it to build path.
	 * @return connection - A Connection type object - "A connection (session) with a specific database. SQL statements are executed and results 
	 * are returned within the context of a connection.".
	 */
	public static Connection Connector() {
		try {
			Class.forName("org.sqlite.JDBC");
			Connection connection = DriverManager.getConnection("jdbc:sqlite:RestaurantDB.sqlite");
			return connection;
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}

}