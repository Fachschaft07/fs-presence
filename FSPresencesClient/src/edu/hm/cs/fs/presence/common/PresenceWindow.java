package edu.hm.cs.fs.presence.common;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class PresenceWindow extends JFrame{
	
	private static final long serialVersionUID = -4393409417202369873L;
	
	private final static String keyStore = "client.jks";
	
	private final static String keyStorePW = "dhlm0V7CcxuhBcY9IfplS5ogJi5dwJ";
	
	private final JTextField nameField = new JTextField();
	
	private final JTextField idleField = new JTextField();
	
	private final JTextField passwordField = new JTextField();
	
	private final JButton buttonSave = new JButton("Save");
	
	private final JRadioButton presentRadio = new JRadioButton(PresenceClient.PRESENT);
	
	private final JRadioButton busyRadio = new JRadioButton(PresenceClient.BUSY);
	
	private final ButtonGroup buttonGroup = new ButtonGroup();
	
	private final JButton buttonActivate = new JButton("Start");
	
	private final PresenceClient presenceClient;
	
	private TrayIcon trayIcon;
	
	private SystemTray sysTray;
	
	private MenuItem openWindowItem;
	
	private MenuItem triggerItem;
	
	private boolean isTraySupported = false;
	
	private ActionListener buttonActiveListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			changeButtonActivateStatus(presenceClient.trigger());
		}
	};
	
	private ActionListener presentRadioListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			presenceClient.setStatus(PresenceClient.PRESENT);
		}
	};
	
	private ActionListener busyRadioListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			presenceClient.setStatus(PresenceClient.BUSY);
		}
	};
	
	private ActionListener buttonSaveListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			presenceClient.setName(nameField.getText());
			String password = passwordField.getText();
			if (!password.isEmpty()) {
				presenceClient.setPassword(SecManager.buildSHAHash(password));
			}
			passwordField.setText("");
			String idleTime = idleField.getText();
			if (idleTime.matches("[0-9]+")) {
				presenceClient.setIdleInterval(Integer.parseInt(idleTime));
				presenceClient.saveConfig();
			} else {
				idleField.setText(String.valueOf(presenceClient.getIdleInterval()));
				System.out.println("Wrong Format!");
			}
		}
	};
	
	public PresenceWindow(final int start) {
		
		System.setProperty("javax.net.ssl.trustStore", keyStore);
		System.setProperty("javax.net.ssl.trustStorePassword" , keyStorePW) ;
		
		setTitle("FSPresence");
		
		setIconImage(new ImageIcon(this.getClass().getResource("Fak07_Logo.png")).getImage());
		initializeTraySupport();
		presenceClient = new PresenceClient(this);
		setSize(230, 270);
		getContentPane().setBackground(Color.WHITE);
	    addWindowListener(new WindowAdapter() {
	    	@Override
	    	public void windowClosing( WindowEvent e) {
	    		
	    		if (isTraySupported) {
	    			handleVisibility();
				} else {
					System.exit(0);
				}
	    	}
		});
	    String row = "5dlu, pref, 2dlu, pref, 5dlu, pref, 2dlu, pref, 5dlu, pref, 5dlu, pref, 5dlu, pref, 10dlu, pref, 2dlu";
	    String col = "2dlu, pref, 2dlu, pref, 2dlu";
	    FormLayout formLayout = new FormLayout(col, row);
	    CellConstraints cc = new CellConstraints();
	    setLayout(formLayout);
	    add(new JTextArea("Name:"), cc.xy(2, 2));
	    nameField.setText(presenceClient.getName());
	    add(nameField, cc.xyw(2, 4, 3));
	    add(new JTextArea("Status:"), cc.xy(2, 6));
	    add(presentRadio, cc.xy(4, 6));
	    add(busyRadio, cc.xy(4, 8));
	    presentRadio.setSelected(presenceClient.isPresentSelected());
	    busyRadio.setSelected(presenceClient.isBusySelected());
	    presentRadio.setBackground(Color.WHITE);
	    busyRadio.setBackground(Color.WHITE);
	    buttonGroup.add(presentRadio);
	    buttonGroup.add(busyRadio);
	    presentRadio.addActionListener(presentRadioListener);
	    busyRadio.addActionListener(busyRadioListener);
	    add(new JTextArea("Idle Time (Sec):"), cc.xy(2, 10));
	    idleField.setHorizontalAlignment(JTextField.RIGHT);
	    idleField.setMaximumSize(new Dimension(50, 20));
	    idleField.setText(String.valueOf(presenceClient.getIdleInterval()));
	    add(idleField, cc.xy(4, 10));
	    add(new JTextArea("Password:"), cc.xy(2, 12));
	    add(passwordField, cc.xyw(2, 14, 3));
	    buttonSave.setPreferredSize(new Dimension(100, 20));
	    add(buttonSave, cc.xy(2, 16));
	    buttonSave.addActionListener(buttonSaveListener);
	    buttonActivate.setPreferredSize(new Dimension(100, 20));
	    add(buttonActivate, cc.xy(4, 16));
	    buttonActivate.addActionListener(buttonActiveListener);
	    setLocationRelativeTo(null);
	    setVisible(true);
	    if (start == 0) {
	    	changeButtonActivateStatus(presenceClient.trigger());
		}
	}
	
	public void changeButtonActivateStatus(final boolean statusRunning) {
		if (statusRunning) {
			buttonActivate.setText("Stop");
			triggerItem.setLabel("Stop");
		} else {
			buttonActivate.setText("Start");
			triggerItem.setLabel("Start");
		}
	}
	
	private void initializeTraySupport() {

		// System tray support
		if (OSManager.isWindows() && SystemTray.isSupported()) {
			isTraySupported = true;
			PopupMenu popup = new PopupMenu();

			// Build Open button in tray popup
			openWindowItem = new MenuItem("Minimize");
			ActionListener openWindowListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					handleVisibility();
				}
			};
			openWindowItem.addActionListener(openWindowListener);
			
			triggerItem = new MenuItem("Start");
			ActionListener triggerListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					changeButtonActivateStatus(presenceClient.trigger());
				}
			};
			triggerItem.addActionListener(triggerListener);
			
			// Build Exit button in tray popup
			MenuItem exitItem = new MenuItem("Exit");
			ActionListener exitListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			};
			exitItem.addActionListener(exitListener);
			
			popup.add(triggerItem);
			popup.add(openWindowItem);
			popup.add(exitItem);

			// Behavior
			sysTray = SystemTray.getSystemTray();
			Image image = new ImageIcon(this.getClass().getResource("Fak07_Logo.png")).getImage();
			trayIcon = new TrayIcon(image, "SystemTray Demo", popup);
			trayIcon.setImageAutoSize(true);
			trayIcon.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						handleVisibility();
					}
				}
			});
			try {
				sysTray.add(trayIcon);
			} catch (AWTException e1) {
				System.out.println("Could not add to tray");
			}
		} else {
			System.out.println("System tray not supported");
		}
	}

	private void handleVisibility() {
		if (isVisible()) {
			setVisible(false);
			openWindowItem.setLabel("Show");
		} else {
			setVisible(true);
			openWindowItem.setLabel("Minimize");
		}
	}
	
	public static void main(String[] args) {
		new PresenceWindow(0);
	}
}
