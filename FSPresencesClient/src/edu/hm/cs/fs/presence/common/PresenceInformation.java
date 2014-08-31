package edu.hm.cs.fs.presence.common;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Data wrapper used to inform the server about presence information of the client.
 * 
 * @author Thomas Maier
 */
@XmlRootElement
public class PresenceInformation {
	/** Nick name. */
	private String nickName;

	/** Password. */
	private String password;

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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj != null) {
			if (obj instanceof PresenceInformation) {
				return this.nickName.equals(((PresenceInformation) obj).nickName);
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "PresenceInformation[" + this.nickName + "," + this.status + "]";
	}
}
