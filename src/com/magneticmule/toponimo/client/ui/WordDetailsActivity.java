package com.magneticmule.toponimo.client.ui;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URLEncoder;

import com.magneticmule.toponimo.client.utils.http.HttpDataUtils;
import com.magneticmule.toponimo.client.Constants;
import com.magneticmule.toponimo.client.R;

import com.magneticmule.toponimo.client.ToponimoApplication;

public class WordDetailsActivity extends Activity implements TextToSpeech.OnInitListener {

	private static int									TAKE_PICTURE	= 1;
	private ToponimoApplication					application;
	private static WordDetailsActivity	mainActivity;
	private TextToSpeech								tts;
	private String											word;
	private String											definition;
	private static final int						DATA_CHECKSUM	= 28031974;
	protected TextView									definitionTextView;

	private Uri													imageOutputURI;

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == TAKE_PICTURE) {
			Uri imageUri = null;
			if (data != null) {
				if (data.hasExtra("data")) {
					Bitmap thumbnail = data.getParcelableExtra("data");
				}
			}
		} else if (requestCode == DATA_CHECKSUM) {
			if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
				// if tts data exists, create a new tts instance
				tts = new TextToSpeech(this, this);
			} else {
				// else, install tts data
				Intent installIntent = new Intent();
				installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
				startActivity(installIntent);
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.word_details);
		mainActivity = this;
		// check for tts data
		Intent checkIntent = new Intent();
		checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(checkIntent, DATA_CHECKSUM);
		application = (ToponimoApplication) this.getApplication();

		Intent sender = getIntent();
		word = sender.getExtras().getString("word");
		word = word.trim();
		TextView wordTextView = (TextView) findViewById(R.id.word_details_word_view);

		wordTextView.setText(word);

		Button speakButton = (Button) findViewById(R.id.word_details_speak_button);
		speakButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				TextView wordTextView = (TextView) findViewById(R.id.word_details_word_view);
				tts.speak(wordTextView.getText().toString() + ".", TextToSpeech.QUEUE_FLUSH, null);
			}
		});

		ImageButton addPictureButton = (ImageButton) findViewById(R.id.word_details_add_picture);
		addPictureButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				File file = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
				imageOutputURI = Uri.fromFile(file);
				i.putExtra(MediaStore.EXTRA_OUTPUT, imageOutputURI);
				startActivityForResult(i, TAKE_PICTURE);
			}
		});

		Button addToWordbank = (Button) findViewById(R.id.word_details_add_to_word_bank_button);
		addToWordbank.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				int currentPlaceIndex = application.getCurrentPlaceIndex();
				String currentLocation = application.getPlaceResults().get(currentPlaceIndex).getResults().get(currentPlaceIndex).getName();
				Double currentLat = application.getPlaceResults().get(currentPlaceIndex).getResults().get(currentPlaceIndex).getLocation().getLat();
				Double currentLng = application.getPlaceResults().get(currentPlaceIndex).getResults().get(currentPlaceIndex).getLocation().getLng();
				String currentLocationID = application.getPlaceResults().get(currentPlaceIndex).getResults().get(currentPlaceIndex).getId();

				addWordToBank(word, definition, currentLocation, currentLat, currentLng, currentLocationID);
				Toast t = Toast.makeText(WordDetailsActivity.this, "Added '" + word + "' to Word Store", Toast.LENGTH_SHORT);
				t.show();
			}
		});

		Gallery gallery = (Gallery) findViewById(R.id.word_details_gallery);
		String[] picText = new String[] { "This", "is", "Just", "a", "Test" };
		ArrayAdapter<String> picAA = new ArrayAdapter<String>(this, android.R.layout.simple_gallery_item, picText);
		gallery.setAdapter(picAA);

		definitionTextView = (TextView) findViewById(R.id.word_details_definition);
		(new DownloadDefinition()).execute((Object) null);

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

	public void onInit(int status) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onDestroy() {
		if (tts != null) {
			tts.stop();
			tts.shutdown();
		}
		super.onDestroy();

	}

	private void addWordToBank(String word, String definition, String locationName, Double lat, Double lng, String locationID) {
		Uri uri = Constants.WORDS_URI;
		ContentValues cv = new ContentValues();
		cv.put(Constants.KEY_WORD, word);
		cv.put(Constants.KEY_WORD_DEFINITION, definition);
		cv.put(Constants.KEY_WORD_LOCATION, locationName);
		cv.put(Constants.KEY_WORD_LOCATION_LAT, 0.12223);
		cv.put(Constants.KEY_WORD_LOCATION_LNG, 1.13131313);
		cv.put(Constants.KEY_WORD_LOCATION_ID, "3478397984758439");
		Uri newWord = getContentResolver().insert(uri, cv);
	}

	private class DownloadDefinition extends AsyncTask {
		String	definition	= "";

		@Override
		protected Object doInBackground(Object... params) {

			try {
				String completeUrl = "http://www.api.toponimo.org/definition.php?word=" + URLEncoder.encode(word);
				Reader reader = new InputStreamReader(HttpDataUtils.getJSONData(completeUrl));
				BufferedReader in = new BufferedReader(reader);
				String current;
				while ((current = in.readLine()) != null) {
					definition += current;
				}
				Log.i("Definition", definition.toString());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
			}
			Log.i("Definition", definition);

			return null;

		}

		@Override
		protected void onPostExecute(Object result) {
			mainActivity.definitionTextView.setText(definition);
			mainActivity.definition = definition;
			super.onPostExecute(result);
		}

	}

}