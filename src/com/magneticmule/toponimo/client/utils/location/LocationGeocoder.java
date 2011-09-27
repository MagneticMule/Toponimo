package com.magneticmule.toponimo.client.utils.location;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class LocationGeocoder {

	private static final String	TAG	= "LocationGeocoder";

	public static void getLocationName(final double lat, final double lng, final Context context, final Handler handler) {
		Thread thread = new Thread() {
			public void run() {
				Geocoder geocoder = new Geocoder(context, Locale.getDefault());
				String result = null;
				try {
					List<Address> addressList = geocoder.getFromLocation(lat, lng, 1);
					if (addressList != null && addressList.size() > 0) {
						Address address = addressList.get(0);
						result = address.getAddressLine(0);
					}
				} catch (IOException e) {
					Log.e(TAG, "Can't connect to Geocoder", e);
				} finally {
					Message message = Message.obtain();
					message.setTarget(handler);
					if (result != null) {
						message.what = 1;
						Bundle bundle = new Bundle();
						bundle.putString("address", result);
						message.setData(bundle);
					} else
						message.what = 0;
					message.sendToTarget();
				}
			}
		};
		thread.start();

	}

}
