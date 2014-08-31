package edu.hm.cs.fs.presence.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecManager {
	
	public static final String SALT = "06~49?W<)k\\@qXegW`)(`W3mT{BmvH";
	
	public static String buildSHAHash(final String toHash) {
		StringBuffer sb = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(toHash.getBytes());
			byte byteData[] = md.digest();
			 
	        //convert the byte to hex format method 1
	        sb = new StringBuffer();
	        for (int i = 0; i < byteData.length; i++) {
	        	sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
	        }
		} catch (NoSuchAlgorithmException e) {
			System.out.println(e.getMessage());
		}
		return sb.toString();
	}

}
