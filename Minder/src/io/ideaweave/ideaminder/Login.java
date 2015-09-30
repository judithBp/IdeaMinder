package io.ideaweave.ideaminder;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import io.ideaweave.ideaminder.R;
import io.ideaweave.models.Profile;
import io.ideaweave.models.User;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.LoginFilter.UsernameFilterGeneric;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity implements OnClickListener, ActiveActivity {
	private static final String TAG = "LoginActivity";
	private static final int RC_SIGN_IN = 0;
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	private EditText emailText;
	private EditText passwordText;
	private String email;
	private String password;

	private Button btnSignIn;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_login_activity);
		
		Bundle extras = getIntent().getExtras();
		if(extras != null && extras.getBoolean("logout") == true) {
			GeneralSettings.token = "";
			finish();
			getIntent().removeExtra("logout");
			startActivity(getIntent());
		}
		else if(extras != null && extras.getBoolean("delete") == true) {
			//TODO function on server for deleting profile
			btnSignIn.setClickable(false);
		}
		else {
			if (checkPlayServices()) {
				btnSignIn = (Button) findViewById(R.id.sign_in_button);
				btnSignIn.setOnClickListener(this);
				btnSignIn.setClickable(true);
				
				//New code
				emailText = (EditText) findViewById(R.id.email);
				passwordText = (EditText) findViewById(R.id.password);
				
				GeneralSettings.restAdapter = new RestAdapter.Builder()
				.setEndpoint("https://api.ideaweave.io")
				//.setEndpoint("http://:5011")
				.build();
				
				GeneralSettings.ideaweave = GeneralSettings.restAdapter.create(IdeaweaveService.class);
				GeneralSettings.activity = this;
			} else {
				Toast.makeText(this, "No valid Google Play Services APK found.",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.sign_in_button) {
			if(btnSignIn.isClickable()) {
				
				//New code
				Log.d(TAG, "sign in button clicked");
				btnSignIn.setEnabled(false);
				
				email = emailText.getText().toString();
				password = passwordText.getText().toString();
				
				User user = new User(email, password);
				GeneralSettings.ideaweave.login(user)
				.observeOn(AndroidSchedulers.mainThread()).
				subscribe(new Action1<ResultMessage>() {

					@Override
					public void call(ResultMessage m) {
							GeneralSettings.token = m.token;
							GeneralSettings.userId = m.user_id;
							Log.d(TAG, m.token + ", " + m.user_id);

							GeneralSettings.activity.next();
					}
				}, new Action1<Throwable>() {

					@Override
					public void call(Throwable t1) {
						// TODO Auto-generated method stub
						Toast.makeText((Context) GeneralSettings.activity, "Wrong email and/or password", Toast.LENGTH_LONG).show();
						Log.d(TAG, "problem with logging in: " + t1.getMessage());
						btnSignIn.setEnabled(true);
					}
				});
				}
		}
	}
	
	public void next() {
		Intent intent = new Intent(this, Homepage.class);
		startActivity(intent);
		finish();
	}
 
	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Toast.makeText(this, "This device is not supported.",
						Toast.LENGTH_LONG).show();
				finish();
			}
			return false;
		}
		return true;
	}
}
