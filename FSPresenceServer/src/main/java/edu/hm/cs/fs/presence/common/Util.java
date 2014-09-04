package edu.hm.cs.fs.presence.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.EncodingException;

/**
 * Some utilities.
 * 
 * @author Thomas Maier
 *
 */
public class Util {
	private static final Logger LOGGER = LogManager.getLogger(Util.class.getName());

	/**
	 * Generates a SHA-256 hash.
	 * 
	 * @param plaintext
	 *            Plaintext to hash
	 * @return Generated hash
	 */
	public static String generateAuthenticationHash(String plaintext) {
		StringBuffer hash = new StringBuffer();
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(plaintext.getBytes());
			byte[] byteData = md.digest();
			for (int i = 0; i < byteData.length; i++) {
				hash.append(Integer.toString((byteData[i] & 0xFF) + 256, 16).substring(1));
			}
		} catch (NoSuchAlgorithmException e) {
			LOGGER.fatal(e.getMessage());
		}
		return hash.toString();
	}

	/**
	 * Escaping to prevent injection.
	 * 
	 * @param unsafe
	 *            Unsafed user input
	 * @return Escaped user input
	 * @throws EncodingException
	 *             If something went wrong with the escaping
	 */
	public static String escapeForXSS(String unsafe) throws EncodingException {
		String safe = unsafe;
		safe = ESAPI.encoder().encodeForHTML(safe);
		safe = ESAPI.encoder().encodeForHTMLAttribute(safe);
		safe = ESAPI.encoder().encodeForJavaScript(safe);
		safe = ESAPI.encoder().encodeForCSS(safe);
		safe = ESAPI.encoder().encodeForURL(safe);
		return safe;
	}

	/**
	 * Load a Java properties file.
	 * 
	 * @param filePath Path to the properties file
	 * @return Properties
	 */
	public static Properties loadPropertiesFile(String filePath) {
		Properties properties = new Properties();
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(filePath);
			properties.load(inputStream);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return properties;
	}
}
