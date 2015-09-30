package io.ideaweave.ideaminder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import io.ideaweave.ideaminder.R;

public class SplashActivity extends Activity {

	private static final String TAG = "SplashActivity";

	protected int _splashTime = 1000;
	protected boolean _active = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_activity);

		Thread splashThread = new Thread() {
			@Override
			public void run() {
				try {
					int waited = 0;
					while (_active && (waited < _splashTime)) {
						sleep(100);
						if (_active) {
							waited += 100;
						}
					}
				} catch (InterruptedException e) {
					System.out
							.println("Exception in SplashActivity while waiting "
									+ e.toString());
				} finally {
					Intent mIntent = new Intent(SplashActivity.this,
							Login.class);
					startActivity(mIntent);
					finish();
				}
			}
		};
		splashThread.start();
	}
}
