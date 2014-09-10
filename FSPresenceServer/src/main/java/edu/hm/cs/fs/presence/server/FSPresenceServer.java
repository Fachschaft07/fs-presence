package edu.hm.cs.fs.presence.server;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.hm.cs.fs.presence.common.Util;

/**
 * Class to start the service.
 * 
 * @author Thomas Maier
 *
 */
public class FSPresenceServer {
	private static final Logger LOGGER = LogManager.getLogger(FSPresenceServer.class.getName());

	private static final String CONFIG_FILE_PATH = "conf/config.properties";

	/**
	 * Main method.
	 * 
	 * @param args
	 *            Command line arguments
	 */
	public static void main(String[] args) {
		LOGGER.info("Start FSPresenceServer...");

		Properties config = Util.loadPropertiesFile(CONFIG_FILE_PATH);

		// Keystore settings for SSL
		System.setProperty("javax.net.ssl.keyStore", config.getProperty("keystore.path"));
		System.setProperty("javax.net.ssl.keyStorePassword", config.getProperty("keystore.password"));

		// Handle command line arguments
		boolean resetDatabase = false;
		List<String> arguments = Arrays.asList(args);
		if ((arguments.contains("-r")) || (arguments.contains("--reset"))) {
			resetDatabase = true;
		}

		// Start service
		String authenticationSalt = config.getProperty("authentication.salt");
		String generalPassword = config.getProperty("authentication.general_password");
		Server server = new Server(resetDatabase, authenticationSalt, generalPassword);
		new Thread(server).start();
	}
}
