package io.ideaweave.ideaminder;

public class Challenge {

	public String _id;
	public String title;
	public String owner;
	public String accessUrl;
	
	public Challenge(String title, String accessUrl) {
		this.title = title;
		this.owner = GeneralSettings.userId;
		this.accessUrl = accessUrl;
	}
}
