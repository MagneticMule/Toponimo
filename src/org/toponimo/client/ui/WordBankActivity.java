package org.toponimo.client.ui;

import java.io.File;
import com.nineoldandroids.*;
import java.util.UUID;

import org.toponimo.client.Constants;
import org.toponimo.client.ToponimoApplication;
import org.toponimo.client.ui.adapters.WordBankCursorAdapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;

import com.actionbarsherlock.ActionBarSherlock;
import com.actionbarsherlock.ActionBarSherlock.OnCreateOptionsMenuListener;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.internal.nineoldandroids.animation.ObjectAnimator;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;

import org.toponimo.client.R;

public class WordBankActivity extends SherlockActivity {

	private static final String	TAG				= "WordBankActivity";
	private static final int	TAKE_PICTURE	= 1;
	private ToponimoApplication	mApplication;
	private ListAdapter			mListAdapter;
	private ListView			mWordListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.word_bank);

		/* get a reference to the activities ActionBar and disable the */
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		getSupportActionBar().setTitle("Timeline");

		String[] wordsProjection = { Constants.KEY_WORD, Constants.KEY_WORD_DEFINITION, Constants.KEY_WORD_GLOSS,
				Constants.KEY_WORD_TYPE, Constants.KEY_WORD_LOCATION, Constants.KEY_WORD_LOCATION_LAT,
				Constants.KEY_WORD_LOCATION_LNG, Constants.KEY_WORD_TYPE, Constants.KEY_WORD_LOCATION_ID,
				Constants.KEY_WORD_TIME };
		
		

		/*
		 * Construct cursor for db operations. Note: using the Activities
		 * managedQuery provides in built management for the cursor, e.g.
		 * destroying the cursor on activity pause or close.
		 */
		Cursor cursor = managedQuery(Constants.WORDS_URI, new String[] { Constants.KEY_ROW_ID, Constants.KEY_WORD,
				Constants.KEY_WORD_DEFINITION, Constants.KEY_WORD_GLOSS, Constants.KEY_WORD_TYPE,
				Constants.KEY_WORD_LOCATION, Constants.KEY_WORD_LOCATION_LAT, Constants.KEY_WORD_LOCATION_LNG,
				Constants.KEY_WORD_LOCATION_ID, Constants.KEY_WORD_TIME }, null, null, Constants.KEY_WORD_TIME
				+ " DESC");

		mListAdapter = new WordBankCursorAdapter(getApplicationContext(), cursor, wordsProjection, new int[] {
				R.id.label_word, R.id.label_definition, R.id.label_gloss, R.id.label_type, R.id.label_location });

		mApplication = (ToponimoApplication) getApplication();
		mWordListView = (ListView) findViewById(R.id.wordListView);

		LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		/*
		 * add the profile header containing user name and word count to the top
		 * of the word listview
		 */
		LinearLayout profileHeader = (LinearLayout) inflater.inflate(R.layout.timeline_header, null);
		mWordListView.addHeaderView(profileHeader);
		mWordListView.setAdapter(mListAdapter);

		TextView profileUserNameTextView = (TextView) profileHeader.findViewById(R.id.label_user_name);

		if (!mApplication.isLoggedIn()) {
			Intent i = new Intent(WordBankActivity.this, LoginActivity.class);
			startActivity(i);
		} else {
			profileUserNameTextView.setText(mApplication.getUser().getFirstName() + " "
					+ mApplication.getUser().getLastName());
			TextView profileWordCountTextView = (TextView) profileHeader.findViewById(R.id.label_user_word_count);
			profileWordCountTextView.setText(Integer.toString(cursor.getCount()) + " words");
		}

		/*
		 * Attach item click listener for individual entry manipulation
		 */
		mWordListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View v, int index, long id) {				
				if (v.findViewById(R.id.dictionary_entry_actions).getVisibility() == View.GONE) {
					v.findViewById(R.id.dictionary_entry_actions).setVisibility(View.VISIBLE);
					
					//v.findViewById(R.id.dictionary_entry_actions).animate().alpha(1f).setDuration(500l).start();
					
					ObjectAnimator.ofFloat(v.findViewById(R.id.dictionary_entry_actions), "y", 60).setDuration(500).start();
				} else {
					 //v.findViewById(R.id.dictionary_entry_actions).animate().alpha(0f).setDuration(500l).start();
					v.findViewById(R.id.dictionary_entry_actions).setVisibility(View.GONE);
				}
			}
		});
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.word_bank_menu, menu);
		return true;
	}

	public void addImage(View v) {
		Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		UUID uuid = UUID.randomUUID();

		File path = new File(Environment.getExternalStorageDirectory(), uuid.toString() + ".jpg");
		Uri imageOutputURI = Uri.fromFile(path);
		Log.d(TAG, imageOutputURI.toString());
		i.putExtra(MediaStore.EXTRA_OUTPUT, imageOutputURI);

		try {
			startActivityForResult(i, TAKE_PICTURE);
		} catch (Exception e) {
			Log.d(TAG, e.toString());
		}
	}
	
	public void addDrawing(View v){
		
	}
	
	public void addSound(View v){
		
	}
	
	public void addWord(View v) {
		
	}
	
	public void addLocation(View v){
		
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

		case R.id.options_menu_add:
			Intent j = new Intent(WordBankActivity.this, PlaceListActivity.class);
			startActivity(j);
			break;

		}
		return false;

	}

}