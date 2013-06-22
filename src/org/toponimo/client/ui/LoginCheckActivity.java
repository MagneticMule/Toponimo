package org.toponimo.client.ui;

import org.toponimo.client.Constants;
import org.toponimo.client.R;
import org.toponimo.client.ToponimoApplication;

import com.google.analytics.tracking.android.EasyTracker;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public class LoginCheckActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	protected void onResume() {
		super.onResume();
		ToponimoApplication application = (ToponimoApplication) getApplication();
		if (application.isLoggedIn()) {
			Log.d("Toponimo: Login", "SUCCESS");
			Intent i = new Intent(LoginCheckActivity.this, JournalActivity.class);
			startActivity(i);
		} else { // Start the Login activity
			Log.d("Toponimo: Login", "FAIL");
			Intent i = new Intent(LoginCheckActivity.this, LoginActivity.class);
			startActivity(i);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		// Start analytics tracking
		EasyTracker.getInstance().activityStart(this);

	}

	@Override
	protected void onStop() {
		super.onStop();
		EasyTracker.getInstance().activityStop(this);
	}

}