package io.ideaweave.models;

public class User {

	public String _id;
	public String email;
	public String password;
	
	public User(String email, String password) {
		this.email = email;
		this.password = password;
	}
}
