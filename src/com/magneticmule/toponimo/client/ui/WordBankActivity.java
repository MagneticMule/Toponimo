package com.magneticmule.toponimo.client.ui;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.magneticmule.toponimo.client.Constants;
import com.magneticmule.toponimo.client.R;

public class WordBankActivity extends Activity {

	private ListView		wordListView;
	private ListAdapter	adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.word_bank);

		wordListView = (ListView) findViewById(R.id.wordListView);

		String[] wordsProjection = { Constants.KEY_WORD, Constants.KEY_WORD_DEFINITION, Constants.KEY_WORD_LOCATION,
				Constants.KEY_WORD_LOCATION_LAT, Constants.KEY_WORD_LOCATION_LNG, Constants.KEY_WORD_LOCATION_ID };


		// Construct cursor for db operations. Note: using the Activities
		// managedQuery provides
		// in built management for the cursor, e.g. destroying the cursor on
		// activity pause or close.

		Cursor cursor = managedQuery(Constants.WORDS_URI, new String[] { Constants.KEY_ROW_ID, Constants.KEY_WORD,
				Constants.KEY_WORD_DEFINITION, Constants.KEY_WORD_LOCATION, Constants.KEY_WORD_LOCATION_LAT, Constants.KEY_WORD_LOCATION_LNG, Constants.KEY_WORD_LOCATION_ID },
				null, null, null);

		adapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.simplerow, cursor, wordsProjection, new int[] { R.id.label,
				R.id.label_address, R.id.label_words });

		wordListView.setAdapter(adapter);

	}

}