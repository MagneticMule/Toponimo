package com.magneticmule.toponimo.client;

import java.util.List;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpParams;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.magneticmule.toponimo.client.structures.placestructure.Place;
import com.magneticmule.toponimo.client.structures.placestructure.Results;
import com.magneticmule.toponimo.client.structures.userstructure.UserDetails;

public class ToponimoApplication extends Application {

	/**
	 * Store a reference to the application object
	 */
	private static ToponimoApplication app = null;

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

	public static final String APP_NAME = "ToponimoApplication";
	public static final String TAG = "ToponimoApplication";

	public ToponimoApplication() {
		super();
		httpClient = new DefaultHttpClient();
		ClientConnectionManager manager = httpClient.getConnectionManager();
		HttpParams params = httpClient.getParams();
		httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(
				params, manager.getSchemeRegistry()), params);
	}

	public void onCreate() {
		// checkInstance();
		super.onCreate();
		ToponimoApplication.context = getApplicationContext();
		ToponimoApplication.app = this;
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
		return userDetails;
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

	public static void checkInstance() {
		if (app == null) {
			throw new IllegalStateException(
					"Application has not been initialized : " + TAG);
		}
	}

}
