package com.magneticmule.toponimo.client.ui;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
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
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.magneticmule.toponimo.client.ApiKeys;
import com.magneticmule.toponimo.client.Constants;
import com.magneticmule.toponimo.client.R;
import com.magneticmule.toponimo.client.ToponimoApplication;
import com.magneticmule.toponimo.client.ui.adapters.WordGalleryAdapter;
import com.magneticmule.toponimo.client.utils.BitmapUtils;
import com.magneticmule.toponimo.client.utils.http.HttpUtils;

public class WordDetailsActivity extends Activity implements
	TextToSpeech.OnInitListener {

    protected static final String TAG = "WordDetailsActivity";
    private static final int TAKE_PICTURE = 1;
    private static final int DATA_CHECKSUM = 28031974;
    private ToponimoApplication application;
    private static WordDetailsActivity mainActivity;
    private TextToSpeech tts;
    private String word;
    private String definition;
    protected TextView definitionTextView;
    private Uri imageOutputURI = null;

    private Animation fadein;
    private final ArrayList<String> imageList = new ArrayList<String>();

    private Gallery gallery;
    private WordGalleryAdapter galleryAdapter;

    // private List<Pair> images = new ArrayList<Pair>();

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
	super.onActivityResult(requestCode, resultCode, data);

	if (resultCode == RESULT_CANCELED) {
	    return;
	}

	if (requestCode == TAKE_PICTURE) {
	    Log.d(TAG, "Got to TAKE PICTURE");
	    if (imageOutputURI != null) {
		Log.v(TAG, "Data is not NULL");
		Log.v("BitmapString: ", imageOutputURI.toString());
		String thumbnailPath = BitmapUtils.createThumbnail(
			imageOutputURI, 80);
		Log.d(TAG + ": ThumbnailPath=", thumbnailPath);

		imageList.add(thumbnailPath);
		galleryAdapter.notifyDataSetChanged();
		(new ImageUploader()).execute((Object) null);
	    }

	} else if (requestCode == DATA_CHECKSUM) {
	    if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {

		// if TTS data exists, create a new TTS instance
		tts = new TextToSpeech(this, this);
	    } else {
		// TTS data is not installed. Ask user if they would like to
		// install it.
		DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {

		    public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:

			    // install TTS data
			    Intent installIntent = new Intent();
			    installIntent
				    .setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
			    startActivity(installIntent);
			    break;
			}

		    }
		};

		// Display alert dialog asking to install TTS data
		AlertDialog.Builder builder = new AlertDialog.Builder(
			WordDetailsActivity.this);
		builder.setMessage("Google Text to Speech needs to be installed. Install it?");
		builder.setPositiveButton("Yes", clickListener);
		builder.setNegativeButton("No", clickListener);
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

	    public void onItemClick(AdapterView<?> adapterView, View v,
		    int position, long id) {
		Bundle bundle = new Bundle();
		bundle.putStringArrayList("images", imageList);
		bundle.putInt("currentIndex", position);
		Intent i = new Intent(WordDetailsActivity.this,
			ImageViewActivity.class);
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
		String currentLocation = application.getPlaceResults(
			application.getCurrentPlaceIndex()).getName();
		double currentLat = application
			.getPlaceResults(application.getCurrentPlaceIndex())
			.getLocation().getLat();
		double currentLng = application
			.getPlaceResults(application.getCurrentPlaceIndex())
			.getLocation().getLng();
		String currentLocationID = application.getPlaceResults(
			application.getCurrentPlaceIndex()).getId();

		addWordToBank(word, definition, currentLocation, currentLat,
			currentLng, currentLocationID);
		Toast t = Toast.makeText(WordDetailsActivity.this, "Added '"
			+ word + "' to Word Store", Toast.LENGTH_SHORT);
		t.show();
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

		File path = new File(Environment.getExternalStorageDirectory(),
			uuid.toString() + ".jpg");
		imageOutputURI = Uri.fromFile(path);
		Log.d(TAG, imageOutputURI.toString());
		i.putExtra(MediaStore.EXTRA_OUTPUT, imageOutputURI);

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
		tts.speak(wordTextView.getText().toString() + ".",
			TextToSpeech.QUEUE_FLUSH, null);
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
    private void addWordToBank(String word, String definition,
	    String locationName, Double lat, Double lng, String locationID) {
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

    /*
     * download current word definitions
     */
    private class DownloadDefinition extends AsyncTask {

	String definition = "";

	@Override
	protected String doInBackground(Object... params) {
	    try {
		String completeUrl = (ApiKeys.DEFINITION_URL + word);
		Log.d("Word", word);
		definition = HttpUtils.getJSONData(ToponimoApplication.getApp()
			.getHttpClient(), completeUrl);
		Log.i("Definition", definition);
	    } catch (Exception e) {
		e.printStackTrace();
	    } finally {
	    }

	    return definition;

	}

	@Override
	protected void onPostExecute(Object result) {
	    mainActivity.definitionTextView.setText(definition);
	    mainActivity.definitionTextView.startAnimation(fadein);
	    mainActivity.definition = definition;
	    super.onPostExecute(definition);
	}

    }

    public void onInit(int arg0) {

    }

    private class ImageUploader extends AsyncTask<Object, Object, Object> {

	@Override
	protected String doInBackground(Object... params) {

	    String thumbnailPath = null;

	    if (imageOutputURI != null) {
		Log.d(TAG, "Data is not NULL");
		Log.d("BitmapString: ", imageOutputURI.toString());

		try {
		    int currentPlaceIndex = application.getCurrentPlaceIndex();
		    String currentPlaceId = application.getPlaceResults(
			    currentPlaceIndex).getId();
		    List<NameValuePair> postParameters = new ArrayList<NameValuePair>(
			    1);
		    postParameters.add(new BasicNameValuePair(
			    "postobject[placeid]", currentPlaceId));
		    postParameters.add(new BasicNameValuePair(
			    "postobject[word]", word));
		    postParameters.add(new BasicNameValuePair(
			    "postobject[wordno]", "23"));
		    postParameters.add(new BasicNameValuePair(
			    "postobject[synsetno]", "7"));
		    postParameters.add(new BasicNameValuePair(
			    "postobject[ownerid]", application.getUserDetails()
				    .getUserId().toString()));

		    HttpUtils.executeHttpMultipartPost(ToponimoApplication
			    .getApp().getHttpClient(), postParameters,
			    "http://www.toponimo.org/toponimo/api/images/",
			    imageOutputURI);
		} catch (UnsupportedEncodingException uce) {
		    uce.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}

	    }
	    return thumbnailPath;
	}

	protected void onPostExecute(String thumbnailPath) {

	}
    }

    private class ImagePair {

	private String fullImagePath;

	private String thumbImagePath;

	public ImagePair(String _imagePath, String _thumbPath) {
	    fullImagePath = _imagePath;
	    thumbImagePath = _thumbPath;
	}

	public String getFullImagePath() {
	    return fullImagePath;
	}

	public void setFullImagePath(String fullImagePath) {
	    this.fullImagePath = fullImagePath;
	}

	public String getThumbImagePath() {
	    return thumbImagePath;
	}

	public void setThumbImagePath(String thumbImagePath) {
	    this.thumbImagePath = thumbImagePath;
	}

    }

}