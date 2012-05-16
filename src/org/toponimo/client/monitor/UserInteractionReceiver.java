package org.toponimo.client.monitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class UserInteractionReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("UIR", "Service Started");
		Intent uploader = new Intent(context, InteractionsLoggerService.class);
		
		context.startService(uploader);
		
	}

}
