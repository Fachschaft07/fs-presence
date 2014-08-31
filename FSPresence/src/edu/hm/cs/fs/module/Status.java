package edu.hm.cs.fs.module;

import android.content.Context;
import edu.hm.cs.fs.presence.R;

public enum Status {

	PRESENT(0, "Present"), BUSY(1, "Busy"), INCOGNITO(2, "Incognito");
	
	private int value;
	
	private String status;
	
	private Status(final int value, final String status) {
		this.value = value;
		this.status = status;
	}
	
	public int getValue() {
		return value;
	}
	
	public String toString() {
		return status;
	}
	
	public String getRessourceValue(final Context context) {
		switch (value) {
		case 0:
			return context.getResources().getString(R.string.present);
		case 1:
			return context.getResources().getString(R.string.busy);
		case 2:
			return context.getResources().getString(R.string.incognito);
		default:
			return context.getResources().getString(R.string.incognito);
		}
	}
	
	public static String getResourceValue(final Context context, final String status) {
		if (status.equals("Present")) {
			return context.getResources().getString(R.string.present);
		} else if(status.equals("Busy")) {
			return context.getResources().getString(R.string.busy);
		} else if(status.equals("Incognito")) {
			return context.getResources().getString(R.string.incognito);
		} else {
			return context.getResources().getString(R.string.present);
		}
	}
}
