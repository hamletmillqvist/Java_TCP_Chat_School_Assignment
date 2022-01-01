import java.util.ArrayList;

public class Driver {
	
	public static void main(String[] args) {
		final int serverPort = 5000;
		
		ArrayList<Thread> client_threads = new ArrayList<Thread>();
		
		TCPServer server = new TCPServer(5000);
		server.start();
		
		client_threads.add(new TCPClient("Grognak_the_Destroyer", serverPort, true));
		client_threads.add(new TCPClient("Joe_Labero", serverPort, true));
		client_threads.add(new TCPClient("Loof_Pemla", serverPort, true));
		client_threads.add(new TCPClient("Yade_Hoho", serverPort, true));
		
		for (int i = 0; i < client_threads.size(); i++) {
			client_threads.get(i).start();
		}
		
		boolean shouldQuit;
		do {
			shouldQuit = true;
			for (int i = 0; shouldQuit && i < client_threads.size(); i++) {
				if (client_threads.get(i).isAlive()) {
					shouldQuit = false;
				}
			}
		} while (!shouldQuit);
		
		System.exit(0);
	}
	
	public static String getControlChar() {
		return "1";
	}
}
