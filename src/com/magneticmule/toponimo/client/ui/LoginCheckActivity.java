package com.magneticmule.toponimo.client.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.magneticmule.toponimo.client.Constants;
import com.magneticmule.toponimo.client.ToponimoApplication;

public class LoginCheckActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	ToponimoApplication application = (ToponimoApplication) this
		.getApplication();
	SharedPreferences userDetailsPreferences = application
		.getUserDetailsPrefs();

	if (userDetailsPreferences.contains(Constants.IS_LOGGED_IN)) {
	    if (userDetailsPreferences.getBoolean(Constants.IS_LOGGED_IN, true)) {
		Intent i = new Intent(LoginCheckActivity.this,
			PlaceListActivity.class);
		startActivity(i);
	    }

	} else {
	    Intent i = new Intent(LoginCheckActivity.this, LoginActivity.class);
	    startActivity(i);
	}
    }

}
