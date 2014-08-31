package edu.hm.cs.fs.presence.common;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import android.content.Context;
import android.text.format.Time;
import android.util.Log;
import edu.hm.cs.fs.module.AlarmHandler;
import edu.hm.cs.fs.module.NetworkManager;
import edu.hm.cs.fs.module.SharedPreferencesManager;
import edu.hm.cs.fs.module.Status;
import edu.hm.cs.fs.presence.PresenceActivity;
import edu.hm.cs.fs.presence.R;

/**
 * The PresenceThread will send a PresenceInformation Object 
 * to the SERVERIP.
 * 
 * @author René
 */
public class PresenceManager {
	
	private final String TAG = "PresenceManager";
	
	private final String SERVERIP = "192.168.60.253";
	
	private final String eduIP = "fs.cs.hm.edu";
	
	private final int PORT = 65535;
	
	private final String keyStorePW = "dhlm0V7CcxuhBcY9IfplS5ogJi5dwJ";
	
	private final long SEND_INTERVAL = 60000;
	
	private final Context context;
	
	private final PresenceInformation pi;
	
	private static PresenceManager instance = null;
	
	private PresenceThread presenceThread;
	
	private PresenceActivity activity;
	
	public PresenceManager(final Context context) {
		this.context = context;
		pi = new PresenceInformation();
	}
	
	public static PresenceManager getInstance(final Context context) {
		if (instance == null) {
            instance = new PresenceManager(context);
        }
        return instance;
	}
	
	public void enablePresence() {
		Log.d(TAG, "Enable Presence");
		SharedPreferencesManager.setActive(context, true);
		activity = (PresenceActivity) PresenceActivity.getActivity();
		if (activity != null) {
			activity.changeMenuStatus(1);
		}
		send();
	}
	
	public void disablePresence() {
		Log.d(TAG, "Disable Presence");
		activity = (PresenceActivity) PresenceActivity.getActivity();
		if (activity != null) {
			activity.changeMenuStatus(2);
		}
		SharedPreferencesManager.setSessionUpdates(context, 0);
		SharedPreferencesManager.setActive(context, false);
		AlarmHandler.cancelAlarm(context);
	}
	
	public void send() {
		if (presenceThread == null || !presenceThread.isAlive()) {
			presenceThread = new PresenceThread();
			presenceThread.start();
		}
	}
	
	public class PresenceThread extends Thread {
		
		private final String TAG = "PresenceThread";
		
		@Override
		public void run() {
			super.run();
			
			Log.d(TAG, "PresencesThread started");
			
			boolean active = SharedPreferencesManager.isActive(context);
			String name = SharedPreferencesManager.getName(context);
			String status = SharedPreferencesManager.getStatus(context);
			
			if (!status.equals(Status.INCOGNITO.toString()) && NetworkManager.isInStudentCouncil(context)) {
				Log.i(TAG, "Send PresenceInformation: " + name + "," + status);
				
				pi.setNickName(name);
				pi.setStatus(status);

				try {
					sendSLL();
					int updates = SharedPreferencesManager.getSessionUpdates(context);
					SharedPreferencesManager.setSessionUpdates(context, ++updates);
					Time time = new Time();
					time.setToNow();
					SharedPreferencesManager.setLastUpdate(context, time.format("%Y/%m/%d - %H:%M:%S"));
					activity = (PresenceActivity) PresenceActivity.getActivity();
					if (activity != null) {
						activity.changeMenuStatus(0);
					}
					Log.i(TAG, "PresenceInformation sent");
				} catch (Exception e) {
					Log.e("Exception in " + TAG, e.getMessage());
					SharedPreferencesManager.setSessionUpdates(context, 0);
					activity = (PresenceActivity) PresenceActivity.getActivity();
					if (activity != null) {
						activity.changeMenuStatus(1);
					}
				}
				AlarmHandler.setAlarm(context, SEND_INTERVAL);
			} else {
				SharedPreferencesManager.setSessionUpdates(context, 0);
				Log.i(TAG, "Send PresenceInformation aborted: Active=" + active + ", status=" + status);
				AlarmHandler.setAlarm(context, SEND_INTERVAL);
			}
		}
		
		public void sendSLL() throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException, KeyManagementException {
			
			SSLContext sslContext = SSLContext.getInstance("SSL");
			KeyStore trustStore = KeyStore.getInstance("BKS");
			TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			InputStream trustStoreStream = context.getResources().openRawResource(R.raw.keystore);
			trustStore.load(trustStoreStream, keyStorePW.toCharArray());
			trustManagerFactory.init(trustStore);
			sslContext.init(null, trustManagerFactory.getTrustManagers(), null);
			
			String currentIP = (NetworkManager.isPostdienststelle(context)) ? SERVERIP : eduIP;
			Socket socket = sslContext.getSocketFactory().createSocket(currentIP, PORT);
			OutputStreamWriter socketOSW = new OutputStreamWriter(socket.getOutputStream());
			BufferedWriter socketBW = new BufferedWriter(socketOSW);
			socketBW.write(pi.toXML(context));
			socketBW.close();
			socketOSW.close();
			socket.close();
		}
	}
}
