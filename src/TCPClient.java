import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.*;

public class TCPClient extends Thread implements ActionListener, WindowListener {

	private class MessageInformation {
		public String content = "";
		public long time = 0;
	}
	private MessageInformation lastMessage = new MessageInformation();
	
	private String displayName;
	private ChatGUI gui;
	private TCPEndPoint tcpEndPoint;
	private Boolean isConnected = false;
	
	public TCPClient(String displayName, int serverPort, Boolean connectAutomatically) {
		this.displayName = displayName;
		this.gui = new ChatGUI(this, this, displayName);
		InetAddress localAddress = null;
		try {
			localAddress = InetAddress.getLocalHost();
			gui.displayMessage("Checking server availability...");
			this.tcpEndPoint = new TCPEndPoint(localAddress, serverPort);
			gui.displayMessage("Server found: " + tcpEndPoint.getSourceString());
			isConnected = true;
		} catch (UnknownHostException e) {
			gui.displayMessage("Failed to read local address! Check your network card");
		} catch (IOException e) {
			gui.displayMessage("Could not connect to server: " + localAddress.toString() + ":" + String.valueOf(serverPort));
		}
		if (connectAutomatically) {
			sendMessage("/handshake");
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(isConnected) {
			lastMessage.content = gui.getInput();
			lastMessage.time = System.nanoTime();
			sendMessage(lastMessage.content);
			gui.clearInput();
		}
	}
	
	@Override
	public void run() {
		while (isConnected) {
			try {
				String message = tcpEndPoint.readMessage();
				if (message.equalsIgnoreCase("DISCONNECT_CLIENT")) {
					isConnected = false; 
				} else {
					if (message.startsWith(displayName)) { // response from last sent message
						float rtt = (System.nanoTime() - lastMessage.time) / 1000000f;
						message += "\t[RTT]: " + String.valueOf(rtt) + "ms";
					}
					gui.displayMessage(message);					
				}
			} catch (IOException e) {
				gui.displayMessage("Connection to server lost");
			}
		}
	}
	
	private void sendMessage(String message) {
		try {
			tcpEndPoint.sendMessage(displayName + Driver.getControlChar() + message);
		} catch (IOException e1) {
			gui.displayMessage("Connection closed unexpectedly!");
		}
	}


	@Override
	public void windowClosed(WindowEvent e) {
		this.sendMessage("/leave");
	}
	
	// WHY DOES A WINDOW LISTENER "HAVE" TO IMPLEMENT ALL THESE METHODS WHEN IT ONLY WANTS TO OVERRIDE ONE!?=!?!?!?!?!
	// Oracle needs to look at the SOLID development pattern...

	@Override public void windowIconified(WindowEvent e) {}
	@Override public void windowDeiconified(WindowEvent e) {}
	@Override public void windowActivated(WindowEvent e) {}
	@Override public void windowDeactivated(WindowEvent e) {}
	@Override public void windowOpened(WindowEvent e) {}
	@Override public void windowClosing(WindowEvent e) {}
}