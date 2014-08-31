package edu.hm.cs.fs.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import edu.hm.cs.fs.module.Status;
import edu.hm.cs.fs.presence.PresenceActivity;
import edu.hm.cs.fs.presence.R;

public class StatusDialog {
	
	private final AlertDialog.Builder builder;
	
	private final Dialog dialog;
	
	public StatusDialog(final Activity activity) {
		
		builder = new AlertDialog.Builder(activity);
//        builder.setTitle(R.string.status);
		
		LayoutInflater inflater = activity.getLayoutInflater();
        final View layout = inflater.inflate(R.layout.dialog_status, null);
        
        final String[] items = new String[] {activity.getString(R.string.present), activity.getString(R.string.busy), activity.getString(R.string.incognito)};
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, R.layout.status_list_item, items);
        
        ListView lv = (ListView) layout.findViewById(R.id.listView);
        lv.setAdapter(adapter);
        lv.setSelector(R.drawable.holoredlight_list_selector_holo_light);
        lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				switch (position) {
					case 0:
						((PresenceActivity) activity).changeStatus(Status.PRESENT);
						break;
					case 1:
						((PresenceActivity) activity).changeStatus(Status.BUSY);
						break;
					case 2:
						((PresenceActivity) activity).changeStatus(Status.INCOGNITO);
				      	break;
					default:
						((PresenceActivity) activity).changeStatus(Status.INCOGNITO);
				}
				dialog.dismiss();
			}
		});
        
        builder.setView(layout);
        
        dialog = builder.create();
	}
	
	public void show() {
		dialog.show();
	}

}
