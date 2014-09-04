package edu.hm.cs.fs.presence.server;

import edu.hm.cs.fs.presence.common.Util;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Server to handle client connections.
 * 
 * @author Thomas Maier
 *
 */
public class Server implements Runnable {
	private static final Logger LOGGER = LogManager.getLogger(Server.class);

	/** Used TCP port for incoming connections. */
	private static final int TCP_PORT = 65535;

	/**
	 * Flag to decide if the database has to be resetted before starting the
	 * service.
	 */
	private boolean resetDatabase;

	private String authenticationSalt;

	private String generalPassword;

	/**
	 * C'tor.
	 * 
	 * @param resetDatabase
	 *            Flag to decide if the database has to be resetted before
	 *            starting the service.
	 */
	public Server(boolean resetDatabase, String authenticationSalt, String generalPassword) {
		this.resetDatabase = resetDatabase;
		this.authenticationSalt = authenticationSalt;
		this.generalPassword = generalPassword;
	}

	public void run() {
		// Connect to database
		DatabaseConnection dbConnection = new DatabaseConnection();

		if (resetDatabase)
			// Reset database to start with an empty database
			dbConnection.dropDatabase();

		dbConnection.createDatabaseIfNotExists();

		ServerSocket serverSocket = null;
		try {
			// Set up server socket with SSL/TLS
			ServerSocketFactory serverSocketFactory = SSLServerSocketFactory.getDefault();
			serverSocket = serverSocketFactory.createServerSocket(TCP_PORT);

			// Generate hash to authenticate users
			String generalHash = Util.generateAuthenticationHash(this.generalPassword);
			while (!Thread.currentThread().isInterrupted()) {
				try {
					// Wait for client connection
					Socket clientSocket = serverSocket.accept();

					// Handle client
					ClientHandler clientHandler = new ClientHandler(dbConnection, clientSocket, generalHash, this.authenticationSalt);
					new Thread(clientHandler).start();
				} catch (IOException e) {
					LOGGER.error(e.getMessage());
				}
			}
		} catch (IOException e1) {
			LOGGER.error(e1.getMessage());
		} finally {
			if (serverSocket != null && !serverSocket.isClosed()) {
				try {
					serverSocket.close();
				} catch (IOException e) {
					LOGGER.error(e.getMessage());
				}
			}
		}
	}
}
