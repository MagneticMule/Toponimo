package org.toponimo.client.ui;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.toponimo.client.ApiKeys;
import org.toponimo.client.Constants;
import org.toponimo.client.RestService;
import org.toponimo.client.ToponimoApplication;
import org.toponimo.client.models.Place;
import org.toponimo.client.models.Userword;
import org.toponimo.client.models.Word;
import org.toponimo.client.models.WordDefinition;
import org.toponimo.client.ui.adapters.WordGalleryAdapter;
import org.toponimo.client.utils.BitmapUtils;
import org.toponimo.client.utils.http.HttpUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.google.gson.Gson;
import org.toponimo.client.R;

public class WordDetailsActivity extends SherlockActivity implements TextToSpeech.OnInitListener {

	protected static final String		TAG							= "WordDetailsActivity";
	private static final int			TAKE_PICTURE				= 1;
	private static final int			DATA_CHECKSUM				= 28031974;
	private static final int			EDIT_DEFINITION_DIALOG_ID	= 1974;
	private ToponimoApplication			application;
	private static WordDetailsActivity	mainActivity;
	private TextToSpeech				tts 						= null;
	private String						word						= null;
	private String						definition					= null;
	private String						gloss						= null;
	private String						lexType						= null;
	private final String				synsetNo					= null;

	protected TextView					definitionTextView;
	protected TextView					glossTextView;
	protected TextView					lexTypeTextView;
	private Uri							imageOutputUri				= null;

	private Animation					fadein;
	private final ArrayList<String>		imageList					= new ArrayList<String>();

	private Gallery						gallery;
	private WordGalleryAdapter			galleryAdapter;

	// Editdefinition dialog controls
	EditText							editDefinitionText;
	

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_CANCELED) {
			return;
		}

		if (requestCode == TAKE_PICTURE) {
			Log.d(TAG, "TAKE PICTURE");
			if (imageOutputUri != null) {
				Log.v(TAG, "Data is not NULL");
				Log.v("BitmapString: ", imageOutputUri.toString());
				String thumbnailPath = BitmapUtils.createThumbnail(imageOutputUri, 240);
				Log.d(TAG + ": ThumbnailPath=", thumbnailPath);

				imageList.add(thumbnailPath);
				galleryAdapter.notifyDataSetChanged();

				int currentPlaceIndex = application.getCurrentPlaceIndex();
				String currentPlaceId = application.getPlaceResults(currentPlaceIndex).getId();
				
				
				List<NameValuePair> postParameters = new ArrayList<NameValuePair>(1);
				// Construct new bundle to pass to the restservice
				Bundle bundle = new Bundle();
				//bundle.putParcelableArrayList("postParameters", postParameters);
				bundle.putString(Constants.UPLOAD_IMAGE_PLACE_ID, currentPlaceId);
				bundle.putString(Constants.UPLOAD_IMAGE_WORD_NAME, word);
				bundle.putString(Constants.UPLOAD_IMAGE_WORD_NUMBER, "23");
				bundle.putString(Constants.UPLOAD_IMAGE_SYNSET_NUMBER, "7");
				bundle.putString(Constants.UPLOAD_IMAGE_USER_ID, application.getUser().getId());
				bundle.putString(Constants.UPLOAD_IMAGE_PATH, imageOutputUri.toString());
				bundle.putSerializable(Constants.UPLOAD_IMAGE_PATH, (Serializable) imageOutputUri);

				// Start new REST service to upload image
				Intent restService = new Intent(WordDetailsActivity.this, RestService.class);
				
				//restService.setData(imageOutputURI);
				restService.putExtra(Constants.REST_PARAMS, bundle);
				startService(restService);
				// (new ImageUploader()).execute((Object) null);
			}

		} else if (requestCode == DATA_CHECKSUM) {
			
			if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
				// if TTS data exists, create a new TTS instance
				tts = new TextToSpeech(this, this);
			} else {
				// TTS data is not installed. Ask user if they would like to install it.
				DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case DialogInterface.BUTTON_POSITIVE:

							// install TTS data
							Intent installIntent = new Intent();
							installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
							startActivity(installIntent);
							break;
						}
					}
				};

				// Display alert dialog asking to install TTS data
				AlertDialog.Builder builder = new AlertDialog.Builder(WordDetailsActivity.this);
				builder.setMessage("Google Text to Speech data needs to be installed "
						+ "before you can hear pronounciations of words");
				builder.setPositiveButton("Install", clickListener);
				builder.setNegativeButton("Skip", clickListener);
				builder.show();

			}
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.word_details);
		application = (ToponimoApplication) this.getApplication();
		mainActivity = this;

		LinearLayout galleryContainer = (LinearLayout) findViewById(R.id.word_details_gallery_container);

		galleryAdapter = new WordGalleryAdapter(this, imageList);

		fadein = AnimationUtils.loadAnimation(this, R.anim.fadein);

		definitionTextView = (TextView) findViewById(R.id.word_details_definition);
		glossTextView = (TextView) findViewById(R.id.word_details_gloss);
		lexTypeTextView = (TextView) findViewById(R.id.label_type);

		ttsCheck();
		Intent sender = getIntent();
		word = sender.getExtras().getString("word");
		word = word.trim();
		TextView wordTextView = (TextView) findViewById(R.id.word_details_word_view);
		wordTextView.setText(word);
		(new DownloadDefinition()).execute((Object) null);

		createSpeakButton();
		createAddPictureButton();
		createGallery();
		createAddToWordBankButton();

		galleryContainer.startAnimation(fadein);

	}

	private void createGallery() {
		gallery = (Gallery) findViewById(R.id.word_details_gallery);
		gallery.setAdapter(galleryAdapter);
		gallery.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
				Bundle bundle = new Bundle();
				bundle.putStringArrayList("images", imageList);
				bundle.putInt("currentIndex", position);
				Intent i = new Intent(WordDetailsActivity.this, ImageViewActivity.class);
				i.putExtra("imageArrayList", bundle);
				startActivity(i);

			}

		});
	}

	/**
	 * Checks if the tts data has been installed. If not, a new activity is
	 * launched to install the tts data.
	 */
	private void ttsCheck() {
		// check for tts data
		Intent checkIntent = new Intent();
		checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(checkIntent, DATA_CHECKSUM);
	}

	/**
	 * Attaches the button from the xml source then assigns a new
	 * onClickListener to the button. If pressed, the current place details are
	 * retrieved from the application cache and added to the local database
	 * along with the current word.
	 */
	private void createAddToWordBankButton() {
		Button addToWordbank = (Button) findViewById(R.id.word_details_add_to_word_bank_button);
		addToWordbank.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				String currentLocation = application.getPlaceResults(application.getCurrentPlaceIndex()).getName();
				double currentLat = application.getPlaceResults(application.getCurrentPlaceIndex()).getLocation()
						.getLat();
				double currentLng = application.getPlaceResults(application.getCurrentPlaceIndex()).getLocation()
						.getLng();
				String currentLocationID = application.getPlaceResults(application.getCurrentPlaceIndex()).getId();

				addWordToBank(word, definition, gloss, currentLocation, lexType, currentLat, currentLng,
						currentLocationID);
				Toast t = Toast.makeText(WordDetailsActivity.this, "Added '" + word + "' to Timeline",
						Toast.LENGTH_SHORT);
				t.show();
				Intent i = new Intent(WordDetailsActivity.this, JournalActivity.class);
				startActivity(i);
			}
		});
	}

	/**
	 * Attaches the button from the xml source then assigns a new
	 * onClickListener to the button. If pressed, a new intent is fired to take
	 * a picture from the in built camera. The resulting image is stored in the
	 * applications solid state cache.
	 */
	private void createAddPictureButton() {
		Button addPictureButton = (Button) findViewById(R.id.word_details_add_picture);
		addPictureButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				UUID uuid = UUID.randomUUID();

				File path = new File(Environment.getExternalStorageDirectory(), uuid.toString() + ".jpg");
				imageOutputUri = Uri.fromFile(path);
				Log.d(TAG, imageOutputUri.toString());
				i.putExtra(MediaStore.EXTRA_OUTPUT, imageOutputUri);

				try {
					startActivityForResult(i, TAKE_PICTURE);
				} catch (Exception e) {
					Log.d(TAG, e.toString());
				}
			}
		});
	}

	/**
	 * Attaches the button from the xml source then assigns a new
	 * onClickListener to the button. If pressed, the current word is spoken via
	 * tts.
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
	 * Enable full colour support.
	 */
	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		Window window = getWindow();
		window.setFormat(PixelFormat.RGBA_8888);
		window.addFlags(WindowManager.LayoutParams.FLAG_DITHER);
	}

	/**
	 * prevents the view from switching from portrait to landscape when the
	 * phone is rotated.
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
	 * adds the current word and associated data to the local database
	 * 
	 * @param word
	 * @param definition
	 * @param locationName
	 * @param lat
	 * @param lng
	 * @param locationID
	 */
	private void addWordToBank(String word, String definition, String gloss, String locationName, String type,
			Double lat, Double lng, String locationID) {

		Uri uri = Constants.WORDS_URI;
		ContentValues cv = new ContentValues();
		cv.put(Constants.KEY_WORD, word);
		cv.put(Constants.KEY_WORD_DEFINITION, definition);
		cv.put(Constants.KEY_WORD_GLOSS, gloss);
		cv.put(Constants.KEY_WORD_LOCATION, locationName);
		cv.put(Constants.KEY_WORD_TYPE, type);
		cv.put(Constants.KEY_WORD_LOCATION_LAT, lat);
		cv.put(Constants.KEY_WORD_LOCATION_LNG, lng);
		cv.put(Constants.KEY_WORD_LOCATION_ID, locationID);
		cv.put(Constants.KEY_WORD_TIME, System.currentTimeMillis());
		cv.put(Constants.KEY_WORD_ADDITION_TYPE, Constants.WORD_ADDITION_TYPE_COLLECT);
		Uri newWord = getContentResolver().insert(uri, cv);

	}
	
	private TextView addDefinitiontextView(int defNum) {
		TextView definitionTextView = new TextView(this);
		return definitionTextView;
		
	}

	/*
	 * download current word definitions
	 */
	private class DownloadDefinition extends AsyncTask {

		WordDefinition	wordDefinitions	= null;

		Place			place			= null;
		

		// try {
		// Gson gson = new Gson();
		// String jsonData = HttpUtils.getJSONData(ToponimoApplication
		// .getApp().getHttpClient(), urlString);
		//
		// if (jsonData != null) {
		// place = gson.fromJson(jsonData, Place.class);
		// placenameList.clear();
		// for (Results p : place.getResults()) {
		// placenameList.add(place);
		// }
		// mainActivity.application.setPlaceResults(placenameList);
		// }

		@Override
		protected String doInBackground(Object... params) {

			String urlString = (ApiKeys.DEFINITION_URL + word);

			try {
				Gson gson = new Gson();
				Log.d("Word", word);
				String jsonData = HttpUtils.getJSONData(ToponimoApplication.getApp().getHttpClient(), urlString);
				if (jsonData != null) {
					wordDefinitions = gson.fromJson(jsonData, WordDefinition.class);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
			}

			return null;

		}

		@Override
		protected void onPostExecute(Object result) {
			try {
				if (wordDefinitions.getTotal() > 0) {
					Userword word = new Userword();
					String definition = "";
					for (int i = 0; i < wordDefinitions.getSynset().size(); i++) {
						definition += Integer.toString(i) + ")";
						
						definition += wordDefinitions.getSynset().get(i).getDefinitions();
					}
					
					mainActivity.definitionTextView.setText(definition);
					mainActivity.definition = definition;

					String gloss = wordDefinitions.getSynset().get(0).getSample();
					if (gloss == null)
						gloss = " ";
					mainActivity.glossTextView.setText(gloss);
					mainActivity.gloss = gloss;

					String lexType = wordDefinitions.getSynset().get(0).getLex();
					if (lexType == null)
						lexType = " ";
					// mainActivity.lexTypeTextView.setText(lexType);
					mainActivity.lexType = lexType;
				} else {
					mainActivity.definitionTextView.setText("No definition found for " + word);
				}
				mainActivity.definitionTextView.startAnimation(fadein);
				// mainActivity.definition = definition;
				super.onPostExecute(definition);
			} catch (NullPointerException npe) {

			}
		}

	}


	public void onInit(int arg0) {
		// TODO Auto-generated method stub

	}

	
}