package edu.hm.cs.fs.presence.server;

import edu.hm.cs.fs.presence.common.PresenceInformation;
import edu.hm.cs.fs.presence.common.Util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.Socket;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Handles one connected client.
 * 
 * @author Thomas Maier
 */
public class ClientHandler implements Runnable {
	private static final Logger LOGGER = LogManager.getLogger(ClientHandler.class.getName());
	private DatabaseConnection dbConnection;
	private Socket clientSocket;
	private String generalHash;
	private String authenticationSalt;

	/**
	 * C'tor.
	 * 
	 * @param dbConnection Database connection
	 * @param clientSocket Client socket
	 * @param generalHash Hash to authenticate clients
	 */
	public ClientHandler(DatabaseConnection dbConnection, Socket clientSocket, String generalHash, String authenticationSalt) {
		this.dbConnection = dbConnection;
		this.clientSocket = clientSocket;
		this.generalHash = generalHash;
		this.authenticationSalt = authenticationSalt;
	}

	public void run() {
		try {
			// Read data from client
			BufferedReader socketReader = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
			StringBuffer request = new StringBuffer();
			String userInput;
			while ((userInput = socketReader.readLine()) != null) {
				request.append(userInput);
			}
			JAXBContext context = JAXBContext.newInstance(new Class[] { PresenceInformation.class });
			Unmarshaller unmarshaller = context.createUnmarshaller();
			PresenceInformation presenceInfo = (PresenceInformation) unmarshaller.unmarshal(new StringReader(request.toString()));

			// Handle received data
			if (presenceInfo != null) {
				String plaintext = this.generalHash + this.authenticationSalt + presenceInfo.getNickName();

				// Authenticate user
				String userHash = Util.generateAuthenticationHash(plaintext);
				if (userHash.equals(presenceInfo.getPassword())) {
					// Persist data
					synchronized (this.dbConnection) {
						this.dbConnection.insertPresenceData(presenceInfo.getNickName(), presenceInfo.getStatus(), this.clientSocket.getInetAddress().getHostAddress());
					}
				} else {
					LOGGER.warn("User authentication failed for: " + presenceInfo);
				}
			}
		} catch (IOException e) {
			LOGGER.error("Cannot receive data from client.");
			try {
				this.clientSocket.close();
			} catch (IOException ex) {
				LOGGER.error(ex.getMessage());
			}
		} catch (JAXBException e) {
			LOGGER.error("Client sent malformed XML data.");
			try {
				this.clientSocket.close();
			} catch (IOException ex) {
				LOGGER.error(ex.getMessage());
			}
		} finally {
			try {
				this.clientSocket.close();
			} catch (IOException e) {
				LOGGER.error(e.getMessage());
			}
		}
	}
}
