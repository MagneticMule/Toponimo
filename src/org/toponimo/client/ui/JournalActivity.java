package org.toponimo.client.ui;

import java.io.File;
import com.nineoldandroids.*;
import java.util.UUID;

import org.toponimo.client.Constants;
import org.toponimo.client.ToponimoApplication;
import org.toponimo.client.ui.adapters.JournalCursorAdapter;
import org.toponimo.client.ui.adapters.WordListAdapter;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentValues;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.fortysevendeg.android.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.android.swipelistview.SwipeListView;
import com.fortysevendeg.android.swipelistview.SwipeListViewListener;
import com.google.analytics.tracking.android.EasyTracker;

import org.toponimo.client.R;

@SuppressLint("NewApi")
public class JournalActivity extends SherlockFragmentActivity implements
        android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

    private static final String  TAG            = org.toponimo.client.ui.JournalActivity.class.getName();

    private static final int     LOADER_ID      = 0;

    private ToponimoApplication  mApplication;
    private JournalCursorAdapter mCursorAdapter;
    private SwipeListView        mWordListView;

    LinearLayout                 profileHeader;
    LinearLayout                 newWordParent;
    LinearLayout                 newWord;

    private String               mCurrentWordId = "";

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timeline);

        // remove unused background layer added by the theme (increase draw
        // performance)
        // TODO: Replace this with background in theme
        this.getWindow().setBackgroundDrawable(null);

        // get reference to global application context
        mApplication = (ToponimoApplication) getApplication();

        // get a reference to the activities ActionBar, disable the home icon
        // and set the title and sub-title
        com.actionbarsherlock.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(actionBar.DISPLAY_SHOW_HOME | actionBar.DISPLAY_SHOW_CUSTOM
                | actionBar.DISPLAY_SHOW_TITLE);
        actionBar.setTitle("Toponimo");
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setSubtitle("Journal");

        // used by the listadapter to reference the SQL rows contained in the
        // word table.
        // String[] bindFrom = { Constants.KEY_WORD_LEMMA,
        // Constants.KEY_WORD_PRONOUNCIATION, Constants.KEY_WORD_POS,
        // Constants.KEY_DEFINITION, Constants.KEY_WORD_LOCATION,
        // Constants.KEY_IMAGE_FILEPATH };

        String[] bindFrom = { Constants.KEY_WORD_LEMMA, Constants.KEY_WORD_PRONOUNCIATION, Constants.KEY_WORD_POS,
                Constants.KEY_DEFINITION,
                Constants.KEY_WORD_LOCATION };

        // array of views contained in which to 'bind' the data from the cursor
        int[] bindTo = { R.id.label_word, R.id.label_definition, R.id.label_gloss, R.id.label_type, R.id.label_location };

        // create new list adapter using automatic bindings and set it as the
        // word listviews adapter. We need to add all header views before the
        // adapter is set.
        mCursorAdapter = new JournalCursorAdapter(this, R.layout.journal_entry, null, bindFrom, bindTo, 0);
        mWordListView = (SwipeListView) findViewById(R.id.wordListView);

        // add default animations in wordlistview
        LayoutTransition wordListViewTransition = mWordListView.getLayoutTransition();
        wordListViewTransition.enableTransitionType(LayoutTransition.CHANGING);

        // inflate then add the profile header containing user name and word
        // count to the top of the word listview
        profileHeader = (LinearLayout) ((LayoutInflater) getBaseContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.timeline_header, null);
        mWordListView.addHeaderView(profileHeader);

        // inflate then add the new word header container to the listview. This
        // view contains the
        // new journal word view
        newWordParent = (LinearLayout) ((LayoutInflater) getBaseContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.new_journal_word, null);

        // inflate the linearlayout for the new journal word.Initially this view
        // will be invisible
        // (visibility = gone).
        newWord = (LinearLayout) (newWordParent.findViewById(R.id.new_journal_word));

        // add the new word view to the listview
        mWordListView.addHeaderView(newWordParent);

        mWordListView.setSwipeListViewListener(new BaseSwipeListViewListener() {

            @Override
            public void onClickFrontView(int position) {
                Log.d("swipe", String.format("onClickFrontView %d", position));
            }

        });

        // Set the custom cursor adapter for the listview
        mWordListView.setAdapter(mCursorAdapter);

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

    public void addImage(View view) {
        Log.d(TAG, "Add Image");
        int position = mWordListView.getPositionForView((FrameLayout) (view.getParent().getParent()));
        if (position > 0) {
            Cursor cursor = mCursorAdapter.getCursor();
            mCurrentWordId = cursor.getString(cursor.getColumnIndex(Constants.KEY_WORD_ID));
            Log.d(TAG, Integer.toString(position));
            Log.d("WordId", mCurrentWordId);

        }

        if (mCurrentWordId != "") {
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Log.d(TAG, mCurrentWordId);
            // generate a new random uuid for the filename
            UUID uuid = UUID.randomUUID();

            // get reference to the apps cache directory and append the
            // filename forming the full path name of the image to pass to
            // the camera intent
            File filePath = new File(getExternalCacheDir(), "images");
            if (!filePath.exists()) {
                if (!filePath.mkdirs()) {
                    Log.d(TAG, "Unable to create dir " + filePath.toString());
                } else {
                    Log.d(TAG, "Directory created " + filePath.toString());
                }
            }

            File imageFile = new File(filePath.getPath() + File.separator + uuid.toString() + ".jpg");
            Log.d(TAG, imageFile.toString());
            // generate a valid uri from the file path string
            Uri imageOutputURI = Uri.fromFile(imageFile);
            i.putExtra(MediaStore.EXTRA_OUTPUT, imageOutputURI);

            try {
                // start the activity
                mApplication.setNewImageLocation(imageOutputURI);
                startActivityForResult(i, Constants.TAKE_PICTURE);
            } catch (Exception e) {
                Log.d(TAG, " Starting camera activity failed: " + e.toString());
            }

            mCurrentWordId = "";
        }
    }

    public void addDrawing(View view) {
        Log.d(TAG, "addDrawing has been licked");
    }

    public void addLocation(View view) {
        Log.d(TAG, "addLocation has been licked");
    }

    public void addExample(View view) {
        Log.d(TAG, "addExample has been licked");
    }

    public void addSound(View view) {
        Log.d(TAG, "addLocation has been licked");
        Intent i = new Intent(JournalActivity.this, AddAudioActivity.class);
        startActivity(i);
        
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.TAKE_PICTURE) {
                Log.d(TAG, "Picture Result");
                Uri imageLocation = mApplication.getNewImageLocation();
                ContentValues imageCv = new ContentValues();
                imageCv.put(Constants.KEY_WORD_ID, mCurrentWordId);
                imageCv.put(Constants.KEY_IMAGE_FILEPATH, imageLocation.toString());
                imageCv.put(Constants.KEY_IMAGE_OWNER_ID, mApplication.getUser().getId());
                // write the image info to the database
                Uri newImage = getContentResolver().insert(Constants.IMAGES_URI, imageCv);
                Log.d(TAG, newImage.toString());
            }

            else if (resultCode == RESULT_CANCELED) {
                Log.d(TAG, "Picture cancelled");

            }

        } else {
            // something else
            Log.d(TAG, "Something else happened here. Hmmm....");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.word_bank_menu, menu);
        return true;
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

            case R.id.menu_add_word:
                newWord.setVisibility(View.VISIBLE);
                break;

        }
        return false;

    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // projection for SQL Queries
        String[] wordsProjection = { "W." + Constants.KEY_ROW_ID, "W." + Constants.KEY_WORD_ID,
                Constants.KEY_WORD_LEMMA,
                Constants.KEY_WORD_SYLLABLES, Constants.KEY_WORD_PRONOUNCIATION, Constants.KEY_WORD_POS,
                Constants.KEY_WORD_FORMS, Constants.KEY_DEFINITION, Constants.KEY_IMAGE_FILEPATH,
                Constants.KEY_WORD_LOCATION, Constants.KEY_WORD_LOCATION_LAT, Constants.KEY_WORD_LOCATION_LNG,
                Constants.KEY_WORD_LOCATION_ID, Constants.KEY_WORD_TIME, Constants.KEY_WORD_ADDITION_TYPE };

        // sort by time added, newest first
        final String sortOrder = Constants.KEY_WORD_TIME + " DESC";

        // Create cursorloader, passing in the wordsprojection
        CursorLoader cursorLoader = new CursorLoader(getApplicationContext(), Constants.WORDS_URI, wordsProjection,
                null, null, null);

        Log.d(TAG, "OnCreateLoader");
        return cursorLoader;
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        cursor.moveToFirst();
        mCursorAdapter.swapCursor(cursor);
        TextView profileWordCountTextView = (TextView) profileHeader.findViewById(R.id.label_user_word_count);
        profileWordCountTextView.setText(Integer.toString(cursor.getCount()) + " words");
        Log.d(TAG, "OnloadFinished");
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
        Log.d(TAG, "OnloadReset");

    }

    @Override
    public boolean onSearchRequested() {
        // TODO Auto-generated method stub
        return super.onSearchRequested();

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
        EasyTracker.getInstance().activityStop(this);
    }

}