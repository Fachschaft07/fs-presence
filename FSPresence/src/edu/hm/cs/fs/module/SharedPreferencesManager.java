package edu.hm.cs.fs.module;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {
	
	private static final String PREFERENCES_KEY = "Presence";
	
	private static final int PREFERENCES_MODE = 0;
	
	public static void setName(final Context context, final String name) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_KEY, PREFERENCES_MODE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString("name", name);
		editor.commit();
	}
	
	public static String getName(final Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_KEY, PREFERENCES_MODE);
		return sharedPreferences.getString("name", "FSler");
	}
	
	public static void setStatus(final Context context, final String status) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_KEY, PREFERENCES_MODE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString("status", status);
		editor.commit();
	}
	
	public static String getStatus(final Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_KEY, PREFERENCES_MODE);
		return sharedPreferences.getString("status", "Present");
	}
	
	public static void setNetworkState(final Context context, final String state) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_KEY, PREFERENCES_MODE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString("networkState", state);
		editor.commit();
	}
	
	public static String getNetworkState(final Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_KEY, PREFERENCES_MODE);
		return sharedPreferences.getString("networkState", "Unknown");
	}
	
	public static void setEduroam(final Context context, final boolean isEduroam) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_KEY, PREFERENCES_MODE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean("iseduroam", isEduroam);
		editor.commit();
	}
	
	public static Boolean isEduroam(final Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_KEY, PREFERENCES_MODE);
		return sharedPreferences.getBoolean("iseduroam", false);
	}
	
	public static void setActive(final Context context, final boolean active) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_KEY, PREFERENCES_MODE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean("active", active);
		editor.commit();
	}
	
	public static Boolean isActive(final Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_KEY, PREFERENCES_MODE);
		return sharedPreferences.getBoolean("active", false);
	}
	
	public static void setLastUpdate(final Context context, final String lastUpdate) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_KEY, PREFERENCES_MODE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString("lastUpdate", lastUpdate);
		editor.commit();
	}
	
	public static String getLastUpdate(final Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_KEY, PREFERENCES_MODE);
		return sharedPreferences.getString("lastUpdate", "");
	}
	
	public static void setSessionUpdates(final Context context, final int sessionUpdates) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_KEY, PREFERENCES_MODE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt("sessionUpdates", sessionUpdates);
		editor.commit();
	}
	
	public static int getSessionUpdates(final Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_KEY, PREFERENCES_MODE);
		return sharedPreferences.getInt("sessionUpdates", 0);
	}
	
	public static void varify(final Context context, final boolean verification) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_KEY, PREFERENCES_MODE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean("verification", verification);
		editor.commit();
	}
	
	public static boolean isVerified(final Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_KEY, PREFERENCES_MODE);
		return sharedPreferences.getBoolean("verification", false);
	}
	
	public static void setPassword(final Context context, final String password) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_KEY, PREFERENCES_MODE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString("password", SecManager.buildSHAHash(password));
		editor.commit();
	}
	
	public static String getPassword(final Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_KEY, PREFERENCES_MODE);
		String rawHash = sharedPreferences.getString("password", "");
		String toSend = rawHash + SecManager.SALT + getName(context);
		return SecManager.buildSHAHash(toSend);
	}
}
