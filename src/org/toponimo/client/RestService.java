package org.toponimo.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.toponimo.client.R;
import org.toponimo.client.utils.http.HttpUtils;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class RestService extends IntentService {

	public static String		TAG				= "ImageUploadService";

	private NotificationManager	mNotificationManager;

	// Unique id for notification service
	private static final int	NOTIFICATION_ID	= 28031974;

	/*
	 * Constructor
	 */
	public RestService() {
		super(TAG);
		setIntentRedelivery(false);

	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// Grab the data from the bundled extras sent from the calling intent
		Bundle bundle = intent.getBundleExtra(Constants.REST_PARAMS);
		Uri imagePath = intent.getData();

		Log.d(TAG, imagePath.getPath());

		// if no receiver is passed to the service then quit
		if (bundle == null) {
			Log.v(TAG, "Data not passed to service. Quitting.");
			return;
		}
		// int action = bundle.getInt(Constants.REST_ACTION);
		post(bundle);
	}

	private void RestAction(int _action) {

	}

	@Override
	public IBinder onBind(Intent intent) {
		return super.onBind(intent);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification notification = new Notification();
		notification.icon = R.drawable.ic_stat_notification_icon;
		notification.tickerText = "Uploading Image";
		notification.when = System.currentTimeMillis();
		notification.number += 1;
		notification.setLatestEventInfo(this, "Toponimo", "Uploading to Toponimo",
				PendingIntent.getActivity(getApplicationContext(), 0, new Intent(), 0));
		mNotificationManager.notify(NOTIFICATION_ID, notification);
		// this.showNotification();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}

	private Bundle get() {

		Bundle bundle = new Bundle();
		return bundle;

	}

	private void put() {

	}

	private void delete() {

	}

	private void post(Bundle _bundle) {

		String currentPlaceId = (String) _bundle.getString(Constants.UPLOAD_IMAGE_PLACE_ID);
		String imageName = (String) _bundle.getString(Constants.UPLOAD_IMAGE_NAME);

		String word = (String) _bundle.getString(Constants.UPLOAD_IMAGE_WORD_NAME);
		Log.d("RESTService: WORD", word);

		String wordNo = (String) _bundle.getString(Constants.UPLOAD_IMAGE_WORD_NUMBER);
		Log.d("WORDNO", wordNo);
		
		String synsetNo = (String) _bundle.getString(Constants.UPLOAD_IMAGE_SYNSET_NUMBER);
		Log.d("SYNSETNO", synsetNo);
		
		String userId = (String) _bundle.getString(Constants.UPLOAD_IMAGE_USER_ID);
		Log.d("USERID", userId);
		
		Uri destinationPath = (Uri) _bundle.getSerializable(Constants.UPLOAD_IMAGE_PATH);
		Log.d("IMAGE_PATH", destinationPath.toString());
		
		try {
			List<NameValuePair> postParameters = new ArrayList<NameValuePair>(1);
			postParameters.add(new BasicNameValuePair("postobject[placeid]", currentPlaceId));
			postParameters.add(new BasicNameValuePair("postobject[word]", word));
			postParameters.add(new BasicNameValuePair("postobject[wordno]", "23"));
			postParameters.add(new BasicNameValuePair("postobject[synsetno]", "7"));
			postParameters.add(new BasicNameValuePair("postobject[ownerid]", userId));

			HttpUtils.executeHttpMultipartPost(ToponimoApplication.getApp().getHttpClient(), postParameters,
					"http://www.toponimo.org/toponimo/api/images/", destinationPath);
		} catch (UnsupportedEncodingException uce) {
			uce.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {

		super.onDestroy();
		mNotificationManager.cancel(NOTIFICATION_ID);
	}

	public void imageResize(String _image) {

	}

}
