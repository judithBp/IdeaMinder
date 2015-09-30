package io.ideaweave.models;

import io.ideaweave.ideaminder.GeneralSettings;

public class Idea {

	public String _id;
	public String owner;
	public String title;
	public String brief;
	public String language;
	public String[] tags;
	public String[] likerIds;
	public String[] dislikerIds;
	
	public Idea(String title, String brief, String language) {
		owner = GeneralSettings.userId;
		this.title = title;
		this.brief = brief;
		this.language = language;
	}
}
