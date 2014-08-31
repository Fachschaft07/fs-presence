package edu.hm.cs.fs.presence.common;

import android.content.Context;
import edu.hm.cs.fs.module.SharedPreferencesManager;

/**
 * Data wrapper used to inform the server about presence information of the client.
 * 
 * @author Thomas Maier
 */
public class PresenceInformation {
	/** Nick name. */
	private String nickName;
	
	/** Nick name. */
	private String status;

	public String getNickName() {
		return this.nickName;
	}

	public void setNickName(final String nickName) {
		this.nickName = nickName;
	}
	
	public String getStatus() {
		return this.status;
	}

	public void setStatus(final String status) {
		this.status = status;
	}
	
	public String toXML(final Context context) {
		
		return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<presenceInformation>"
				+ "		<password>" + SharedPreferencesManager.getPassword(context) + "</password>" 
				+ "		<nickName>" + nickName + "</nickName>"
				+ "	    <status>" + status + "</status>"
				+ "</presenceInformation>";
	}
}
