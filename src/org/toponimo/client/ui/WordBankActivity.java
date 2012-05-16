package org.toponimo.client.ui;

import org.toponimo.client.Constants;
import org.toponimo.client.ToponimoApplication;
import org.toponimo.client.ui.adapters.WordBankCursorAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.toponimo.client.R;

public class WordBankActivity extends Activity {

	private ListView			mWordListView;
	private ListAdapter			mListAdapter;
	private ToponimoApplication	mApplication;

	private Button				mAddWordButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.word_bank);

		mAddWordButton = (Button) findViewById(R.id.timeline_add_location_button);
		mAddWordButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Intent i = new Intent(WordBankActivity.this,
						PlaceListActivity.class);
				startActivity(i);
			}
		});

		String[] wordsProjection = { Constants.KEY_WORD,
				Constants.KEY_WORD_DEFINITION, Constants.KEY_WORD_GLOSS,
				Constants.KEY_WORD_TYPE, Constants.KEY_WORD_LOCATION,
				Constants.KEY_WORD_LOCATION_LAT,
				Constants.KEY_WORD_LOCATION_LNG, Constants.KEY_WORD_TYPE,
				Constants.KEY_WORD_LOCATION_ID, Constants.KEY_WORD_TIME };

		// Construct cursor for db operations. Note: using the Activities
		// managedQuery provides
		// in built management for the cursor, e.g. destroying the cursor on
		// activity pause or close.

		Cursor cursor = managedQuery(Constants.WORDS_URI, new String[] {
				Constants.KEY_ROW_ID, Constants.KEY_WORD,
				Constants.KEY_WORD_DEFINITION, Constants.KEY_WORD_GLOSS,
				Constants.KEY_WORD_TYPE, Constants.KEY_WORD_LOCATION,
				Constants.KEY_WORD_LOCATION_LAT,
				Constants.KEY_WORD_LOCATION_LNG,
				Constants.KEY_WORD_LOCATION_ID, Constants.KEY_WORD_TIME },
				null, null, Constants.KEY_WORD_TIME + " DESC");

		mListAdapter = new WordBankCursorAdapter(
				getApplicationContext(),
				R.layout.simplerow_definition,
				cursor,
				wordsProjection,
				new int[] { R.id.label_word, R.id.label_definition,
						R.id.label_gloss, R.id.label_type, R.id.label_location });

		mApplication = (ToponimoApplication) getApplication();
		mWordListView = (ListView) findViewById(R.id.wordListView);

		LayoutInflater inflater = (LayoutInflater) getBaseContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout profileHeader = (RelativeLayout) inflater.inflate(
				R.layout.timeline_header, null);
		mWordListView.addHeaderView(profileHeader);

		mWordListView.setAdapter(mListAdapter);

		TextView profileUserNameTextView = (TextView) profileHeader
				.findViewById(R.id.label_user_name);

		try {
			if (!mApplication.isLoggedIn()) {
				Intent i = new Intent(WordBankActivity.this,
						LoginActivity.class);
				startActivity(i);
			} else {
				profileUserNameTextView.setText(mApplication.getUser()
						.getFirstName()
						+ " "
						+ mApplication.getUser().getLastName());

				TextView profileWordCountTextView = (TextView) profileHeader
						.findViewById(R.id.label_user_word_count);
				profileWordCountTextView.setText(Integer.toString(cursor
						.getCount()) + " words");
			}
		} catch (NullPointerException ne) {

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.options_menu_support:
			break;

		case R.id.options_menu_logout:
			mApplication.deleteDetails();
			Intent i = new Intent(WordBankActivity.this, LoginActivity.class);
			startActivity(i);
			break;
		}
		return false;

	}

}