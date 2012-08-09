package org.toponimo.client.ui;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.toponimo.client.ApiKeys;
import org.toponimo.client.ToponimoApplication;
import org.toponimo.client.structures.placestructure.Place;
import org.toponimo.client.structures.placestructure.Results;
import org.toponimo.client.ui.adapters.PlacesListAdapter;
import org.toponimo.client.utils.http.HttpUtils;
import org.toponimo.client.utils.location.LocationGeocoder;
import org.toponimo.client.utils.location.LocationUpdateService;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import org.toponimo.client.R;

public class PlaceListActivity extends Activity implements LocationListener {

	protected static final String		TAG				= "ToponimoActivity";
	private ListView					placeListView;
	private ArrayAdapter<Place>			placeArrayAdapter;
	private final List<Place>			placenameList	= new ArrayList<Place>();
	private static PlaceListActivity	mainActivity;

	protected String					lat;
	protected String					lng;

	private ToponimoApplication			application;
	private ProgressBar					refreshBar;

	private ImageView					refreshButton;
	private ImageView					wordBankButton;
	private TextView					loadingView;
	private LinearLayout				emptyView;
	private LocationReceiver			receiver;
	private LocationManager				locationManager;
	private LocationListener			locationListener;
	
	private Location currentLocation;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Inflate view
		setContentView(R.layout.listitems);

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		// Get instance to application context
		application = (ToponimoApplication) getApplicationContext();
		mainActivity = this;
		placeArrayAdapter = new PlacesListAdapter(this, R.layout.simplerow,
				placenameList);

		refreshBar = (ProgressBar) findViewById(R.id.refresh_progress_bar);
		emptyView = (LinearLayout) findViewById(R.id.empty_view);
		loadingView = (TextView) findViewById(R.id.loading_view);

		placeListView = (ListView) findViewById(R.id.wordListView);

		placeListView.setAdapter(placeArrayAdapter);

		placeListView.setEmptyView(loadingView);

		placeListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> a, View v,
					int currentPlaceIndex, long id) {
				TextView textView = (TextView) v.findViewById(R.id.label);
				String placeName = textView.getText().toString();
				TextView types = (TextView) v.findViewById(R.id.label_words);
				application.setCurrentPlaceIndex(currentPlaceIndex);
				String placeAddress = application
						.getPlaceResults(application.getCurrentPlaceIndex())
						.getVicinity().toString();
				Intent intent = new Intent(PlaceListActivity.this,
						PlaceDetailsActivity.class);
				intent.putExtra("position", currentPlaceIndex);
				intent.putExtra("placeName", placeName);
				intent.putExtra("placeAddress", placeAddress);
				intent.putExtra("currentPosLat", lat);
				intent.putExtra("currentPosLng", lng);
				startActivity(intent);
				intent.getExtras();
			}
		});

		refreshButton = (ImageView) findViewById(R.id.refresh_button);
		refreshButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				checkNetStatusandRefresh();
			}
		});
		registerLocationReceiver();
		checkNetStatusandRefresh();

		wordBankButton = (ImageView) findViewById(R.id.word_bank_button);
		wordBankButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Intent i = new Intent(PlaceListActivity.this,
						WordBankActivity.class);
				startActivity(i);

			}
		});
	}

	public void setEmptyView() {
		placeListView.setEmptyView(emptyView);
	}

	public void setLoadingView() {
		loadingView.setText("Loading Places");
		placeListView.setEmptyView(loadingView);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		registerLocationReceiver();
	}

	private void registerLocationReceiver() {
		IntentFilter filter = new IntentFilter(LocationReceiver.ACTION);
		filter.addCategory(Intent.CATEGORY_DEFAULT);
		receiver = new LocationReceiver();
		registerReceiver(receiver, filter);
	}

	private void checkNetStatusandRefresh() {
		if (HttpUtils.isOnline(application.getApplicationContext())) {
			setLoadingView();
			refreshLocation();
		} else {
			Toast.makeText(mainActivity, "No connection to Toponimo server",
					Toast.LENGTH_LONG).show();
			loadingView.setText("No connection to Toponimo");

		}
	}

	private void refreshLocation() {
		Intent locationUpdateService = new Intent(PlaceListActivity.this,
				LocationUpdateService.class);
		startService(locationUpdateService);
	}
	

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		Window window = getWindow();
		window.setFormat(PixelFormat.RGBA_8888);
		window.addFlags(WindowManager.LayoutParams.FLAG_DITHER);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	@SuppressWarnings("rawtypes")
	private class GetPlaceList extends AsyncTask {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mainActivity.refreshButton.setVisibility(View.GONE);
			mainActivity.refreshBar.setVisibility(View.VISIBLE);
			if (mainActivity.loadingView.getVisibility() != View.GONE) {
				mainActivity.loadingView.setText("Loading Places");
			}
		}

		@Override
		protected Object doInBackground(Object... arg0) {
			String urlString = ApiKeys.DOWNLOAD_URL + "lat=" + lat + "&"
					+ "lng=" + lng;
			Log.d(TAG, urlString);
			Place place = null;
			try {
				Gson gson = new Gson();
				String jsonData = HttpUtils.getJSONData(ToponimoApplication
						.getApp().getHttpClient(), urlString);
				Log.d(TAG, jsonData);

				place = gson.fromJson(jsonData, Place.class);
				if (place.getResults().isEmpty()) {

				}
				placenameList.clear();
				for (Results p : place.getResults()) {
					placenameList.add(place);
				}
				mainActivity.application.setPlaceResults(placenameList);

			} catch (NullPointerException e) {
				e.printStackTrace();
				AlertDialog.Builder builder = new AlertDialog.Builder(
						getBaseContext());
				builder.setMessage(
						"There was a problem downloading location data. Try again?")
						.setCancelable(false)
						.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										mainActivity.refreshLocation();
									}
								})
						.setNegativeButton("No",
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {
										mainActivity.finish();

									}
								});

			} catch (Exception e) {
				e.printStackTrace();
			}
			return place;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			mainActivity.placeArrayAdapter.notifyDataSetChanged();
			mainActivity.refreshButton.setVisibility(View.VISIBLE);
			mainActivity.refreshBar.setVisibility(View.GONE);

		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterLocationReceiver();
	}

	@Override
	protected void onResume() {
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 0, 0, this);
		super.onResume();
	}

	private void unregisterLocationReceiver() {
		if (receiver != null) {
			unregisterReceiver(receiver);
			Log.w(TAG, "Location Receiver has been unregistered");
			receiver = null;
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		unregisterLocationReceiver();
		stopService(new Intent(PlaceListActivity.this,
				LocationUpdateService.class));
	}

	private class LocationReceiver extends BroadcastReceiver {
		public static final String	ACTION	= "LOCATION";

		@Override
		public void onReceive(Context context, Intent intent) {
			lat = intent.getStringExtra("lat");
			lng = intent.getStringExtra("lng");
			double dLat = Double.parseDouble(lat);
			double dLng = Double.parseDouble(lng);
			LocationGeocoder.getLocationName(dLat, dLng,
					getApplicationContext(), new ReverseGeocoderHandler());
			(new GetPlaceList()).execute((Object) null);
		}

		private class ReverseGeocoderHandler extends Handler {
			@Override
			public void handleMessage(Message message) {
				String result;
				switch (message.what) {
				case 1:
					Bundle bundle = message.getData();
					result = bundle.getString("address");
					break;
				default:
					result = null;
				}
				TextView locationDisplayBar = (TextView) findViewById(R.id.places_info_text);
				locationDisplayBar.setText("Near " + result);
			}
		}

	}

	public void onLocationChanged(Location arg0) {

	}

	public void onProviderDisabled(String arg0) {

	}

	public void onProviderEnabled(String arg0) {

	}

	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {

	}
	
	

}