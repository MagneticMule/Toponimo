package com.magneticmule.toponimo.client.utils.location;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.util.Log;

import com.magneticmule.toponimo.client.Constants;
import com.magneticmule.toponimo.client.ToponimoApplication;

public class LocationUpdateService extends IntentService {

    protected static String TAG = "LocationUpdateService";

    protected ConnectivityManager connectivityManager;
    protected ContentResolver contentResolver;
    protected LastLocationRequester llr;
    Location lastLocation;
    LocationManager locationManager;

    public LocationUpdateService() {
	super(TAG);
	setIntentRedelivery(false);
    }

    @Override
    public void setIntentRedelivery(boolean enabled) {
	super.setIntentRedelivery(enabled);
    }

    @Override
    public void onCreate() {
	super.onCreate();
	connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	locationManager = (LocationManager) this
		.getSystemService(Context.LOCATION_SERVICE);
	Log.d(TAG, "Service Created");
    }

    @Override
    public IBinder onBind(Intent intent) {
	return super.onBind(intent);
    }

    @Override
    public void onDestroy() {
	super.onDestroy();
	Log.d(TAG, "Service Destroyed");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
	Log.d(TAG, "Service Running");
	connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	llr = new LastLocationRequester(ToponimoApplication.getGlobalContext());
	Location location = new Location(llr.lastLocation(Constants.MIN_TIME,
		Constants.MIN_DISTANCE));
	broadcastLocation(location);

    }

    protected void broadcastLocation(Location location) {

	// extract latitude and longitude from location
	Double lat = location.getLatitude();
	Double lng = location.getLongitude();
	Intent intent = new Intent();
	intent.putExtra("lat", lat.toString());
	intent.putExtra("lng", lng.toString());
	intent.putExtra("location", location);
	intent.setAction("LOCATION");
	Log.d(TAG, lat.toString());
	Log.d(TAG, lng.toString());

	// broadcast the intent
	sendBroadcast(intent);

    }

}
