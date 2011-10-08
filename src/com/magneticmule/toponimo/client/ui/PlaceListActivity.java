package com.magneticmule.toponimo.client.ui;

import android.app.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

import com.google.gson.Gson;

import com.magneticmule.toponimo.client.R;
import com.magneticmule.toponimo.client.ToponimoApplication;
import com.magneticmule.toponimo.client.placestructure.Place;
import com.magneticmule.toponimo.client.placestructure.Results;
import com.magneticmule.toponimo.client.ui.adapters.PlacesListAdapter;
import com.magneticmule.toponimo.client.utils.http.HttpDataUtils;
import com.magneticmule.toponimo.client.utils.location.LocationGeocoder;
import com.magneticmule.toponimo.client.utils.location.LocationUpdateService;


public class PlaceListActivity extends Activity {

	protected static final String			TAG	= "ToponimoActivity";
	private ListView									placeListView;
	private ArrayAdapter<Place>				placeArrayAdapter;
	private ArrayList<Place>					placenameList;
	private static PlaceListActivity	mainActivity;

	protected String									lat;
	protected String									lng;

	private ToponimoApplication				application;
	private ProgressBar								refreshBar;

	private ImageView									refreshButton;
	private ImageView									wordBankButton;
	private TextView									emptyView;

	private LocationReceiver					receiver;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Inflate view
		setContentView(R.layout.listitems);

		// Get instance to application context
		application = (ToponimoApplication) getApplicationContext();

		placeListView = (ListView) findViewById(R.id.wordListView);
		placenameList = new ArrayList<Place>();
		mainActivity = this;

		placeArrayAdapter = new PlacesListAdapter(this, R.layout.simplerow, placenameList);

		refreshBar = (ProgressBar) findViewById(R.id.refresh_progress_bar);
		emptyView = (TextView) findViewById(R.id.emptyView);
		placeListView.setEmptyView(emptyView);
		placeListView.setAdapter(placeArrayAdapter);

		placeListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> a, View v, int position, long id) {
				TextView textView = (TextView) v.findViewById(R.id.label);
				String placeName = textView.getText().toString();
				TextView types = (TextView) v.findViewById(R.id.label_words);
				String placeAddress = application.getPlaceResults().get(position).getResults().get(position).getVicinity().toString();
				application.setCurrentPlace(application.getPlaceResults().get(position));
				application.setCurrentPlaceIndex(position);
				Intent intent = new Intent(PlaceListActivity.this, PlaceDetailsActivity.class);
				intent.putExtra("position", position);
				intent.putExtra("placeName", placeName);
				intent.putExtra("placeAddress", placeAddress);
				intent.putExtra("currentPosLat", lat);
				intent.putExtra("currentPosLng", lng);
				startActivity(intent);
				intent.getExtras();
			}
		});

		// final Button mapsButton = (Button) findViewById(R.id.map_button);
		// mapsButton.setOnClickListener(new View.OnClickListener() {
		//
		// public void onClick(View arg0) {
		// Intent i = new Intent(PlaceListActivity.this,
		// MapsViewActivity.class);
		// startActivity(i);
		//
		// }
		// });
		//
		// final Button wordBankButton = (Button)
		// findViewById(R.id.words_button);
		// wordBankButton.setOnClickListener(new View.OnClickListener() {
		//
		// public void onClick(View v) {
		// Intent i = new Intent(PlaceListActivity.this,
		// MyWordBankActivity.class);
		// startActivity(i);
		//
		// }
		// });

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
				Intent i = new Intent(PlaceListActivity.this, WordBankActivity.class);
				startActivity(i);

			}
		});
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
		if (HttpDataUtils.isOnline(application.getApplicationContext())) {
			refreshLocation();
		} else {
			Toast.makeText(mainActivity, "No connection to Toponimo server", Toast.LENGTH_LONG).show();

		}
	}

	private void refreshLocation() {
		Intent locationUpdateService = new Intent(PlaceListActivity.this, LocationUpdateService.class);
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
	private class GetPlacesandParse extends AsyncTask {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mainActivity.refreshButton.setVisibility(View.GONE);
			mainActivity.refreshBar.setVisibility(View.VISIBLE);
			if (mainActivity.emptyView.getVisibility() != View.GONE) {
				mainActivity.emptyView.setText("Loading Places");
			}
		}

		@Override
		protected Object doInBackground(Object... arg0) {
			String urlString = "http://api.toponimo.org?" + "lat=" + lat + "&" + "lng=" + lng;
			Log.i("URL", urlString);

			Place place = null;
			try {
				Gson gson = new Gson();
				Reader r = new InputStreamReader(HttpDataUtils.getJSONData(urlString));
				place = gson.fromJson(r, Place.class);
				Log.i(TAG, "Got the Json");
				placenameList.clear();
				for (Results p : place.getResults()) {
					placenameList.add(place);
				}
				mainActivity.application.setPlaceResults(placenameList);

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
			}

			return place;

		}

		@Override
		protected void onPostExecute(Object result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			mainActivity.placeArrayAdapter.notifyDataSetChanged();
			mainActivity.refreshButton.setVisibility(View.VISIBLE);
			mainActivity.refreshBar.setVisibility(View.GONE);

		}

		/*
		 * (non-Javadoc)
		 * @see android.os.AsyncTask#onProgressUpdate(Progress[])
		 */
		@Override
		protected void onProgressUpdate(Object... values) {
			super.onProgressUpdate(values);
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterLocationReceiver();
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
		stopService(new Intent(PlaceListActivity.this, LocationUpdateService.class));
	}

	public class LocationReceiver extends BroadcastReceiver {
		public static final String	ACTION	= "LOCATION";

		@Override
		public void onReceive(Context context, Intent intent) {
			lat = intent.getStringExtra("lat");
			lng = intent.getStringExtra("lng");
			Log.i(TAG, lat);
			Log.i(TAG, lng);
			double dLat = Double.parseDouble(lat);
			double dLng = Double.parseDouble(lng);
			LocationGeocoder.getLocationName(dLat, dLng, getApplicationContext(), new ReverseGeocoderHandler());
			(new GetPlacesandParse()).execute((Object) null);
		}

		public class ReverseGeocoderHandler extends Handler {
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

}