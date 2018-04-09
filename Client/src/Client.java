import java.awt.Dimension;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.JFrame;

public class Client extends JFrame{
	
	private static final long serialVersionUID = 1L;
	private static final int PORT = 8999;
	
	private DatagramSocket cSocket;
	
	public Client(String serverAddr, int chunkSize, String fileName) {
		//initWindow();
		startFileTransfer(serverAddr, chunkSize, fileName);
	}
	
	private void initWindow() {
		this.setTitle("Client");
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(new Dimension(800, 600));
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
	private void startFileTransfer(String serverAddr, int chunkSize, String fileName){
		InetAddress ipAddr;
		
		byte[] recData = new byte[256];
		
		String initConnection = "HSOSSTP;" + chunkSize + ";" + fileName;
		System.out.println("> " + initConnection);
	
		
		try {
			ipAddr = InetAddress.getByName(serverAddr);
			cSocket = new DatagramSocket();
			
			DatagramPacket sendPacket = new DatagramPacket(initConnection.getBytes(), initConnection.getBytes().length, ipAddr, PORT);
			cSocket.send(sendPacket);

			DatagramPacket recPacket = new DatagramPacket(recData, recData.length);
			cSocket.receive(recPacket);
			
			String recString = new String(recPacket.getData()).replaceAll("\0", "");
			System.out.println(recString);
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		cSocket.close();
	}
	
	public static void main(String args[]) {
		Client client = new Client("127.0.0.1", 256, "myfile.txt");
	}
}
