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

import com.magneticmule.toponimo.client.ui.adapters.WordGalleryAdapter;
import com.magneticmule.toponimo.client.utils.http.HttpDataUtils;
import com.magneticmule.toponimo.client.Constants;
import com.magneticmule.toponimo.client.R;

import com.magneticmule.toponimo.client.*;

public class WordDetailsActivity extends Activity implements TextToSpeech.OnInitListener {

	private static final int						TAKE_PICTURE	= 1;
	private static final int						DATA_CHECKSUM	= 28031974;
	private ToponimoApplication					application;
	private static WordDetailsActivity	mainActivity;
	private TextToSpeech								tts;
	private String											word;
	private String											definition;

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
		application = (ToponimoApplication) this.getApplication();
		mainActivity = this;
		ttsCheck();
		Intent sender = getIntent();
		word = sender.getExtras().getString("word");
		word = word.trim();
		TextView wordTextView = (TextView) findViewById(R.id.word_details_word_view);
		wordTextView.setText(word);
		createSpeakButton();
		createAddPictureButton();
		createAddToWordBankButton();
		createGallery();
		definitionTextView = (TextView) findViewById(R.id.word_details_definition);
		(new DownloadDefinition()).execute((Object) null);

	}

	/**
	 * Checks if the tts data has been installed. If not, a new activity is
	 * launched to install the missing data.
	 */
	private void ttsCheck() {
		// check for tts data
		Intent checkIntent = new Intent();
		checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(checkIntent, DATA_CHECKSUM);
	}

	/**
	 * Assigns
	 */
	private void createGallery() {
		Gallery gallery = (Gallery) findViewById(R.id.word_details_gallery);
		gallery.setAdapter(new WordGalleryAdapter(this));
		gallery.setSelection(1);
	}

	/**
	 * Attaches the button from the xml source then assigns a new onClickListener
	 * to the button. If pressed, the current place details are retrieved from the
	 * application cache and added to the local database along with the current
	 * word.
	 */
	private void createAddToWordBankButton() {
		Button addToWordbank = (Button) findViewById(R.id.word_details_add_to_word_bank_button);
		addToWordbank.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				int currentPlaceIndex = application.getCurrentPlaceIndex();
				String currentLocation = application.getPlaceResults().get(currentPlaceIndex).getResults().get(currentPlaceIndex).getName();
				Double currentLat = application.getPlaceResults().get(currentPlaceIndex).getResults().get(currentPlaceIndex).getLocation().getLat();
				Double currentLng = application.getPlaceResults().get(currentPlaceIndex).getResults().get(currentPlaceIndex).getLocation().getLng();
				String currentLocationID = application.getPlaceResults().get(currentPlaceIndex).getResults().get(currentPlaceIndex).getId();
				Log.i("WORD", word.toString());
				Log.i("definition", definition.toString());
				Log.i("currentPlaceIndex", Integer.toString(currentPlaceIndex));
				Log.i("currentLocation", currentLocation.toString());
				Log.i("currentLat", currentLat.toString());
				Log.i("CurrentLng", currentLng.toString());
				Log.i("currentLocationID", currentLocationID.toString());

				addWordToBank(word, definition, currentLocation, currentLat, currentLng, currentLocationID);
				Toast t = Toast.makeText(WordDetailsActivity.this, "Added '" + word + "' to Word Store", Toast.LENGTH_SHORT);
				t.show();
			}
		});
	}

	/**
	 * Attaches the button from the xml source then assigns a new onClickListener
	 * to the button. If pressed, a new intent is fired to take a picture from the
	 * in built camera. The resulting image is stored in the applications solid
	 * state cache.
	 */
	private void createAddPictureButton() {
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
	}

	/**
	 * Attaches the button from the xml source then assigns a new onClickListener
	 * to the button. If pressed, the current word is spoken via tts.
	 */
	private void createSpeakButton() {
		Button speakButton = (Button) findViewById(R.id.word_details_speak_button);
		speakButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				TextView wordTextView = (TextView) findViewById(R.id.word_details_word_view);
				tts.speak(wordTextView.getText().toString() + ".", TextToSpeech.QUEUE_FLUSH, null);
			}
		});
	}

	/**
	 * Enables full color support.
	 */
	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		Window window = getWindow();
		window.setFormat(PixelFormat.RGBA_8888);
		window.addFlags(WindowManager.LayoutParams.FLAG_DITHER);
	}

	/**
	 * prevents the view from switching from portrait to landscape when the phone
	 * is rotated.
	 */

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	/**
	 * Kills the current tts instance when the intent is destroyed
	 */
	@Override
	protected void onDestroy() {
		if (tts != null) {
			tts.stop();
			tts.shutdown();
		}
		super.onDestroy();
	}

	/**
	 * 
	 * @param word
	 * @param definition
	 * @param locationName
	 * @param lat
	 * @param lng
	 * @param locationID
	 */
	private void addWordToBank(String word, String definition, String locationName, Double lat, Double lng, String locationID) {
		Uri uri = Constants.WORDS_URI;
		ContentValues cv = new ContentValues();
		cv.put(Constants.KEY_WORD, word);
		cv.put(Constants.KEY_WORD_DEFINITION, definition);
		cv.put(Constants.KEY_WORD_LOCATION, locationName);
		cv.put(Constants.KEY_WORD_LOCATION_LAT, lat);
		cv.put(Constants.KEY_WORD_LOCATION_LNG, lng);
		cv.put(Constants.KEY_WORD_LOCATION_ID, locationID);
		Uri newWord = getContentResolver().insert(uri, cv);
	}

	private class DownloadDefinition extends AsyncTask {

		String	definition	= "";

		@Override
		protected Object doInBackground(Object... params) {

			try {
				String completeUrl = Secret.DEFINITION_URL + URLEncoder.encode(word);
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

	public void onInit(int status) {
		// TODO Auto-generated method stub

	}

}