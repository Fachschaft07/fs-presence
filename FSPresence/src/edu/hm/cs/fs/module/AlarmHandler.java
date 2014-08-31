package edu.hm.cs.fs.module;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import edu.hm.cs.fs.receiver.AlarmReceiver;

@SuppressWarnings("static-access")
public class AlarmHandler {
	
	private final static String TAG = "AlarmHandler";
	
	public static void setAlarm(final Context context, final long sendInterval) {
		Log.d(TAG, "setAlarm: " + sendInterval);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
		Intent alarmIntent = new Intent(context, AlarmReceiver.class);
		PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
		alarmManager.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + sendInterval, alarmPendingIntent);
	}
	
	public static void cancelAlarm(final Context context) {
		Log.d(TAG, "cancelAlarm: ");
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
		Intent alarmIntent = new Intent(context, AlarmReceiver.class);
		PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
		alarmManager.cancel(alarmPendingIntent);
	}

}
