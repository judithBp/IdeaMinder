package io.ideaweave.models;

import io.ideaweave.ideaminder.GeneralSettings;

public class IdeaWithOwner {

	public String _id;
	public User owner;
	public String title;
	public String brief;
	public String language;
	public String[] tags;
	public String[] likerIds;
	public String[] dislikerIds;
	
}
