/**
 * 
 */
package org.toponimo.client.utils.maps;

import android.content.Context;
import android.graphics.Canvas;
import android.location.Location;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

/**
 * @author tommy
 *
 */
public class LocationOverlayHandler extends MyLocationOverlay {

	public LocationOverlayHandler(Context arg0, MapView arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.google.android.maps.MyLocationOverlay#drawMyLocation(android.graphics.Canvas, com.google.android.maps.MapView, android.location.Location, com.google.android.maps.GeoPoint, long)
	 */
	@Override
	protected void drawMyLocation(Canvas canvas, MapView mapView,
			Location lastFix, GeoPoint myLocation, long when) {
		// TODO Auto-generated method stub
		super.drawMyLocation(canvas, mapView, lastFix, myLocation, when);
	}

}
