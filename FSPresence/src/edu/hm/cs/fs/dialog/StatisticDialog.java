package edu.hm.cs.fs.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnShowListener;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import edu.hm.cs.fs.module.NetworkManager;
import edu.hm.cs.fs.module.SharedPreferencesManager;
import edu.hm.cs.fs.presence.R;

public class StatisticDialog {
	
	private final Activity activity;
	
	private final AlertDialog.Builder builder;
	
	private TextView wifi;
	
	private TextView lastUpdate;
	
	private TextView sessionUpdates;
	
	private EditText editPassword;
	
	private ImageButton savePassword;
	
	public StatisticDialog(final Activity activity) {
		
		this.activity = activity;
		
		builder = new AlertDialog.Builder(activity);
		
//		builder.setTitle(R.string.statistic);
//		builder.setIcon(R.drawable.ic_action_line_chart);
		
		LayoutInflater inflater = activity.getLayoutInflater();
        final View layout = inflater.inflate(R.layout.dialog_statistic, null);
        
        wifi = (TextView)layout.findViewById(R.id.statistic_currentwifi);
        lastUpdate = (TextView)layout.findViewById(R.id.statistic_lastUpdate);
        sessionUpdates = (TextView)layout.findViewById(R.id.statistic_sessionUpdates);
        editPassword = (EditText)layout.findViewById(R.id.editPassword);
        savePassword = (ImageButton)layout.findViewById(R.id.buttonSavePassword);
        
        savePassword.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String input = editPassword.getText().toString();
				if (input.length() > 0) {
					SharedPreferencesManager.setPassword(activity, input);
					Toast.makeText(activity, R.string.passwordSaved, Toast.LENGTH_SHORT).show();
				}
			}
		});
        refreshStatistic();
        builder.setView(layout);
        builder.setPositiveButton(R.string.ok, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String input = editPassword.getText().toString();
				if (input.length() > 0) {
					SharedPreferencesManager.setPassword(activity, input);
					Toast.makeText(activity, R.string.passwordSaved, Toast.LENGTH_SHORT).show();
				}
			}
		});
        
        builder.setNegativeButton(R.string.refresh, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
	}
	
	public void show() {
		final AlertDialog dialog = builder.create();
        
        dialog.setOnShowListener(new OnShowListener() {
			
			@Override
			public void onShow(final DialogInterface dialogInterface) {
				dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {            
		            @Override
		            public void onClick(View v) {
		            	refreshStatistic();
		            }
		        });
			}
		});
        
        dialog.show();
	}
	
	public void refreshStatistic() {
		
		String ssid = NetworkManager.getCurrentSsid(activity);
		if (ssid != null && NetworkManager.isInStudentCouncil(activity)) {
			wifi.setText(ssid);
			wifi.setTextColor(Color.parseColor("#ff669900"));
		} else if (ssid != null && NetworkManager.isInValidNetwork(activity) && !NetworkManager.isInRange(activity, NetworkManager.SSID_Postdienststelle)) {
			wifi.setText(ssid);
			wifi.setTextColor(Color.parseColor("#ffffbb33"));
		} else if (ssid != null && !NetworkManager.isInValidNetwork(activity)){
			wifi.setText(ssid);
			wifi.setTextColor(Color.parseColor("#ffcc0000"));
		} else if (ssid == null) {
			wifi.setText(R.string.noWifi);
			wifi.setTextColor(Color.parseColor("#ffcc0000"));
		} else {
			wifi.setText(R.string.unknownError);
			wifi.setTextColor(Color.parseColor("#ffcc0000"));
		}
		
		String lu = SharedPreferencesManager.getLastUpdate(activity);
		lastUpdate.setText(lu);
		
		int su = SharedPreferencesManager.getSessionUpdates(activity);
		sessionUpdates.setText(String.valueOf(su));
	}
}
