package com.magneticmule.toponimo.client.ui;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.magneticmule.toponimo.client.R;
import com.magneticmule.toponimo.client.ToponimoApplication;
import com.magneticmule.toponimo.client.database.DBHelper;
import com.magneticmule.toponimo.client.utils.http.HttpDataUtils;

public class AddWordActivity extends Activity {

	private ToponimoApplication	application;

	private int									position;
	private String							upid	= null;
	private String							placeName;
	private String							placeNameFromCaller;
	private String							words	= null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_word);

		application = (ToponimoApplication) this.getApplication();

		Intent sender = getIntent();

		this.position = application.getCurrentPlaceIndex();

		upid = new String(application.getPlaceResults().get(position).getResults().get(position).getId());
		placeName = new String(application.getPlaceResults().get(position).getResults().get(position).getName());
		// placeNameFromCaller = new
		// String(application.getPlaceResults().get(position).getResults().get(position).getName().toString());

		Log.i("ID", upid);
		Log.i("Name", placeName);

		InputMethodManager imm = (InputMethodManager) AddWordActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);

		if (imm != null) {
			imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
		}

		final TextView addWordText = (TextView) findViewById(R.id.add_word_add_edit_text);
		addWordText.setOnEditorActionListener(new OnEditorActionListener() {

			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
				if (arg1 == EditorInfo.IME_ACTION_GO) {
					addWord(addWordText);
				}
				return false;
			}

		});

		// TextView placeTypes = (TextView)
		// findViewById(R.id.place_details_place_type);

	}

	public void addWord(final TextView addWordText) {
		String confirmMessage = "";

		words = addWordText.getText().toString();

		if (!words.equalsIgnoreCase("")) {
			if (words.contains("*")) {
				confirmMessage = "Taboo language is not allowed";
			} else {
				confirmMessage = "Added '" + words + "' to Place";
				int currentPlaceIndex = application.getCurrentPlaceIndex();
				application.getPlaceResults().get(currentPlaceIndex).getResults().get(currentPlaceIndex).addWord(words);
				Log.i("WORDS", words);
				Log.i("PlaceResultWord", application.getPlaceResults().get(position).getResults().get(position).getWords().get(0));
				Intent i = new Intent();
				i.putExtra("words", words);
				setResult(RESULT_OK, i);

				new Thread(new Runnable() {

					public void run() {
						try {
							List<NameValuePair> wordList = new ArrayList<NameValuePair>(1);
							wordList.add(new BasicNameValuePair("words", words));
							wordList.add(new BasicNameValuePair("upid", upid));
							wordList.add(new BasicNameValuePair("uid", "123"));
							HttpDataUtils.executeHttpPost(wordList);
						} catch (UnsupportedEncodingException uce) {
						} catch (IOException e) {
						}
					}
				}).start();

			}
		} else {
			confirmMessage = "No words to add";
		}
		Toast toast = Toast.makeText(AddWordActivity.this, confirmMessage, Toast.LENGTH_SHORT);
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

}