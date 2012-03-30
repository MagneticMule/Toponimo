package com.magneticmule.toponimo.client;

import java.util.List;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpParams;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.magneticmule.toponimo.client.structures.placestructure.Place;
import com.magneticmule.toponimo.client.structures.placestructure.Results;
import com.magneticmule.toponimo.client.structures.userstructure.UserDetails;

public class ToponimoApplication extends Application {

    public static final String APP_NAME = "ToponimoApplication";
    public static final String TAG = "ToponimoApplication";

    /**
     * Store a reference to the application object
     */
    private static ToponimoApplication app = null;

    private SharedPreferences userDetailsPrefs;

    private static Context context;
    private DefaultHttpClient httpClient;

    /**
     * placeResults holds a global reference to the PlaceStructure data. This
     * allows each class to access the placeResults without resorting to passing
     * values via bundled extras.
     */
    private List<Place> placeResults = null;
    private UserDetails userDetails;

    private String currentPlaceId = null;

    private int currentPlaceIndex = 0;

    public ToponimoApplication() {
	super();

    }

    @Override
    public void onCreate() {
	// checkInstance();
	super.onCreate();
	ToponimoApplication.context = getApplicationContext();
	ToponimoApplication.app = this;

	// Reference to the login details shared preferences file
	userDetailsPrefs = getSharedPreferences(Constants.USER_DETAILS_PREFS, 0);

	// Httpclient object which will be used throughout the application
	httpClient = new DefaultHttpClient();
	ClientConnectionManager manager = httpClient.getConnectionManager();
	HttpParams params = httpClient.getParams();
	httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(
		params, manager.getSchemeRegistry()), params);
	Log.i(TAG, "Tooponimo app started");

    }

    @Override
    public void onTerminate() {
	super.onTerminate();
	if (httpClient != null) {
	    httpClient.getConnectionManager().shutdown();
	}
	Log.i(TAG, "Tooponimo application ended.");
    }

    public DefaultHttpClient getHttpClient() {
	return httpClient;
    }

    public void setHttpClient(DefaultHttpClient httpClient) {
	this.httpClient = httpClient;
    }

    public UserDetails getUserDetails() {
	if (this.userDetails == null) {
	    this.userDetails = new UserDetails();
	    userDetails.setUserId(userDetailsPrefs.getString(Constants.USER_ID,
		    null));
	    userDetails.setFirstName(userDetailsPrefs.getString(
		    Constants.FIRST_NAME, null));
	    userDetails.setLastName(userDetailsPrefs.getString(
		    Constants.LAST_NAME, null));
	}

	return this.userDetails;
    }

    public void setUserDetails(UserDetails userDetails) {
	this.userDetails = userDetails;
    }

    public void setPlaceResults(List<Place> placeResults) {
	checkInstance();
	this.placeResults = placeResults;
    }

    public Results getPlaceResults(int position) {
	checkInstance();
	return placeResults.get(position).getResults().get(position);

    }

    public int getCurrentPlaceIndex() {
	checkInstance();
	return currentPlaceIndex;
    }

    public String getCurrentPlaceId() {
	checkInstance();
	return currentPlaceId;
    }

    public void setCurrentPlaceId(String currentPlaceId) {
	checkInstance();
	this.currentPlaceId = currentPlaceId;
    }

    public void setCurrentPlaceIndex(int currentPlaceIndex) {
	checkInstance();
	this.currentPlaceIndex = currentPlaceIndex;
    }

    public static Context getGlobalContext() {
	checkInstance();
	return ToponimoApplication.context;
    }

    public static ToponimoApplication getApp() {
	checkInstance();
	return app;

    }

    public SharedPreferences getUserDetailsPrefs() {
	return this.userDetailsPrefs;
    }

    public static void checkInstance() {
	if (app == null) {
	    throw new IllegalStateException(
		    "Application has not been initialized : " + TAG);
	}
    }

}
