package edu.hm.cs.fs.module;

import java.util.List;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

public class NetworkManager {
	
	public final static String SSID_Postdienststelle = "Postdienststelle";
	
	private final static String SSID_eduroam = "eduroam";
	
	private final static String SSID_eduroama = "eduroam-a";
	
	/**
	 * Returns Current NetworkState.
	 * 
	 * @param context
	 * @return Current NetworkState.
	 */
	public static NetworkInfo.State getNetworkState(final Context context) {
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return networkInfo.getState();
	}
	
	/**
	 * Get current SSID or Null, if not connected to a
	 * WIFI.
	 * 
	 * @param context
	 * @return Current SSID
	 */
	public static String getCurrentSsid(final Context context) {
		String ssid = null;
	    final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	    final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
	    if (connectionInfo != null && !TextUtils.isEmpty(connectionInfo.getSSID())) {
	      ssid = connectionInfo.getSSID();
	      ssid = ssid.replaceAll("\"", "");
	    }
		return ssid;
	}
	
	public static boolean isInValidNetwork(final Context context) {
		String ssid = getCurrentSsid(context);
		if (ssid == null) {
			return false;
		}
		return ssid.equals(SSID_Postdienststelle) || ssid.equals(SSID_eduroam) || ssid.equals(SSID_eduroama);
	}
	
	public static boolean isInRange(final Context context, final String ssid) {
		
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		wifiManager.startScan();
		List<ScanResult> results = wifiManager.getScanResults();
		for (ScanResult sr : results) {
			String currentSsid = sr.SSID.replaceAll("\"", "");
			if (currentSsid.equals(ssid)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isInStudentCouncil(final Context context) {
		
		String ssid = getCurrentSsid(context);
		if (ssid != null && (ssid.equals(SSID_Postdienststelle))) {
			return true;
		} else if(ssid != null && (ssid.equals(SSID_eduroam) || ssid.equals(SSID_eduroama))) {
			if (isInRange(context, SSID_Postdienststelle)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isPostdienststelle(final Context context) {
		return getCurrentSsid(context).equals(SSID_Postdienststelle);
	}
}
