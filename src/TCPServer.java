import java.net.*;

import java.io.IOException;
import java.util.Hashtable;
import java.util.LinkedList;

public class TCPServer extends Thread {
	
	private Hashtable<String, UserAgent> userList;
	private Boolean isRunning = false;
	private ServerSocket serverSocket;
	
	public TCPServer(int serverPort) {
		try {
			InetAddress localAddress = InetAddress.getLocalHost();
			serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress(localAddress, serverPort));
			userList = new Hashtable<String, UserAgent>();
			isRunning = true;
			System.out.println("Server ready.");
		} catch (IOException e) {
			System.err.println("Port " + serverPort + " already in use!");
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		System.out.println("Server is running.");
		Socket socket = null;
		
		while (isRunning) {
			try {
				socket = serverSocket.accept();
				System.out.println("New connection from " + socket.getInetAddress() + ":" + socket.getPort());
				
				TCPEndPoint endPoint = new TCPEndPoint(socket);
				UserAgent agent = new UserAgent(this, endPoint);
				agent.start();
			} catch (IOException e) {
				System.err.println("Unknown connection failed to load.");
			}
		}
	}

	public void sendTell(String username, String content) {
		content = content.substring("/tell".length());
		String[] strs = content.trim().split(" ", 2);
		if (strs.length >= 2) {
			if (userList.containsKey(strs[0])) {
				strs[1] = username + " (whisper): " + strs[1]; 
				if (unicast(strs[0], strs[1])) {
					unicast(username, strs[1]);
				}
			} else {
				unicast(username, "Specified user does not exist");
			}
		} else {
			unicast(username, "Incorrect usage of command /tell");
		}
	}

	public Boolean unicast(String username, String message) {
		try {
			return unicast(userList.get(username), message, true);
		} catch (NullPointerException e) {
			System.err.println("Tried unicasting to non-existent user: " + username);
			return false;
		}
	}
	
	public Boolean unicast(UserAgent user, String message, Boolean shouldDelete) {
		user.sendMessage(message);
		System.out.println("[to " + user.getAddressAsString() + "] " + message);
		return true;
	}
	
	public void listUsers(String username) {
		StringBuilder sb = new StringBuilder();
		sb.append("Users online: \n");
		for(UserAgent user : userList.values()) {
			sb.append("\t");
			sb.append(user.getDisplayName());
			sb.append("\n");
		}
		unicast(username, sb.toString());
	}

	private void removeUser(String username) {
		userList.remove(username);
	}

	public void addUser(UserAgent agent, String[] strs) {
		if (!userList.containsKey(strs[0])) {
			userList.put(agent.getDisplayName(), agent);
			broadcast("User connected: " + agent.getDisplayName(), null);
		}
	}
	
	public void broadcast(String message, UserAgent shouldIgnore) {
		LinkedList<String> list = new LinkedList<String>();
		System.out.println("(Broadcasting...) " + message);
		for(UserAgent user : userList.values()) {
			if (user != shouldIgnore) {
				unicast(user, message, false);				
			}
		} 
		cleanUserList(list);
	}
	
	private void cleanUserList(LinkedList<String> list) {
		for(String name : list) {
			userList.remove(name);
		}
	}

	public boolean hasUser(String username) {
		return userList.containsKey(username);
	}

	public void leave(UserAgent userAgent, boolean sendResponse) {
		UserAgent ignore = sendResponse ? userAgent : null;
		broadcast(userAgent.getDisplayName() + " has left the building", ignore);
		removeUser(userAgent.getDisplayName());
		
		if (sendResponse) userAgent.dismiss(); // tell client it can leave gracefully
	}
}
