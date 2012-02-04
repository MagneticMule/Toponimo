package com.magneticmule.toponimo.client.ui;

import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.magneticmule.toponimo.client.R;
import com.magneticmule.toponimo.client.ToponimoApplication;
import com.magneticmule.toponimo.client.ui.adapters.WordListAdapter;
import com.magneticmule.toponimo.client.utils.location.DistanceCalculator;
import com.magneticmule.toponimo.client.utils.maps.CustomOverlay;

public class PlaceDetailsActivity extends MapActivity {

    protected static final String TAG = "PlaceDetailsActivity";
    private static int TAKE_PICTURE = 1;
    private int currentPlaceIndex;
    private String placeName;
    private String placeAddress;
    private String extraData = "";
    private String currentPosLat;
    private String currentPosLng;
    private Double targetPosLat;
    private Double targetPosLng;
    private ListView wordListView;
    private ArrayAdapter<String> wordListArrayAdapter;
    private ToponimoApplication application;
    private ImageView mapImage;
    private RelativeLayout headerViewMap;
    private LinearLayout headerViewWordButton;
    private Button addWordButton;
    private MapView mapView;
    private View mapViewClickReciever;
    private MapController mapController;
    private GeoPoint targetLocation;

    private LayoutInflater inflater;

    private static final short ZOOM_LEVEL = 18; // mapview

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
	super.onActivityResult(requestCode, resultCode, data);

	if (data != null) {
	    // extraData = data.getStringExtra("words");

	    // application.getPlaceResults(application.getCurrentPlaceIndex())
	    // .getWords().add(extraData + " *");
	    wordListArrayAdapter.notifyDataSetChanged();
	}

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.homegrid);

	Intent sender = getIntent();
	application = (ToponimoApplication) getApplication();

	placeName = application.getPlaceResults(
		application.getCurrentPlaceIndex()).getName();
	placeAddress = application.getPlaceResults(
		application.getCurrentPlaceIndex()).getVicinity();
	currentPosLat = sender.getExtras().getString("currentPosLat");
	currentPosLng = sender.getExtras().getString("currentPosLng");

	targetPosLat = application
		.getPlaceResults(application.getCurrentPlaceIndex())
		.getLocation().getLat();
	targetPosLng = application
		.getPlaceResults(application.getCurrentPlaceIndex())
		.getLocation().getLng();

	wordListView = (ListView) findViewById(R.id.place_details_word_listview);

	wordListArrayAdapter = new WordListAdapter(this, R.layout.word_row,
		application.getPlaceResults(application.getCurrentPlaceIndex())
			.getWords());

	inflater = (LayoutInflater) getBaseContext().getSystemService(
		Context.LAYOUT_INFLATER_SERVICE);

	initMap();

	// inflate the second listview header and attach the add word button
	headerViewWordButton = (LinearLayout) inflater.inflate(
		R.layout.add_word_button, null);
	addWordButton = (Button) headerViewWordButton
		.findViewById(R.id.place_details_add_word_button);

	try {
	    wordListView.addHeaderView(headerViewMap);
	    wordListView.addHeaderView(headerViewWordButton);
	    wordListView.setAdapter(wordListArrayAdapter);
	    if (!(wordListView.getCount() < 1)) {
		TextView wordInfoTextView = (TextView) findViewById(R.id.places_info_text);
		wordInfoTextView.setText(Integer.toString(application
			.getPlaceResults(application.getCurrentPlaceIndex())
			.getWords().size())
			+ " words at this location");
	    }
	} catch (Exception e) {

	}

	wordListView.setOnItemClickListener(new OnItemClickListener() {
	    public void onItemClick(AdapterView<?> parent, View v,
		    int position, long id) {
		// TextView wordView = (TextView)
		// v.findViewById(R.id.word_row_word_view);
		Intent intent = new Intent(PlaceDetailsActivity.this,
			WordDetailsActivity.class);
		Log.d("position", Integer.toString(position));
		Log.d("ID", Long.toString(id));
		String word = application
			.getPlaceResults(application.getCurrentPlaceIndex())
			.getWords().get((int) id);

		if (word != null || word.length() != 0) {
		    intent.putExtra("word", word);
		    startActivity(intent);
		}
	    }
	});

	// Set header textviews with appropriate information
	TextView placeTitleView = (TextView) findViewById(R.id.place_details_place_name);
	placeTitleView.setText(placeName);
	TextView placeAddressView = (TextView) findViewById(R.id.place_details_place_address);
	placeAddressView.setText(placeAddress);
	TextView placeDistanceView = (TextView) findViewById(R.id.place_details_place_distance);

	double lat = Double.parseDouble(currentPosLat);
	double lng = Double.parseDouble(currentPosLng);
	double distance = DistanceCalculator.haverSine(lat, lng, targetPosLat,
		targetPosLng);

	String distanceIndicator = " Kilometers from here";
	Double d = (double) Math.round(distance * 1000);
	if (distance < 0.5) {
	    distanceIndicator = " Meters from here";
	} else {
	    distanceIndicator = " Kilometers from here";
	    d /= 1000;
	}
	int roundedDistance = d.intValue();

	placeDistanceView.setText(Integer.toString(roundedDistance)
		+ distanceIndicator);

	addWordButton.setOnClickListener(new View.OnClickListener() {
	    public void onClick(View v) {
		Intent intent = new Intent(PlaceDetailsActivity.this,
			AddWordActivity.class);
		intent.putExtra("position", currentPlaceIndex);
		intent.putExtra("name", placeName);
		final int result = 1;
		startActivityForResult(intent, result);
	    }
	});

	mapViewClickReciever.setOnClickListener(new View.OnClickListener() {
	    public void onClick(View v) {
		Intent intent = new Intent(PlaceDetailsActivity.this,
			MapsViewActivity.class);
		intent.putExtra("targetPosLat", targetPosLat);
		intent.putExtra("targetPosLng", targetPosLng);
		startActivity(intent);
		Log.i(TAG, "MAP PRESSED");

	    }
	});
    }

    private void initMap() {
	// inflate the first listview header view then find and attach the
	// mapview
	headerViewMap = (RelativeLayout) inflater.inflate(R.layout.map_image,
		null);
	mapView = (MapView) headerViewMap
		.findViewById(R.id.place_list_activity_mapview);

	mapViewClickReciever = headerViewMap
		.findViewById(R.id.place_list_activity_click_view);

	mapController = mapView.getController();
	mapController.setZoom(ZOOM_LEVEL);

	List<Overlay> placeOverlays = mapView.getOverlays();
	placeOverlays.clear();
	mapView.invalidate();

	targetLocation = new GeoPoint((int) (targetPosLat * 1e6),
		((int) (targetPosLng * 1e6)));

	Drawable drawable = this.getResources().getDrawable(
		R.drawable.marker_red);

	List<Overlay> overlays = mapView.getOverlays();

	// remove old overlays
	if (overlays.size() > 0) {
	    for (Iterator<Overlay> it = overlays.iterator(); it.hasNext();) {
		it.next();
		it.remove();
	    }
	}

	overlays.add(new CustomOverlay(this, targetLocation,
		R.drawable.marker_red));

	mapController.animateTo(targetLocation);
	mapView.postInvalidate();

    }

    @Override
    protected void onResume() {
	wordListArrayAdapter.notifyDataSetChanged();
	mapController.setZoom(ZOOM_LEVEL);
	mapController.animateTo(targetLocation);
	super.onResume();
    }

    @Override
    public void onAttachedToWindow() {
	super.onAttachedToWindow();
	Window window = getWindow();
	window.setFormat(PixelFormat.RGBA_8888);
	window.addFlags(WindowManager.LayoutParams.FLAG_DITHER);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
	super.onConfigurationChanged(newConfig);
	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected boolean isRouteDisplayed() {
	return false;
    }

}