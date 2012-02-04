package com.magneticmule.toponimo.client.ui;

import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.magneticmule.toponimo.client.Constants;
import com.magneticmule.toponimo.client.R;
import com.magneticmule.toponimo.client.ToponimoApplication;
import com.magneticmule.toponimo.client.utils.maps.CustomOverlay;
import com.magneticmule.toponimo.client.utils.maps.Locations;

public class MapsViewActivity extends MapActivity implements LocationListener {

    private MapView mapView;
    private MapController mapController;
    private MyLocationOverlay myLocationOverlay;
    LocationManager locationManager;
    Location location;
    ToponimoApplication application;

    private static final short ZOOM_LEVEL = 18; // mapview
    private static final short MIN_TIME = 5000; // milliseconds
    private static final short MIN_DISTANCE = 5; // distance

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	setContentView(R.layout.mapview);
	application = (ToponimoApplication) getApplicationContext();

	int currentPlaceIndex = application.getCurrentPlaceIndex();
	createInfoView(currentPlaceIndex);

	Intent sender = getIntent();
	initMap();
	initLocationManager();
    }

    private void createInfoView(int index) {
	TextView locationName = (TextView) findViewById(R.id.mapview_place_name);
	TextView locationAddress = (TextView) findViewById(R.id.mapviews_place_address);

	locationName.setText(application.getPlaceResults(
		application.getCurrentPlaceIndex()).getName());
	locationAddress.setText(application.getPlaceResults(
		application.getCurrentPlaceIndex()).getVicinity());

    }

    public void onLocationChanged(Location location) {

	mapView.postInvalidate();
    }

    public void onProviderDisabled(String arg0) {
	locationManager.removeUpdates(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onBackPressed()
     */
    @Override
    public void onBackPressed() {
	// TODO Auto-generated method stub
	super.onBackPressed();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onStart()
     */
    @Override
    protected void onStart() {
	// TODO Auto-generated method stub
	super.onStart();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onStop()
     */
    @Override
    protected void onStop() {
	// TODO Auto-generated method stub
	super.onStop();
	locationManager.removeUpdates(this);
    }

    public void onProviderEnabled(String arg0) {
	// GPS on

    }

    public void onStatusChanged(String arg0, int status, Bundle extras) {
	// TODO Auto-generated method stub

    }

    @Override
    protected boolean isRouteDisplayed() {
	// is there a route being displayed in map view?
	return false;
    }

    /**
     * Init maps and add the default zoom control
     */
    private void initMap() {
	mapView = (MapView) findViewById(R.id.mapview_control);
	mapController = mapView.getController();
	myLocationOverlay = new MyLocationOverlay(this, mapView);

	// mapView.getOverlays().clear();

	// mapView.postInvalidate();

	myLocationOverlay.runOnFirstFix(new Runnable() {

	    public void run() {

		mapView.getOverlays().add(myLocationOverlay);
		mapView.postInvalidate();
	    }
	});

	mapController.setZoom(ZOOM_LEVEL);
	mapView.setBuiltInZoomControls(true);
	mapView.setClickable(true);
	mapView.setEnabled(true);
	// mapView.preLoad();

	List<Overlay> overlays = mapView.getOverlays();

	// remove old overlays
	if (overlays.size() > 0) {
	    for (Iterator<Overlay> it = overlays.iterator(); it.hasNext();) {
		it.next();
		it.remove();
	    }
	}

	// convert mLocation to int based geopoint

	// GeoPoint gPoint2 = new GeoPoint((int) (lat.doubleValue() * 1e6),
	// ((int) (lng.doubleValue() * 1e6)));

	// GeoPoint gPoint2 = new GeoPoint((int) (52.9551 ),
	// (int) (-1.1946 * 1e6));
	// CustomOverlay customOverlay2 = new CustomOverlay(gPoint2);

	// add custom overlay to map view
	// overlays.add(customOverlay2);
	// overlays.addAll(placeOverlays);
	// overlays.add(myLocationOverlay);

	// redraw map

	// locationManager.removeUpdates(this);

	Drawable drawable = this.getResources().getDrawable(
		R.drawable.marker_red);
	Locations locations = new Locations(drawable, this);

	double lat = 0d;
	double lng = 0d;
	OverlayItem customOverlay = null;

	lat = (application.getPlaceResults(application.getCurrentPlaceIndex())
		.getLocation().getLat());
	lng = (application.getPlaceResults(application.getCurrentPlaceIndex())
		.getLocation().getLng());

	GeoPoint gPoint = new GeoPoint((int) (lat * 1e6), ((int) (lng * 1e6)));
	customOverlay = new OverlayItem(gPoint, "", "");

	locations.addOverlay(customOverlay);
	// placeOverlays.add(itemizedOverlay);
	// overlays.add(myOverlay);
	overlays.add(new CustomOverlay(this, gPoint, R.drawable.marker_red));

	customOverlay.setMarker(drawable);
	locations.populateNow();
	overlays.add(locations);
	mapController.animateTo(gPoint);
	mapView.postInvalidate();

    }

    private void initLocationManager() {

	String context = Context.LOCATION_SERVICE;
	locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	Criteria criteria = Constants.fastCriteria();
	String bestProvider = locationManager.getBestProvider(criteria, true);
	location = locationManager.getLastKnownLocation(bestProvider);
	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
		0, this);

    }

    private void searchMap(String searchString) {
	// TODO Map search implementation
    }

    @Override
    protected boolean isLocationDisplayed() {
	// TODO Auto-generated method stub
	return super.isLocationDisplayed();
    }

    @Override
    protected void onDestroy() {
	super.onDestroy();
	locationManager.removeUpdates(this);
	// myLocationOverlay.disableCompass();
	// myLocationOverlay.disableMyLocation();

    }

    @Override
    protected int onGetMapDataSource() {
	// TODO Auto-generated method stub
	return super.onGetMapDataSource();
    }

    @Override
    protected void onPause() {
	super.onPause();
	myLocationOverlay.disableMyLocation();
	locationManager.removeUpdates(this);
	myLocationOverlay.disableMyLocation();

    }

    @Override
    protected void onResume() {
	super.onResume();
	myLocationOverlay.enableMyLocation();
	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
		Constants.MIN_TIME, Constants.MIN_DISTANCE, this);
	myLocationOverlay.enableMyLocation();
    }

}