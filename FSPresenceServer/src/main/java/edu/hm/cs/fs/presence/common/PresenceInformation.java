package edu.hm.cs.fs.presence.common;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * POJO which contains user presence information.
 * 
 * @author Thomas Maier
 *
 */
@XmlRootElement
public class PresenceInformation {
	private String nickName;
	private String password;
	private String status;

	public String getNickName() {
		return this.nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean equals(Object obj) {
		if ((obj != null) && ((obj instanceof PresenceInformation))) {
			return this.nickName.equals(((PresenceInformation) obj).nickName);
		}
		return false;
	}

	public String toString() {
		return "PresenceInformation[" + this.nickName + "," + this.status + "]";
	}
}
