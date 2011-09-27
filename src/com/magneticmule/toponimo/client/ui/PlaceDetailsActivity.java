package com.magneticmule.toponimo.client.ui;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import com.magneticmule.toponimo.client.BitmapDrawUtils;
import com.magneticmule.toponimo.client.R;
import com.magneticmule.toponimo.client.ToponimoApplication;
import com.magneticmule.toponimo.client.ui.adapters.WordListAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.magneticmule.toponimo.client.utils.http.HttpDataUtils;
import com.magneticmule.toponimo.client.utils.location.DistanceCalculator;

public class PlaceDetailsActivity extends Activity {

	protected static final String	TAG						= "PlaceDetailsActivity";
	private static int						TAKE_PICTURE	= 1;
	private int										position;
	private String								placeName;
	private String								placeAddress;
	private String								extraData			= "";
	private String								currentPosLat;
	private String								currentPosLng;
	private Double								targetPosLat;
	private Double								targetPosLng;
	private ArrayList<String>			words					= new ArrayList<String>();
	private ListView							wordListView;
	private ArrayAdapter<String>	wordListArrayAdapter;
	private ToponimoApplication		application;
	private ImageView							image;
	private LinearLayout					headerViewMap;
	private LinearLayout					headerViewButton;
	private Button								addWordButton;

	// distance between two locations
	private Double								distance;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == TAKE_PICTURE) {
			Uri imageUri = null;

			if (data != null) {
				if (data.hasExtra("data")) {
					Bitmap thumbnail = data.getParcelableExtra("data");
				}
			}
		} else {
			if (data != null) {
				extraData = data.getStringExtra("words");
				// TextView placeWords = (TextView)
				// findViewById(R.id.place_details_place_words);
				// placeWords.setText(extraData);
				words.add(extraData + " *");
				wordListArrayAdapter.notifyDataSetChanged();
			}

		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.homegrid);
		this.application = (ToponimoApplication) this.getApplication();
		Intent sender = getIntent();
		this.position = application.getCurrentPlaceIndex();
		this.placeName = application.getPlaceResults().get(position).getResults().get(position).getName();
		this.placeAddress = application.getPlaceResults().get(position).getResults().get(position).getVicinity();
		this.currentPosLat = sender.getExtras().getString("currentPosLat");
		this.currentPosLng = sender.getExtras().getString("currentPosLng");

		this.targetPosLat = application.getPlaceResults().get(position).getResults().get(position).getLocation().getLat();
		this.targetPosLng = application.getPlaceResults().get(position).getResults().get(position).getLocation().getLng();

		// words.addAll(application.getPlaceResults().get(position).getResults().get(position).getWords());
		// Log.i("Words", words.get(0));

		wordListView = (ListView) findViewById(R.id.place_details_word_listview);
		application.getPlaceResults().get(position).getResults().get(position).getWords().add("Place");
		application.getPlaceResults().get(position).getResults().get(position).getWords().add("Location");

		wordListArrayAdapter = new WordListAdapter(this, R.layout.word_row, application.getPlaceResults().get(position).getResults()
				.get(position).getWords());

		// inflate the first listview header view then find and assign the
		// mapview image
		LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		headerViewMap = (LinearLayout) inflater.inflate(R.layout.map_image, null);
		image = (ImageView) headerViewMap.findViewById(R.id.place_details_static_map_view);

		// inflate the second listview header and attach the add word button
		headerViewButton = (LinearLayout) inflater.inflate(R.layout.add_word_button, null);
		addWordButton = (Button) headerViewButton.findViewById(R.id.place_details_add_word_button);

		
			try {
				wordListView.addHeaderView(headerViewMap);
				wordListView.addHeaderView(headerViewButton);
				wordListView.setAdapter(wordListArrayAdapter);
				if (!(wordListView.getCount() < 1)){
					TextView totalWordTextView = (TextView) findViewById(R.id.places_info_text);
					totalWordTextView.setText((wordListView.getCount()-1) + " words at this location");
				}

			} catch (Exception e) {
				Log.i("Exception", "lv.setadapter");
			}
		

		wordListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
				TextView wordView = (TextView) v.findViewById(R.id.word_row_word_view);
				Intent intent = new Intent(PlaceDetailsActivity.this, WordDetailsActivity.class);
				String word = wordView.getText().toString();
				intent.putExtra("word", word);
				startActivity(intent);
				
			}
		});

		// Set header textviews with appropriate imformation
		TextView placeTitleView = (TextView) findViewById(R.id.place_details_place_name);
		placeTitleView.setText(placeName);
		TextView placeAddressView = (TextView) findViewById(R.id.place_details_place_address);
		placeAddressView.setText(placeAddress);
		TextView placeDistanceView = (TextView) findViewById(R.id.place_details_place_distance);

		double lat = Double.parseDouble(currentPosLat);
		double lng = Double.parseDouble(currentPosLng);
		double distance = DistanceCalculator.haverSine(lat, lng, targetPosLat, targetPosLng);

		String distanceIndicator = " Kilometers from here";
		Double d = (double) Math.round(distance * 1000);
		if (distance < 0.5) {
			distanceIndicator = " Meters from here";
		} else {
			distanceIndicator = " Kilometers from here";
			d /= 1000;
			}		
		int roundedDistance = d.intValue();

		placeDistanceView.setText(Integer.toString(roundedDistance) + distanceIndicator);
		// final Button addWordButton = (Button)
		// findViewById(R.id.place_details_add_word);c

		addWordButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(PlaceDetailsActivity.this, AddWordActivity.class);
				intent.putExtra("position", position);
				intent.putExtra("name", placeName);
				final int result = 1;
				startActivityForResult(intent, result);
			}
		});

		(new DownloadMapImage()).execute((Object) null);
	}

	@Override
	protected void onResume() {
		wordListArrayAdapter.notifyDataSetChanged();
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

	private class DownloadMapImage extends AsyncTask {
		Bitmap	bitmap;

		@Override
		protected Bitmap doInBackground(Object... params) {
		  File file = HttpDataUtils.getWebImage("http://maps.google.com/maps/api/staticmap?" + "zoom=16&size=400x180&markers=size:big|color:green|"
					+ targetPosLat.toString() + "," + targetPosLng.toString() + "&sensor=true&format=png8", "place.png");

			FileInputStream is;
			BufferedInputStream bis;
			try {
				String savePath = Environment.getExternalStorageDirectory().toString();
				is = new FileInputStream(file);
				bis = new BufferedInputStream(is);
				bitmap = BitmapFactory.decodeStream(bis);
				if (is != null) {
					is.close();
				}
				if (bis != null) {
					bis.close();
				}
			} catch (FileNotFoundException e) {
				Log.d("PlaceDetailsActivity.class", "FileNotFoundException, Stacktrace follows:");
				e.printStackTrace();
			} catch (IOException e) {
				Log.d("PlaceDetailsActivity.class", "IOException, Stacktrace follows:");
				e.printStackTrace();
			}
			return bitmap;
		}

		protected void onPostExecute(Object result) {
			image.setImageBitmap(BitmapDrawUtils.getRoundedCornerBitmap(bitmap));
		}
	}

}