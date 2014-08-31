package edu.hm.cs.fs.presence;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnShowListener;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import edu.hm.cs.fs.dialog.StatisticDialog;
import edu.hm.cs.fs.dialog.StatusDialog;
import edu.hm.cs.fs.module.NetworkManager;
import edu.hm.cs.fs.module.SharedPreferencesManager;
import edu.hm.cs.fs.module.Status;
import edu.hm.cs.fs.presence.common.PresenceManager;
import edu.hm.cs.fs.receiver.WifiReceiver;

/**
 * MainActivity.
 * 
 * @author René
 */
public class PresenceActivity extends Activity {

	private final String TAG = "PresenceActivity";

	private final String authentification = "Marvin";

	private EditText editText;

	private EditText authentificationField;

	private static SubMenu options;

	private static Activity activity = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_presence);

		String name = SharedPreferencesManager.getName(this);
		editText = (EditText) findViewById(R.id.editText);
		editText.setText(name);

		Button statusButton = (Button) findViewById(R.id.buttonStatus);
		statusButton.setText(Status.getResourceValue(this,
				SharedPreferencesManager.getStatus(this)));
		statusButton.setOnClickListener(new View.OnClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				StatusDialog statusDialog = new StatusDialog(getActivity());
				statusDialog.show();
			}
		});

		if (NetworkManager.isInStudentCouncil(this)) {
			PresenceManager.getInstance(this).enablePresence();
		}
		
		authentificate();
	}

	public void registerWifiReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.net.wifi.STATE_CHANGE");
	    registerReceiver(new WifiReceiver(), filter);
	}

	@SuppressLint({ "NewApi", "InlinedApi" })
	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {

		if (Integer.valueOf(Build.VERSION.SDK_INT) >= 11) {
			options = menu.addSubMenu(0, R.id.action_settings, 0,
					R.string.statistic);
			options.getItem().setShowAsAction(
					android.view.MenuItem.SHOW_AS_ACTION_ALWAYS);
			if (SharedPreferencesManager.isActive(this)) {
				options.getItem().setIcon(R.drawable.circle_yellow_light);
			} else {
				options.getItem().setIcon(R.drawable.circle_red_dark);
			}
		} else {
			// getMenuInflater().inflate(R.menu.activity_ddmenu, menu);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			Log.d(TAG, "Menu Item Settings clicked");
			StatisticDialog statisticDialog = new StatisticDialog(this);
			statisticDialog.show();
			break;
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		activity = this;
		super.onResume();
	}

	@Override
	protected void onPause() {
		activity = null;
		super.onPause();
	}
	
	public void changeMenuStatus(final int status) {
		Log.d(TAG, "changeMenuStatus: " + status);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				switch (status) {
				case 0:
					options.setIcon(R.drawable.circle_green_dark);
					break;
				case 1:
					options.setIcon(R.drawable.circle_yellow_light);
					break;
				case 2:
					options.setIcon(R.drawable.circle_red_dark);
					break;
				default:
					options.setIcon(R.drawable.circle_yellow_light);
					break;
				}
			}
		});
	}

	public static Activity getActivity() {
		return activity;
	}

	/**
	 * Saves input from EditText Field to SharedPreferences or shows an
	 * AlertDialog on wrong input.
	 * 
	 * @param view
	 */
	public void onSaveButtonClicked(final View view) {
		String name = editText.getText().toString();
		Log.d(TAG, "SaveButtonClicked: " + name);
		if (name.length() > 0) {
			SharedPreferencesManager.setName(this, name);
			Toast.makeText(this, "Name \'" + name + "\' saved...",
					Toast.LENGTH_SHORT).show();
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.nameDialogTitle)
					.setMessage(R.string.emptyChar)
					.setIcon(R.drawable.ic_warning)
					.setPositiveButton(R.string.ok, new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					}).create();
			builder.show();
		}
	}

	/**
	 * Gets called by StatusDialog. Saves the selected Status from StatusDialog
	 * to SharedPreferences and activates the Presence.
	 * 
	 * @param status
	 *            Current Status (Present, Busy, Incognito)
	 */
	public void changeStatus(final Status status) {

		Log.d(TAG, "changeStatus: " + status);

		SharedPreferencesManager.setStatus(this, status.toString());

		Button statusButton = (Button) findViewById(R.id.buttonStatus);
		statusButton.setText(status.getRessourceValue(this));

		if (NetworkManager.isInStudentCouncil(this)) {
			if (!status.toString().equals(Status.INCOGNITO.toString())) {
				PresenceManager.getInstance(this).enablePresence();
			} else {
				PresenceManager.getInstance(this).disablePresence();
			}
		} else if (NetworkManager.isInValidNetwork(this)) {
			changeMenuStatus(1);
		} else {
			changeMenuStatus(2);
		}
	}

	public void authentificate() {
		if (!SharedPreferencesManager.isVerified(this)) {
			AlertDialog.Builder authentificationDialogBuilder = new AlertDialog.Builder(
					this);
			
			LayoutInflater inflater = getLayoutInflater();
	        final View layout = inflater.inflate(R.layout.dialog_authentificate, null);
			authentificationDialogBuilder.setView(layout);
			final EditText authentificationField = (EditText) layout.findViewById(R.id.editAuthentification);
			authentificationDialogBuilder.setPositiveButton(R.string.ok,
					new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					});

			final AlertDialog authentificationDialog = authentificationDialogBuilder
					.create();

			authentificationDialog.setOnShowListener(new OnShowListener() {

				@Override
				public void onShow(final DialogInterface dialogInterface) {
					authentificationDialog.getButton(
							AlertDialog.BUTTON_POSITIVE).setOnClickListener(
							new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									String input = authentificationField
											.getText().toString();
									if (input.equals(authentification)) {
										SharedPreferencesManager.varify(
												getActivity(), true);
//										registerWifiReceiver();
										authentificationDialog.dismiss();
									} else {
										Toast.makeText(getActivity(),
												R.string.authentificationerror,
												Toast.LENGTH_SHORT).show();
									}
								}
							});
				}
			});
			
			authentificationDialog.setCancelable(false);
			authentificationDialog.show();
		}
	}
}
