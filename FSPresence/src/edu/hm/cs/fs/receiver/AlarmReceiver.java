package edu.hm.cs.fs.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import edu.hm.cs.fs.presence.common.PresenceManager;

/**
 * Will be called by a Broadcast.
 * 
 * @author René
 */
public class AlarmReceiver extends BroadcastReceiver{
	
	private final String TAG = "AlarmReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "onReceive");
		PresenceManager.getInstance(context).send();
	}
}
