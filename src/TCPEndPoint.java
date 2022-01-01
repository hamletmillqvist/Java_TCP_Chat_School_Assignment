import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;

public class TCPEndPoint {
	
	private Socket socket;
	
	public TCPEndPoint (InetAddress targetAddress, int targetPort) throws IOException {
		this.socket = new Socket(targetAddress, targetPort);
	}
	
	public TCPEndPoint (Socket socket) {
		this.socket = socket;

	}
	
	public InetAddress getAddress() {
		return socket.getInetAddress();
	}
	
	public int getPort() {
		return socket.getPort();
	}
	
	public String getSourceString() {
		return getAddress().toString() + ":" + String.valueOf(getPort());
	}
	
	public void sendMessage(String message) throws IOException {		
		OutputStream outputStream = socket.getOutputStream();
		DataOutputStream dataStream = new DataOutputStream(outputStream);

		dataStream.writeUTF(message);
	}
	
	public String readMessage() throws IOException {
		InputStream inStream = null;
		DataInputStream dataStream = null;
		
		inStream = socket.getInputStream();
		dataStream = new DataInputStream(inStream);

		return dataStream.readUTF();
	}
}
