package io.ideaweave.ideaminder;

import io.ideaweave.models.Tag;

import java.util.List;

import android.app.Activity;
import android.widget.Toast;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class GeneralSettings {

	public static String SENDER_ID = "893319302071";
	public static String[] languages = { "English", "Francais", "Deutsch",
			"Espanol" };
	public static boolean logout = false;
	public static int language = 0;
	public static boolean tags;
	public static List<Tag> tagsArray;
	public static boolean refresh;
	public static boolean deleteAccount = false;
	
	//New code
	public static RestAdapter restAdapter;
	public static IdeaweaveService ideaweave;
	public static String token;
	public static String userId;
	public static ActiveActivity activity;
}
