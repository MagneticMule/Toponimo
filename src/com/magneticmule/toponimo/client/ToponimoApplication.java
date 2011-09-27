package com.magneticmule.toponimo.client;

import java.util.List;

/**
 * Singleton to hold common state of app. As long as app is running, this object
 * will be accessible.
 */

import com.magneticmule.toponimo.client.placestructure.Place;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class ToponimoApplication extends Application {

	/**
	 * placeResults holds a global reference to the PlaceStructure bean. This
	 * allows each class to access the placeResults without resorting to passing
	 * values via bundled extras.
	 */
	private List<Place>									placeResults			= null;
	private Place												currentPlace			= null;
	private int													currentPlaceIndex	= 0;

	public static final String					APP_NAME					= "ToponimoApplication";
	public static final String					TAG								= "ToponimoApplication";
	private static Context							context;

	/**
	 * Store a reference to the application object
	 */
	private static ToponimoApplication	app								= null;

	/**
	 * Provide an instance to the application object
	 */
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
		Log.i(TAG, "Tooponimo application ended.");
	}

	public void setPlaceResults(List<Place> placeResults) {
		checkInstance();
		this.placeResults = placeResults;
	}

	public List<Place> getPlaceResults() {
		checkInstance();
		return placeResults;

	}

	public Place getCurrentPlace() {
		checkInstance();
		return this.currentPlace;
	}

	public void setCurrentPlace(Place place) {
		checkInstance();
		this.currentPlace = place;
	}
	
	public int getCurrentPlaceIndex() {
		checkInstance();
		return currentPlaceIndex;
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
			throw new IllegalStateException("Application has not been initialized! : " + TAG);
		}
	}


}
