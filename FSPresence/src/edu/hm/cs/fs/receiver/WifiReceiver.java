package edu.hm.cs.fs.receiver;

import edu.hm.cs.fs.module.NetworkManager;
import edu.hm.cs.fs.module.SharedPreferencesManager;
import edu.hm.cs.fs.presence.PresenceActivity;
import edu.hm.cs.fs.presence.common.PresenceManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Will be called by a Broadcast if Network State changes.
 * 
 * @author René
 */
public class WifiReceiver extends BroadcastReceiver {
	
	private final String TAG = "WifiReceiver";
	
	@Override
	public void onReceive(final Context context, final Intent intent) {
		Log.d(TAG, "onReceive");
		
		NetworkInfo.State state = NetworkManager.getNetworkState(context);
		
		boolean alreadyConnected = SharedPreferencesManager.getNetworkState(context).equals(NetworkInfo.State.CONNECTED.toString());
		boolean networkIsConnected = state.equals(NetworkInfo.State.CONNECTED);
		boolean validNetwork = NetworkManager.isInValidNetwork(context);
		
		if (validNetwork && networkIsConnected && !alreadyConnected) {
			PresenceManager.getInstance(context).enablePresence();
		} else if(state.equals(NetworkInfo.State.DISCONNECTED)) {
			Log.d(TAG, "Deactivate Presence");
			PresenceActivity activity = (PresenceActivity)PresenceActivity.getActivity();
			if (activity != null) {
				activity.changeMenuStatus(2);
			}
			PresenceManager.getInstance(context).disablePresence();
		}
		
		SharedPreferencesManager.setNetworkState(context, state.toString());
		
	}
}
