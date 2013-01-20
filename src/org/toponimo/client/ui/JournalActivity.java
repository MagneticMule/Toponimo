package org.toponimo.client.ui;

import java.io.File;
import com.nineoldandroids.*;
import java.util.UUID;

import org.toponimo.client.Constants;
import org.toponimo.client.ToponimoApplication;
import org.toponimo.client.ui.adapters.JournalCursorAdapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;

import com.actionbarsherlock.ActionBarSherlock;
import com.actionbarsherlock.ActionBarSherlock.OnCreateOptionsMenuListener;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.internal.nineoldandroids.animation.ObjectAnimator;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;

import org.toponimo.client.R;

public class JournalActivity extends SherlockFragmentActivity implements
		android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

	private static final String	TAG				= "WordBankActivity";

	private static final int	TAKE_PICTURE	= 0x02;
	private static final int	LOADER_ID		= 0;

	private ToponimoApplication	mApplication;
	private JournalCursorAdapter	mListAdapter;
	private ListView			mWordListView;

	LinearLayout				profileHeader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timeline);

		// remove unused background layer added by the theme (increase draw
		// performance)
		this.getWindow().setBackgroundDrawable(null);

		// get reference to global application context
		mApplication = (ToponimoApplication) getApplication();

		// get a reference to the activities ActionBar, disable the home icon
		// and title and set the title
		getSupportActionBar().setTitle("Toponimo");
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		// used by the listadapter to reference the SQL rows contained in the
		// word table.
		String[] bindFrom = { Constants.KEY_WORD, Constants.KEY_WORD_DEFINITION, Constants.KEY_WORD_GLOSS,
				Constants.KEY_WORD_TYPE, Constants.KEY_WORD_LOCATION };

		// used by the listadapter to
		int[] bindTo = { R.id.label_word, R.id.label_definition, R.id.label_gloss, R.id.label_type, R.id.label_location };

		// create new list adapter using automatic bindings and set it as the
		// word listviews adapter. We need to add all header views before the
		// adapter is set.
		mListAdapter = new JournalCursorAdapter(getApplicationContext(), R.layout.simplerow_definition_single, null,
				bindFrom, bindTo, 0);
		mWordListView = (ListView) findViewById(R.id.wordListView);

		// inflate then add the profile header containing user name and word
		// count to the top of the word listview
		profileHeader = (LinearLayout) ((LayoutInflater) getBaseContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.timeline_header, null);
		mWordListView.addHeaderView(profileHeader);
		mWordListView.setAdapter(mListAdapter);

		// get reference to the activities loadermanger. This will automate the
		// loading and managing
		// of the word store SQL database.
		getSupportLoaderManager().initLoader(LOADER_ID, null, this);

		// check if the user is logged in, if not then display the login screen
		if (!mApplication.isLoggedIn()) {
			Intent i = new Intent(JournalActivity.this, LoginActivity.class);
			startActivity(i);
		} else {
			TextView profileUserNameTextView = (TextView) profileHeader.findViewById(R.id.label_user_name);
			profileUserNameTextView.setText(mApplication.getUser().getFirstName() + " "
					+ mApplication.getUser().getLastName());

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.word_bank_menu, menu);
		return true;
	}

	/*
	 * 
	 */
	public void addImage() {
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

	public void addDrawing(View v) {

	}

	public void addSound(View v) {

	}

	public void addWord(View v) {

	}

	public void addLocation(View v) {

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.menu_support:
			break;

		case R.id.menu_logout:
			mApplication.deleteDetails();
			startActivity(new Intent(JournalActivity.this, LoginActivity.class));
			break;

		case R.id.menu_discover:
			startActivity(new Intent(JournalActivity.this, PlaceListActivity.class));
			break;

		case R.id.menu_add_drawing:
			startActivity(new Intent(JournalActivity.this, DrawActivity.class));
			break;

		}
		return false;

	}

	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// projection for SQL Queries
		String[] wordsProjection = { Constants.KEY_ROW_ID, Constants.KEY_WORD, Constants.KEY_WORD_DEFINITION,
				Constants.KEY_WORD_GLOSS, Constants.KEY_WORD_TYPE, Constants.KEY_WORD_LOCATION,
				Constants.KEY_WORD_LOCATION_LAT, Constants.KEY_WORD_LOCATION_LNG, Constants.KEY_WORD_TYPE,
				Constants.KEY_WORD_LOCATION_ID, Constants.KEY_WORD_TIME };

		// sort by time added, newest first
		final String sortOrder = Constants.KEY_WORD_TIME + " DESC";

		// Create cursorloader, passing in the wordsprojection
		CursorLoader cursorLoader = new CursorLoader(getApplicationContext(), Constants.WORDS_URI, wordsProjection,
				null, null, sortOrder);
		Log.d(TAG, "OnCreateLoader");
		return cursorLoader;
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		cursor.moveToFirst();
		mListAdapter.swapCursor(cursor);
		TextView profileWordCountTextView = (TextView) profileHeader.findViewById(R.id.label_user_word_count);
		profileWordCountTextView.setText(Integer.toString(cursor.getCount()) + " words");
		Log.d(TAG, "OnloadFinished");
	}

	public void onLoaderReset(Loader<Cursor> loader) {
		mListAdapter.swapCursor(null);
		Log.d(TAG, "OnloadReset");

	}

	@Override
	public boolean onSearchRequested() {
		// TODO Auto-generated method stub
		return super.onSearchRequested();

	}

}