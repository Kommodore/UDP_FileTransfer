import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Client {
	
	
	
	public static void main(String args[]) {

		int testsize = 512;
		String msg = "HSOSSTP_INITX;256;myfile.txt";
		DatagramSocket cSocket;
		
		byte[] sendData = new byte[testsize];
		
		try {
			
			InetAddress ipAddr = InetAddress.getByName("127.0.0.1");
			cSocket = new DatagramSocket();
			
			sendData = msg.getBytes();
			
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddr, 8999);
			cSocket.send(sendPacket);
			
			
			cSocket.close();
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
