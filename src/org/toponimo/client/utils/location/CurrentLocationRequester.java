package org.toponimo.client.utils.location;

import android.location.LocationManager;
import org.toponimo.client.R;

public class CurrentLocationRequester {

    private static String TAG = "CurrentLocationRequester";

    // store whether the gps and wifi are enabled on the phone
    boolean gpsEnabled = false;
    boolean wifiEnabled = false;

    LocationManager locationManager;

}
