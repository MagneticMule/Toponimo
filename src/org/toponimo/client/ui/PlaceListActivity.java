package org.toponimo.client.ui;

import java.util.ArrayList;
import java.util.List;

import org.toponimo.client.ApiKeys;
import org.toponimo.client.Constants;
import org.toponimo.client.R;
import org.toponimo.client.ToponimoApplication;
import org.toponimo.client.models.Place;
import org.toponimo.client.models.Results;
import org.toponimo.client.ui.adapters.PlacesListAdapter;

import org.toponimo.client.utils.http.HttpUtils;
import org.toponimo.client.utils.http.ToponimoVolley;
import org.toponimo.client.utils.location.LocationUpdateService;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

public class PlaceListActivity extends SherlockFragmentActivity implements
		GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener,
		LocationListener {

	protected static final String		TAG										= "ToponimoActivity";
	private ListView					placeListView;
	private ArrayAdapter<Place>			placeArrayAdapter;
	private final List<Place>			placenameList							= new ArrayList<Place>();
	private static PlaceListActivity	mainActivity;

	private ToponimoApplication			application;

	private TextView					loadingTextView;
	private LinearLayout				loadingLinearLayout;
	private LinearLayout				emptyView;

	private GoogleMap					mMap;

	private LocationClient				mLocationClient;

	private Location					mCurrentLocation;

	private final static int			CONNECTION_FAILURE_RESOLUTION_REQUEST	= 9000;

	/** Called when the activity is first created. */
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.place_list);

		com.actionbarsherlock.app.ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayOptions(actionBar.DISPLAY_SHOW_HOME | actionBar.DISPLAY_SHOW_CUSTOM
				| actionBar.DISPLAY_SHOW_TITLE);
		actionBar.setTitle("Toponimo");
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setSubtitle("Nearby Places");
		actionBar.setHomeButtonEnabled(true);

		mLocationClient = new LocationClient(this, this, this);
		mLocationClient.connect();

		if (this.mMap == null) {
			this.mMap = ((SupportMapFragment) this.getSupportFragmentManager().findFragmentById(
					R.id.place_list_map_fragment)).getMap();
			mMap.getUiSettings().setMyLocationButtonEnabled(true);
			mMap.getUiSettings().setZoomControlsEnabled(false);
			mMap.setIndoorEnabled(true);
			mMap.setMyLocationEnabled(true);
		}

	}

	private void checkNetStatusandRefresh() {
		if (HttpUtils.isOnline(this.application.getApplicationContext())) {
			setLoadingView();
			refreshLocation();
		} else {
			Toast.makeText(mainActivity, "Oooops!!! We have a problem.", Toast.LENGTH_LONG).show();
			loadingTextView.setText("No connection to Toponimo :(");

		}
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		final Window window = this.getWindow();
		window.setFormat(PixelFormat.RGBA_8888);
		window.addFlags(WindowManager.LayoutParams.FLAG_DITHER);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	public void onConnected(Bundle connectionHint) {
		LocationRequest lr = LocationRequest.create();
		mLocationClient.requestLocationUpdates(lr, this);
	
	}

	private Response.Listener<String> successListener() {
		return new Response.Listener<String>() {

			public void onResponse(String response) {
				Gson gson = new Gson();

				Place place = gson.fromJson(response, Place.class);
				placenameList.clear();
				for (Results p : place.getResults()) {
					placenameList.add(place);
				}

				application.setPlaceResults(placenameList);
				placeArrayAdapter.notifyDataSetChanged();

			}

		};
	}

	private Response.ErrorListener errorListener() {
		return new Response.ErrorListener() {

			public void onErrorResponse(VolleyError error) {
				Log.d(TAG, "There was an error getting those place results. Hot diggity damn, Muthu-ucker!");
			}
		};
	}

	public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
		if (connectionResult.hasResolution()) {
			try {
				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
				/*
				 * Thrown if Google Play services cancelled the original
				 * PendingIntent
				 */
			} catch (IntentSender.SendIntentException e) {
				// Log the error
				e.printStackTrace();
			}
		} else {
			/*
			 * If no resolution is available, display a dialog to the user with
			 * the error.
			 */
			// (new Toast). (connectionResult.getErrorCode());
		}

	}

	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onResume() {

		// Get instance to application context
		application = (ToponimoApplication) this.getApplicationContext();
		mainActivity = this;
		placeArrayAdapter = new PlacesListAdapter(this, R.layout.simplerow, this.placenameList);

		emptyView = (LinearLayout) this.findViewById(R.id.empty_view);
		loadingLinearLayout = (LinearLayout) this.findViewById(R.id.loading_view);
		loadingTextView = (TextView) this.findViewById(R.id.loading_text_view);

		placeListView = (ListView) this.findViewById(R.id.wordListView);

		placeListView.setAdapter(this.placeArrayAdapter);

		placeListView.setEmptyView(this.loadingLinearLayout);

		placeListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> a, View v, int currentPlaceIndex, long id) {
				final TextView textView = (TextView) v.findViewById(R.id.label);
				final String placeName = textView.getText().toString();
				v.findViewById(R.id.label_words);
				PlaceListActivity.this.application.setCurrentPlaceIndex(currentPlaceIndex);
				final String placeAddress = PlaceListActivity.this.application
						.getPlaceResults(PlaceListActivity.this.application.getCurrentPlaceIndex()).getVicinity()
						.toString();
				final Intent intent = new Intent(PlaceListActivity.this, PlaceDetailsActivity.class);
				intent.putExtra("position", currentPlaceIndex);
				intent.putExtra("placeName", placeName);
				intent.putExtra("placeAddress", placeAddress);
				intent.putExtra("currentPosLat", Double.toString(mCurrentLocation.getLatitude()));
				intent.putExtra("currentPosLng", Double.toString(mCurrentLocation.getLongitude()));
				PlaceListActivity.this.startActivity(intent);
				intent.getExtras();
			}
		});

		checkNetStatusandRefresh();

		super.onResume();
	}

	@Override
	protected void onStart() {
		super.onStart();
		// Connect the client.
		mLocationClient.connect();
		// Start analytics tracking
		EasyTracker.getInstance().activityStart(this);

	}

	@Override
	protected void onStop() {
		mLocationClient.disconnect();
		super.onStop();
		EasyTracker.getInstance().activityStop(this);

	}

	private void refreshLocation() {

	}

	// display mesage to t
	public void setEmptyView() {
		this.placeListView.setEmptyView(this.emptyView);
	}

	public void setLoadingView() {
		this.loadingTextView.setText("Loading Places");
		this.placeListView.setEmptyView(this.loadingLinearLayout);
	}

	public void onLocationChanged(Location loc) {
		if (mLocationClient.isConnected()) {
			mCurrentLocation = mLocationClient.getLastLocation();
			if (mCurrentLocation != null) {
				LatLng pos = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
				//mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 10));
				
				mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 16), 2000, null );
				RequestQueue queue = ToponimoVolley.getRequestQueue();
				Request request = new StringRequest(Method.GET, ApiKeys.DOWNLOAD_URL + "lat="
						+ Double.toString(mCurrentLocation.getLatitude()) + "&" + "lng="
						+ Double.toString(mCurrentLocation.getLongitude()), successListener(), errorListener());
				request.setShouldCache(true);
				queue.add(request);
			}
		}


	}

}
