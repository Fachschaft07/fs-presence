package edu.hm.cs.fs.presence.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Handler for database connections.
 * 
 * @author Thomas Maier
 *
 */
public class DatabaseConnection {
	private static final Logger LOGGER = LogManager.getLogger(DatabaseConnection.class.getName());
	Connection dbConnection = null;

	public DatabaseConnection() {
		this.dbConnection = connect2Database();
	}

	/**
	 * Insert new client entry in database.
	 * 
	 * @param nickName
	 *            User nickname
	 * @param status
	 *            User status
	 * @param hostIp
	 *            Client IP
	 */
	public void insertPresenceData(String nickName, String status, String hostIp) {
		LOGGER.trace("Insert database record: " + nickName + ", " + status + ", " + hostIp + ", " + new Date());
		try {
			PreparedStatement statement = this.dbConnection.prepareStatement("insert into presence_data (nick_name, status, host_ip, receive_timestamp) values (?, ?, ?, CURRENT_TIMESTAMP)");
			statement.setString(1, nickName);
			statement.setString(2, status);
			statement.setString(3, hostIp);
			statement.executeUpdate();
			statement.close();
		} catch (SQLException e) {
			LOGGER.warn(e.getMessage());
		}
	}

	/**
	 * Create table which contains client data.
	 */
	public void createDatabaseIfNotExists() {
		LOGGER.info("Create database...");
		try {
			Statement statement = this.dbConnection.createStatement();
			statement.execute("create table if not exists presence_data (nick_name text not null, status text not null, host_ip text not null, receive_timestamp number not null)");
			statement.close();
		} catch (SQLException e) {
			LOGGER.warn(e.getMessage());
		}
	}

	/**
	 * Delete table which contains client data.
	 */
	public void dropDatabase() {
		LOGGER.info("Drop database...");
		try {
			Statement statement = this.dbConnection.createStatement();
			statement.execute("drop table if exists presence_data");
			statement.close();
		} catch (SQLException e) {
			LOGGER.warn(e.getMessage());
		}
	}

	/**
	 * Establishes new database connection.
	 * 
	 * @return New database connection
	 */
	private static Connection connect2Database() {
		LOGGER.info("Connect to database...");
		Connection connection = null;
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:db.sqlite", "", "");
		} catch (Exception e) {
			LOGGER.warn(e.getMessage());
		}
		return connection;
	}
}
