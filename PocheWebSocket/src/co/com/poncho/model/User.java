package co.com.poncho.model;

public class User {

	String name;
	int id;
	private boolean status;
	
	public String getName() {
		return name;
	}
	
	public int getId() {
		return id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public User() {
		// TODO Auto-generated constructor stub
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}
	
	
}
