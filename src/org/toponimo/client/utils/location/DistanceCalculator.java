package org.toponimo.client.utils.location;

import org.toponimo.client.R;

public class DistanceCalculator {

    public static double haverSine(double lat1, double lng1, double lat2,
	    double lng2) {
	double miles = 3958.75;
	double kmeters = 6371;
	double earthRadius = kmeters;

	double dLat = Math.toRadians(lat2 - lat1);
	double dLng = Math.toRadians(lng2 - lng1);
	double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
		+ Math.cos(Math.toRadians(lat1))
		* Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2)
		* Math.sin(dLng / 2);
	double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	double distance = earthRadius * c;

	return distance;
    }

}
