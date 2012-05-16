package org.toponimo.client.utils.location;

import java.util.List;

import org.toponimo.client.Constants;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;


public class LastLocationRequester {

    private static String TAG = "LastLocationRequester";

    protected LocationListener locationListener;
    protected LocationManager locationManager;
    protected Criteria accurateCriteria;
    protected Criteria fastCriteria;
    protected Context context;

    /**
     * Request location updates using the quickest method possible. More
     * accurate results can be obtained using synchronous location updates with
     * an accurate criteria.
     * 
     * @param context
     */

    public LastLocationRequester(Context context) {
	locationManager = (LocationManager) context
		.getSystemService(Context.LOCATION_SERVICE);
	accurateCriteria = Constants.accurateCriteria();
	fastCriteria = Constants.fastCriteria();
	this.context = context;
    }

    public Location lastLocation(long minTime, float minDistance) {
	Location bestLocation = null;
	float bestAccuracy = Float.MAX_VALUE;
	long bestTime = Long.MAX_VALUE;

	List<String> providers = locationManager.getAllProviders();
	for (String provider : providers) {
	    Location location = locationManager.getLastKnownLocation(provider);
	    if (location != null) {
		float accuracy = location.getAccuracy();
		long time = location.getTime();
		Log.i("Provider", location.getProvider().toString());

		if (time < minTime && accuracy < bestAccuracy) {
		    bestLocation = location;
		    bestAccuracy = accuracy;
		    bestTime = time;
		} else if (time > minTime && bestAccuracy == Float.MAX_VALUE
			&& time < bestTime) {
		    bestLocation = location;
		    bestTime = time;
		}
	    }
	}

	if ((locationListener != null) && (bestTime > minTime)
		|| (bestAccuracy > minDistance)) {
	    String provider = locationManager.getBestProvider(fastCriteria,
		    true);
	    if (provider != null) {
		locationManager.requestLocationUpdates(provider, 5, 10,
			oneShotLocationListener, context.getMainLooper());
	    }

	}
	return bestLocation;

    }

    public LocationListener oneShotLocationListener = new LocationListener() {

	public void onLocationChanged(Location location) {
	    if ((location != null) && (locationListener != null)) {
		locationListener.onLocationChanged(location);
		locationManager.removeUpdates(oneShotLocationListener);

	    }

	}

	public void onProviderDisabled(String provider) {
	}

	public void onProviderEnabled(String provider) {
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

    };

    public void setChangedLocationListener(LocationListener l) {
	locationListener = l;
    }

    public void cancel() {
	locationManager.removeUpdates(oneShotLocationListener);
    }
}
