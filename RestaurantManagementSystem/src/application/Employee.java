package application;

public class Employee {
	
	private final boolean isManager = false;
	private String username;
	private String password;
	
	public Employee(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	public boolean verifyLogin(String username, String password) {
		
		boolean verification;
		if (password.equals("pass")) {
			verification = true;
		} else {
			verification = false;
		}
		
		return verification;
		
	}

}
