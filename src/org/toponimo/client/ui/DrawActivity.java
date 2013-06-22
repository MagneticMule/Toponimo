package org.toponimo.client.ui;

import java.io.IOException;

import org.apache.http.HttpStatus;
import org.toponimo.client.ApiKeys;
import org.toponimo.client.Constants;
import org.toponimo.client.ToponimoApplication;
import org.toponimo.client.User;
import org.toponimo.client.utils.TwoReturnValues;
import org.toponimo.client.utils.http.HttpUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.actionbarsherlock.ActionBarSherlock;
import com.actionbarsherlock.ActionBarSherlock.OnCreateOptionsMenuListener;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.internal.nineoldandroids.animation.ObjectAnimator;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.google.analytics.tracking.android.EasyTracker;

import org.toponimo.client.R;

public class DrawActivity extends SherlockActivity {

	public static final String	TAG			= "DrawActivity";

	// Reference to global appliation
	ToponimoApplication			mApplication;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.draw);

		mApplication = (ToponimoApplication) this.getApplication();
		String activityTitle = "Draw";
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			Log.d(TAG, "Title=" + extras.getString("TITLE"));
			activityTitle = activityTitle + (" '" + extras.getString("TITLE") + "'");
		}
		
		
		/*
		 * get a reference to the activities ActionBar, disable the home icon
		 * and title and set the title
		 */
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		getSupportActionBar().setTitle(activityTitle);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	/*
	 * Enable full colour support (non-Javadoc)
	 * 
	 * @see android.app.Activity#onAttachedToWindow()
	 */
	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		Window window = getWindow();
		window.setFormat(PixelFormat.RGBA_8888);
		window.addFlags(WindowManager.LayoutParams.FLAG_DITHER);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.draw_menu, menu);
		return true;
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
