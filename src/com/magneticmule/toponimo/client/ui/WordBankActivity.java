package com.magneticmule.toponimo.client.ui;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.magneticmule.toponimo.client.Constants;
import com.magneticmule.toponimo.client.R;
import com.magneticmule.toponimo.client.ToponimoApplication;

public class WordBankActivity extends Activity {

    private ListView wordListView;
    private ListAdapter adapter;
    private ToponimoApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.word_bank);

	String[] wordsProjection = { Constants.KEY_WORD,
		Constants.KEY_WORD_DEFINITION, Constants.KEY_WORD_GLOSS,
		Constants.KEY_WORD_LOCATION, Constants.KEY_WORD_LOCATION_LAT,
		Constants.KEY_WORD_LOCATION_LNG, Constants.KEY_WORD_LOCATION_ID };

	// Construct cursor for db operations. Note: using the Activities
	// managedQuery provides
	// in built management for the cursor, e.g. destroying the cursor on
	// activity pause or close.

	Cursor cursor = managedQuery(Constants.WORDS_URI,
		new String[] { Constants.KEY_ROW_ID, Constants.KEY_WORD,
			Constants.KEY_WORD_DEFINITION,
			Constants.KEY_WORD_GLOSS, Constants.KEY_WORD_LOCATION,
			Constants.KEY_WORD_LOCATION_LAT,
			Constants.KEY_WORD_LOCATION_LNG,
			Constants.KEY_WORD_LOCATION_ID }, null, null, null);

	adapter = new SimpleCursorAdapter(getApplicationContext(),
		R.layout.simplerow_definition, cursor, wordsProjection,
		new int[] { R.id.label_word, R.id.label_definition,
			R.id.label_gloss, R.id.label_location });

	application = (ToponimoApplication) getApplication();
	wordListView = (ListView) findViewById(R.id.wordListView);

	LayoutInflater inflater = (LayoutInflater) getBaseContext()
		.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	RelativeLayout profileHeader = (RelativeLayout) inflater.inflate(
		R.layout.timeline_header, null);
	wordListView.addHeaderView(profileHeader);

	wordListView.setAdapter(adapter);

	TextView profileUserNameTextView = (TextView) profileHeader
		.findViewById(R.id.label_user_name);
	profileUserNameTextView.setText(application.getUserDetails()
		.getFirstName()
		+ " "
		+ application.getUserDetails().getLastName());

	TextView profileWordCountTextView = (TextView) profileHeader
		.findViewById(R.id.label_user_word_count);
	profileWordCountTextView.setText(Integer.toString(cursor.getCount())
		+ " words");

    }

}