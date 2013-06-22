package org.toponimo.client.ui;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.UUID;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.toponimo.client.ApiKeys;
import org.toponimo.client.Constants;
import org.toponimo.client.RestService;
import org.toponimo.client.ToponimoApplication;
import org.toponimo.client.models.Place;
import org.toponimo.client.models.Results;
import org.toponimo.client.models.Userword;

import org.toponimo.client.models.webster.Definition;
import org.toponimo.client.models.webster.WebsterEntries;
import org.toponimo.client.ui.adapters.WordGalleryAdapter;
import org.toponimo.client.utils.BitmapUtils;
import org.toponimo.client.utils.http.HttpUtils;
import org.toponimo.client.utils.http.ToponimoVolley;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import com.actionbarsherlock.app.SherlockActivity;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import org.toponimo.client.R;

@SuppressLint("NewApi")
public class WordDetailsActivity extends SherlockActivity implements TextToSpeech.OnInitListener {

    protected static final String      TAG                       = "WordDetailsActivity";
    private static final int           TAKE_PICTURE              = 1;
    private static final int           DATA_CHECKSUM             = 28031974;
    private static final int           EDIT_DEFINITION_DIALOG_ID = 1974;
    private ToponimoApplication        application;
    private static WordDetailsActivity mainActivity;
    private TextToSpeech               tts                       = null;
    private String                     lemma                     = null;
    private String                     definition                = null;

    WebsterEntries                     websterEntries            = null;

    protected TextView                 ipaTextView;
    protected TextView                 posTextView;
    protected TextView                 lexTypeTextView;

    protected ProgressBar              progressBar;
    private Uri                        imageOutputUri            = null;

    private ArrayList<String>          imageList                 = new ArrayList<String>();

    private Gallery                    gallery;
    private WordGalleryAdapter         galleryAdapter;

    LinearLayout                       rootLinearLayout;
    LinearLayout                       defContainer;

    // Editdefinition dialog controls
    EditText                           editDefinitionText;

    Cursor                             mCursor                   = null;

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

                // TODO: Refactor image upload code. Move to contentprovider
                ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>(1);
                // Construct new bundle to pass to the restservice
                Bundle bundle = new Bundle();
                bundle.putString(Constants.UPLOAD_IMAGE_PLACE_ID, currentPlaceId);
                bundle.putString(Constants.UPLOAD_IMAGE_WORD_NAME, lemma);
                bundle.putString(Constants.UPLOAD_IMAGE_WORD_NUMBER, "23");
                bundle.putString(Constants.UPLOAD_IMAGE_SYNSET_NUMBER, "7");
                bundle.putString(Constants.UPLOAD_IMAGE_USER_ID, application.getUser().getId());
                bundle.putString(Constants.UPLOAD_IMAGE_PATH, imageOutputUri.toString());
                bundle.putSerializable(Constants.UPLOAD_IMAGE_PATH, (Serializable) imageOutputUri);

                // Start new REST service to upload image
                // Intent restService = new Intent(WordDetailsActivity.this,
                // RestService.class);

                // restService.setData(imageOutputURI);
                // restService.putExtra(Constants.REST_PARAMS, bundle);
                // startService(restService);
                // (new ImageUploader()).execute((Object) null);
            }

        } else if (requestCode == DATA_CHECKSUM) {

            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // if TTS data exists, create a new TTS instance
                tts = new TextToSpeech(this, this);
            } else {
                // TTS data is not installed. Ask user to install
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

    @SuppressLint("NewApi")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_details);

        com.actionbarsherlock.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(actionBar.DISPLAY_SHOW_HOME | actionBar.DISPLAY_SHOW_CUSTOM
                | actionBar.DISPLAY_SHOW_TITLE);
        actionBar.setTitle("Toponimo");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setSubtitle("Dictionary");
        actionBar.setHomeButtonEnabled(true);

        application = (ToponimoApplication) this.getApplication();
        mainActivity = this;

        LinearLayout galleryContainer = (LinearLayout) findViewById(R.id.word_details_gallery_container);

        galleryAdapter = new WordGalleryAdapter(this, imageList);

        ipaTextView = (TextView) findViewById(R.id.word_details_ipa_text_view);
        lexTypeTextView = (TextView) findViewById(R.id.label_type);
        posTextView = (TextView) findViewById(R.id.word_details_pos_text_view);

        progressBar = (ProgressBar) findViewById(R.id.word_details_progress_bar);

        ttsCheck();
        Intent sender = getIntent();
        lemma = sender.getExtras().getString("word");
        lemma = lemma.trim();
        TextView wordTextView = (TextView) findViewById(R.id.word_details_word_view);
        wordTextView.setText(lemma);
        createSpeakButton();
        createAddPictureButton();
        createGallery();
        createAddToWordBankButton();

        rootLinearLayout = (LinearLayout) findViewById(R.id.word_details_top_linear_layout);
        LayoutTransition rootTransition = rootLinearLayout.getLayoutTransition();
        rootTransition.enableTransitionType(LayoutTransition.CHANGING);

        defContainer = (LinearLayout) findViewById(R.id.word_details_def_holder);

        RequestQueue queue = ToponimoVolley.getRequestQueue();
        // StringRequest request = new StringRequest(Method.GET,
        // ApiKeys.DEFINITION_URL + lemma, successListener(),
        // errorListener());
        // request.setShouldCache(true);
        // queue.add(request);
        String[] wordProjection = { "W." + Constants.KEY_ROW_ID, "W." + Constants.KEY_WORD_ID,
                Constants.KEY_WORD_LEMMA,
                Constants.KEY_WORD_SYLLABLES, Constants.KEY_WORD_PRONOUNCIATION, Constants.KEY_WORD_POS,
                Constants.KEY_WORD_FORMS, Constants.KEY_DEFINITION, Constants.KEY_IMAGE_FILEPATH,
                Constants.KEY_WORD_LOCATION, Constants.KEY_WORD_LOCATION_LAT, Constants.KEY_WORD_LOCATION_LNG,
                Constants.KEY_WORD_LOCATION_ID, Constants.KEY_WORD_TIME, Constants.KEY_WORD_ADDITION_TYPE };

        // sort by time added, newest first
        final String sortOrder = Constants.KEY_WORD_TIME + " DESC";
        mCursor = getContentResolver().query(Constants.WORDS_URI, wordProjection,"W.lemma = " + lemma, null,null);
        String w = mCursor.getString(mCursor.getColumnIndex(Constants.KEY_WORD_LEMMA));
        Log.d("WORD", w);
        // mCursor.close();

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

                String syllables = websterEntries.getWords().get(0).getSyllables();
                String pronounciation = websterEntries.getWords().get(0).getPronounciation();
                String pos = websterEntries.getWords().get(0).getPart_of_speach();
                String forms = websterEntries.getWords().get(0).getInflected_forms();
                String wordId = websterEntries.getWords().get(0).get_id();
                ArrayList<Definition> definitions = websterEntries.getWords().get(0).getDefinitions();
                // TODO: Change this ya cunt
                addWordToJournal(lemma, wordId, definitions, syllables, pronounciation, pos, forms, currentLocation,
                        currentLat, currentLng, currentLocationID);

                Toast t = Toast.makeText(WordDetailsActivity.this, "Added '" + lemma + "' to Timeline",
                        Toast.LENGTH_SHORT);
                t.show();
                Intent i = new Intent(WordDetailsActivity.this, JournalActivity.class);
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
                    Log.d(TAG, " Starting camera activity failed: " + e.toString());
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
     * @param syllables
     * @param pronounciation
     * @param pos
     * @param forms
     * @param locationName
     * @param lat
     * @param lng
     * @param locationID
     */
    private void addWordToJournal(String word, String wordId, ArrayList<Definition> definitions, String syllables,
            String pronounciation, String pos, String forms, String locationName, Double lat, Double lng,
            String locationID) {

        Uri wordsUri = Constants.WORDS_URI;
        ContentValues wordCv = new ContentValues();
        wordCv.put(Constants.KEY_WORD_LEMMA, word);
        wordCv.put(Constants.KEY_WORD_ID, wordId);
        wordCv.put(Constants.KEY_WORD_SYLLABLES, syllables);
        wordCv.put(Constants.KEY_WORD_PRONOUNCIATION, pronounciation);
        wordCv.put(Constants.KEY_WORD_POS, pos);
        wordCv.put(Constants.KEY_WORD_FORMS, forms);
        wordCv.put(Constants.KEY_WORD_LOCATION, locationName);
        wordCv.put(Constants.KEY_WORD_LOCATION_LAT, lat);
        wordCv.put(Constants.KEY_WORD_LOCATION_LNG, lng);
        wordCv.put(Constants.KEY_WORD_LOCATION_ID, locationID);
        wordCv.put(Constants.KEY_WORD_TIME, System.currentTimeMillis());
        wordCv.put(Constants.KEY_WORD_ADDITION_TYPE, Constants.WORD_ADDITION_TYPE_COLLECT);
        Uri newWord = getContentResolver().insert(wordsUri, wordCv);

        // Construct definition batch
        Uri defsUri = Constants.DEFINITIONS_URI;
        ArrayList<ContentProviderOperation> defsOperation = new ArrayList<ContentProviderOperation>();

        // Create a set of insert ContentProviderOperations
        for (int i = 0; i < definitions.size(); i++) {
            defsOperation.add(ContentProviderOperation.newInsert(defsUri)
                    .withValue(Constants.KEY_DEFINITION, definitions.get(i).getDefinition())
                    .withValue(Constants.KEY_WORD_ID, wordId)
                    .build());

        }

        try {
            ContentProviderResult[] defsResult = getContentResolver().applyBatch(Constants.AUTHORITY, defsOperation);
        } catch (RemoteException e) {
            Log.d(TAG, "Error inserting batch values");
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private Response.Listener<String> successListener() {
        return new Response.Listener<String>() {

            public void onResponse(String response) {
                Gson gson = new Gson();

                try {
                    websterEntries = gson.fromJson(response, WebsterEntries.class);
                } catch (JsonSyntaxException jse) {
                    Log.d(TAG, "JSON Syntax Error", jse);
                    Log.d(TAG + "response", response);
                } catch (JsonIOException jioe) {
                    Log.d(TAG, "JSON IO Exception", jioe);
                }

                ipaTextView.setText(websterEntries.getWords().get(0).getPronounciation());
                posTextView.setText(websterEntries.getWords().get(0).getPart_of_speach());

                int totalDefs = websterEntries.getWords().get(0).getDefinitions().size();

                TextView[] defViews = new TextView[totalDefs];

                for (int i = 0; i < totalDefs; i++) {
                    defViews[i] = (TextView) getLayoutInflater().inflate(R.layout.definition_textview, defContainer,
                            false);
                    defViews[i]
                            .setText(websterEntries.getWords().get(0).getDefinitions().get(i).getSpannedDefinition());
                    defContainer.addView(defViews[i]);

                }

                progressBar.setVisibility(View.GONE);
                defContainer.setVisibility(View.VISIBLE);

                Log.d(TAG, websterEntries.getWords().get(0).getDefinitions().get(0).getSpannedDefinition().toString());

            }

        };
    }

    private Response.ErrorListener errorListener() {
        return new Response.ErrorListener() {

            public void onErrorResponse(VolleyError error) {
                // definitionTextView.setText("No definition found for " +
                // lemma.toString());
                // Log.d(TAG, "Error downloading definitions " +
                // error.toString());

            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Start analytics tracking
        EasyTracker.getInstance().activityStart(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        // Stop analytics tracking
        mCursor.close();
        EasyTracker.getInstance().activityStop(this);
    }

    public void onInit(int status) {
        // TODO Auto-generated method stub

    }

}