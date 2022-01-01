import java.io.IOException;

public class UserAgent extends Thread implements Runnable {
	
	private TCPServer server;
	private TCPEndPoint tcpEndPoint;
	
	private String displayName;
	private Boolean isRunning, isDismissed = true;
	
	public UserAgent (TCPServer server, TCPEndPoint tcpEndPoint) {
		this.server = server;
		this.tcpEndPoint = tcpEndPoint;
	}
	
	@Override
	public void run () {	
		isRunning = handshake();
		while (isRunning) {
			try {
				String message = tcpEndPoint.readMessage();
				handleReceivedMessage(message);	
			} catch (IOException e) { // read message failed
				
			}
		}
		
		while (!isDismissed) {}
	}
	
	private Boolean handshake() {
		try {
			String message = tcpEndPoint.readMessage();
			String[] strs = message.split(Driver.getControlChar());
			
			if (strs[1].equalsIgnoreCase("/handshake")) {
				this.displayName = strs[0];
				server.addUser(this, strs);
				return true;
			} else {
				System.out.println("Connection from " + tcpEndPoint.getSourceString() + " was denied.");
				return false;
			}			
		} catch (IOException e) {
			System.out.println("A FUCK, I CAN'T BELIEVE YOU'VE DONE THIS");
			return false;
		}
	}

	private void handleReceivedMessage(String message) {
		String[] strs = message.split(Driver.getControlChar());
		
		if (server.hasUser(strs[0])) {
			System.out.println("[from " + getAddressAsString() + "] " + message);
			if (strs[1].startsWith("/")) {
				switch (strs[1]) {
				case "/list":
					server.listUsers(getDisplayName());
					break;
				case "/leave":
					server.leave(this, true);
					isRunning = false;
					isDismissed = false;
					break;
				default:
					if (strs[1].startsWith("/tell")) {
						server.sendTell(getDisplayName(), strs[1]);
					}
					break;
				}
			} else {
				server.broadcast(strs[0] + ": " + strs[1], null);
			}
		} else { // Non-existent user sent a message ???
			isRunning = false;
		}
	}
	
	public String getDisplayName() {
		return this.displayName;
	}
	
	public String getAddressAsString() {
		return tcpEndPoint.getSourceString();
	}
	
	public void sendMessage(String message) {
		try {
			this.tcpEndPoint.sendMessage(message);
		} catch (IOException e) { // socket dead
			server.leave(this, false);
		}
	}
	
	public void dismiss() { // graceful exit
		sendMessage("DISCONNECT_CLIENT");
		this.isDismissed = true;
	}
}
