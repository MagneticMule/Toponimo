package org.toponimo.client.ui;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.toponimo.client.R;
import org.toponimo.client.ToponimoApplication;
import org.toponimo.client.utils.http.HttpUtils;

import com.actionbarsherlock.app.SherlockActivity;
import com.google.analytics.tracking.android.EasyTracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;


public class AddWordLocalActivity extends SherlockActivity {

    private ToponimoApplication application;

    private int currentPlaceIndex;
    private String upid = null;
    private String placeName;
    private String words = null;
    InputMethodManager imm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.add_word);
	/* get a reference to the activities ActionBar, disable the home icon and title and set the title*/
	getSupportActionBar().setDisplayShowHomeEnabled(false);
	getSupportActionBar().setTitle("Add Word");

	
	application = (ToponimoApplication) this.getApplication();
	Intent sender = getIntent();
	this.currentPlaceIndex = application.getCurrentPlaceIndex();
	upid = new String(application.getPlaceResults(currentPlaceIndex)
		.getId());
	placeName = new String(application.getPlaceResults(currentPlaceIndex)
		.getName());
	// placeNameFromCaller = new
	// String(application.getPlaceResults().get(position).getResults().get(position).getName().toString());

	Log.i("ID", upid);
	Log.i("Name", placeName);

	imm = (InputMethodManager) AddWordLocalActivity.this
		.getSystemService(Context.INPUT_METHOD_SERVICE);
	if (imm != null) {
	    // imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
	}

	final TextView addWordText = (TextView) findViewById(R.id.add_word_add_edit_text);
	addWordText.setOnEditorActionListener(new OnEditorActionListener() {

	    public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
		if (arg1 == EditorInfo.IME_ACTION_GO) {
		    return true;
		} else {
		    return false;
		}
	    }

	});

	final Button addWordButton = (Button) findViewById(R.id.add_word_add_button);
	addWordButton.setOnClickListener(new View.OnClickListener() {

	    public void onClick(View v) {
		addWord(addWordText);
	    }

	});

    }

    public void addWord(final TextView addWordText) {
	String confirmMessage = "";
	imm.hideSoftInputFromInputMethod(addWordText.getWindowToken(), 0);
	words = addWordText.getText().toString();

	if (!words.equalsIgnoreCase("")) {
	    if (words.contains("*")) {
		confirmMessage = "Oops!!! Those kind of words ";

	    } else {
		confirmMessage = "Added '" + words + "' to '" + placeName;
		application.getPlaceResults(application.getCurrentPlaceIndex())
			.addWord(words);
		Intent i = new Intent();
		i.putExtra("words", words);
		setResult(RESULT_OK, i);

		new Thread(new Runnable() {
		    public void run() {
			try {
			    List<NameValuePair> wordList = new ArrayList<NameValuePair>(
				    1);
			    wordList.add(new BasicNameValuePair(
				    "postobject[word]", words));
			    wordList.add(new BasicNameValuePair(
				    "postobject[placeid]", upid));
			    wordList.add(new BasicNameValuePair(
				    "postobject[userid]", "123"));

			    HttpUtils
				    .executeHttpPost(ToponimoApplication
					    .getApp().getHttpClient(),
					    wordList,
					    "http://www.toponimo.org/toponimo/api/words/");
			} catch (UnsupportedEncodingException uce) {
			} catch (IOException e) {
			}
		    }
		}).start();

	    }
	} else {
	    confirmMessage = "No words to add";
	}
	Toast toast = Toast.makeText(AddWordLocalActivity.this, confirmMessage,
		Toast.LENGTH_SHORT);
	toast.show();
	finish();

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
	protected void onStart() {
		super.onStart();
		// start analytics tracking
		EasyTracker.getInstance().activityStart(this);
		
	}

	@Override
	protected void onStop() {
		super.onStop();
		// stop analytics tracking
		EasyTracker.getInstance().activityStop(this);

	}

}