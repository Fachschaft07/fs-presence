package edu.hm.cs.fs.presence.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;

import javax.net.ssl.SSLSocketFactory;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import edu.hm.cs.fs.presence.jna.LinuxIdleTime;
import edu.hm.cs.fs.presence.jna.MacIdleTime;
import edu.hm.cs.fs.presence.jna.Win32IdleTime;

public class PresenceClient {
	
	private final String TAG = "PresenceClient";
	
	private final String SERVERIP = "192.168.60.253"; // 60.253, 2.146
	
	private final int PORT = 65535;
	
	private final long SEND_INTERVAL = 5000;
	
	private final String CONFIG_FILE = "config.txt";
	
	public final static String PRESENT = "Anwesend";
	
	public final static String BUSY = "Beschäftigt";
	
	private final PresenceWindow presenceWindow;
	
	private final PresenceInformation pi;
	
	private String status = "Present";
	
	private String name = "FSler";
	
	private String password = "";
	
	private PresenceClientThread presenceClientThread;
	
	private boolean userActive = true;
	
	private long idleInterval;
	
	private long idleTime = 0;
	
	public PresenceClient(final PresenceWindow presenceWindow) {
		readConfig();
		this.presenceWindow = presenceWindow;
		pi = new PresenceInformation();
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(final String status) {
		this.status = status;
	}
	
	public boolean isPresentSelected() {
		return status.equals(PRESENT);
	}
	
	public boolean isBusySelected() {
		return status.equals(BUSY);
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}
	
	public void setIdleInterval(final int interval) {
		idleInterval = interval * 1000;
	}
	
	public int getIdleInterval() {
		return (int) idleInterval / 1000;
	}
	
	public void setPassword(final String password) {
		this.password = password;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void saveConfig() {
		System.out.println(TAG + " - Save Config");
		File file = new File(CONFIG_FILE);
		Writer w;
		try {
			w = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(w);
			bw.write(getName());
			bw.newLine();
			bw.write(getStatus());
			bw.newLine();
			bw.write(String.valueOf(getIdleInterval()));
			bw.newLine();
			bw.write(getPassword());
			bw.flush();
			bw.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public void readConfig(){
		System.out.println(TAG + " - Read Config");
		File file = new File(CONFIG_FILE);
		Reader r;
		try {
			r = new FileReader(file);
			BufferedReader br = new BufferedReader(r);
			setName(br.readLine());
			setStatus(br.readLine());
			setIdleInterval(Integer.parseInt(br.readLine()));
			String line = br.readLine();
			setPassword(line);
			br.close();
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public boolean trigger() {
		if (presenceClientThread == null || !presenceClientThread.isAlive()) {
			System.out.println(TAG + " - start Thread: " + presenceClientThread);
			presenceClientThread = new PresenceClientThread(this);
			presenceClientThread.start();
			return true;
		} else if (presenceClientThread != null && presenceClientThread.isAlive()) {
			presenceClientThread.interrupt();
			return false;
		}
		return false;
	}
	
	public void start() {
		System.out.println(TAG + " - start Thread: " + presenceClientThread);
		if (presenceClientThread == null || !presenceClientThread.isAlive()) {
			presenceClientThread = new PresenceClientThread(this);
			presenceClientThread.start();
		}
	}
	
	public void stop() {
		System.out.println(TAG + " - stop Thread: " + presenceClientThread);
		if (presenceClientThread != null && presenceClientThread.isAlive()) {
			presenceClientThread.interrupt();
		}
	}

	/**
	 * The PresenceThread will send a PresenceInformation Object 
	 * to the SERVERIP.
	 * 
	 * @author René
	 */
	public class PresenceClientThread extends Thread {
		
		private final String TAG = "PresenceClientThread";
		
		private int interruptCounter = 0;
		private PresenceClient client;
		
		public PresenceClientThread(PresenceClient client) {
			this.client = client;
		}
		
		@Override
		public void run() {
			super.run();
			
			System.out.println(TAG + " - Client Thread Started");
			
			while (!isInterrupted()) {
				if (userActive) {
					System.out.println("Send PresenceInformation: " + name + ", " + status + ", " + idleTime);
					
					pi.setNickName(name);
					pi.setStatus(status);
					pi.setPassword(SecManager.buildSHAHash(client.password + SecManager.SALT + client.name));
					
		            try {
		            	sendSLL();
						interruptCounter = 0;
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println(e.getMessage());
						interruptCounter++;
						if (interruptCounter >= 3) {
							interrupt();
						} else {
							try {
								sleep(SEND_INTERVAL);
							} catch (InterruptedException exception) {
								System.out.println(exception.getMessage());
								interrupt();
							}
						}
					}
				} else {
					System.out.println("Send PresenceInformation aborted: " + status + ", " + idleTime);
				}
				
				try {
					sleep(SEND_INTERVAL);
				} catch (InterruptedException e) {
					System.out.println(e.getMessage());
					interrupt();
				}
				
				if (OSManager.isWindows()) {
					idleTime = Win32IdleTime.getIdleTimeMillisWin32();
				} else if (OSManager.isMac()) {
					idleTime = MacIdleTime.getIdleTimeMillis();
				} else if (OSManager.isUnix()) {
					idleTime = LinuxIdleTime.getIdleTimeMillis();
				}
				userActive = (idleTime > idleInterval) ? false : true;
			}
			presenceWindow.changeButtonActivateStatus(false);
			System.out.println(TAG + " - Client Thread Interrupted");
		}
		
		public void send() throws IOException, JAXBException {
			Socket socket;
        	socket = new Socket(SERVERIP, PORT);
			JAXBContext context;
			context = JAXBContext.newInstance(PresenceInformation.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
			m.marshal(pi, socket.getOutputStream());
			socket.close();
		}
		
		public void sendSLL() throws IOException, JAXBException {
			
			Socket socket = SSLSocketFactory.getDefault().createSocket(SERVERIP, PORT);
			JAXBContext context;
			context = JAXBContext.newInstance(PresenceInformation.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
			m.marshal(pi, socket.getOutputStream());
			socket.close();
		}
	}
}
