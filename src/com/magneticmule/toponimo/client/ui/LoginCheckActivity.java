package com.magneticmule.toponimo.client.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.magneticmule.toponimo.client.Constants;
import com.magneticmule.toponimo.client.ToponimoApplication;
import com.magneticmule.toponimo.client.structures.userstructure.UserDetails;

public class LoginCheckActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	ToponimoApplication application = (ToponimoApplication) this
		.getApplication();
	SharedPreferences userDetailsPreferences = getSharedPreferences(
		Constants.USER_DETAILS_PREFS, 0);

	if (userDetailsPreferences.contains(Constants.IS_LOGGED_IN)) {
	    if (userDetailsPreferences
		    .getBoolean(Constants.IS_LOGGED_IN, false)) {

		// set global user details
		UserDetails userDetails = new UserDetails();
		userDetails.setUserId(userDetailsPreferences.getString(
			Constants.USER_ID, null));
		userDetails.setFirstName(userDetailsPreferences.getString(
			Constants.FIRST_NAME, null));
		userDetails.setLastName(userDetailsPreferences.getString(
			Constants.LAST_NAME, null));
		application.setUserDetails(userDetails);

		Intent i = new Intent(LoginCheckActivity.this,
			PlaceListActivity.class);

		Log.v("Firstname", userDetails.getFirstName());
		Log.v("Lastname", userDetails.getLastName());

		startActivity(i);
	    }

	} else { // Start the Login activity
	    Intent i = new Intent(LoginCheckActivity.this, LoginActivity.class);
	    i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	    startActivity(i);
	}
    }

}
