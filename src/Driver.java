public class Driver {
	
	public static void main(String[] args) {
		final int serverPort = 5000;
		
		new TCPServer(5000).start();
		new TCPClient("Grognak_the_Destroyer", serverPort, true).start();
		new TCPClient("Joe_Labero", serverPort, true).start();
		new TCPClient("Loof_Pemla", serverPort, true).start();
		new TCPClient("Yade_Hoho", serverPort, true).start();
	}
	
	public static String getControlChar() {
		return "1";
	}
}
