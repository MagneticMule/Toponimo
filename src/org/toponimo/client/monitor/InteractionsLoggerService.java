package org.toponimo.client.monitor;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;

import android.net.ConnectivityManager;

import android.os.IBinder;
import android.util.Log;

public class InteractionsLoggerService extends IntentService {



	protected static String				TAG	= "InteractionsLoggerService";
	protected ConnectivityManager	connectivityManager;
	protected ContentResolver			contentResolver;
	protected AlarmManager				alarmManager;
	PendingIntent									alarmIntent;

	String												ALARM_ACTION;
	
	public InteractionsLoggerService() {
		super(TAG);
		setIntentRedelivery(false);
		
	}

	@Override
	public void onCreate() {
		super.onCreate();
		connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		Log.i("ILS", "Service Created");


	}


	@Override
	public void onDestroy() {
		Log.d(TAG, "Service Destroyed");
		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		
		super.onStart(intent, startId);
	}

	@Override
	public IBinder onBind(Intent arg0) {

		return null;
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "Handling Intent");
		
		
	}

}
