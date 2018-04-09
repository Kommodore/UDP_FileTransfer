import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client implements ActionListener {

	private DatagramSocket cSocket;
	private ClientWindow window;

	private String sessionKey;

	public Client() {
		window = new ClientWindow();
		window.addCustomActionListener(this);
	}

	//NOTE: Das ist nur referenz für mich
	private void readAndWriteFile() {
		File in = new File("Client/src/myfile.txt");
		File out = new File("Client/src/out.txt");

		FileInputStream fis;
		FileOutputStream fos;
		byte[] test = new byte[256];

		if (in.exists() && out.exists()) {
			System.out.println("Ist Da!");
		}

		try {
			fis = new FileInputStream(in);
			fos = new FileOutputStream(out, true);

			int k = fis.read(test);

			fos.write(test, 0, k);
			fos.write(test, 0, k);
			fos.write(test, 0, k);
			fos.write(test, 0, k);

			fis.close();
			fos.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void startFileTransfer(String serverAddr, int port, int chunkSize, String fileName) {
		String action, param;
		InetAddress ipAddr;

		byte[] recData = new byte[256];

		String initConnection = "HSOSSTP_INITX;" + chunkSize + ";" + fileName;
		System.out.println("> " + initConnection);

		try {
			ipAddr = InetAddress.getByName(serverAddr);
			cSocket = new DatagramSocket();

			DatagramPacket sendPacket = new DatagramPacket(initConnection.getBytes(), initConnection.getBytes().length, ipAddr, port);
			cSocket.send(sendPacket);

			DatagramPacket recPacket = new DatagramPacket(recData, recData.length);
			cSocket.setSoTimeout(5000);
			cSocket.receive(recPacket);

			String recString = new String(recPacket.getData()).replaceAll("\0", "");
			System.out.println(recString);

			Scanner sc = new Scanner(recString);
			sc.useDelimiter(";");
			action = sc.next();
			param = sc.next();
			sc.close();

			switch (action) {
				case "HSOSSTP_ERROR":
					switch (param) {
						case "FNF":
							System.out.println("FILE NOT FOUND!");
							window.showError("The requested file was not found!", "FNF");
							break;
						case "CNF":
							System.out.println("CHUNK NOT FOUND!");
							window.showError("The requested chunk was not found!", "CNF");
							break;
						case "NOS":
							System.out.println("NO SESSION!");
							window.showError("Your session does not exist!", "NOS");
							break;
						default:
							System.out.println("UNKNOWN ERROR!");
							window.showError("An unkown error has occurred!", "UNKNOWN");
							break;
					}
					break;
				case "HSOSSTP_SIDXX":
					this.sessionKey = param;
					retrieveData();
					break;

				default:
					System.out.println("UNKNOWN ACTION!");
					window.showError("An unkown action was sent!", "UNKNOWN");
					break;
			}

		} catch (SocketTimeoutException e) {
			System.out.println("TIMED OUT!");
			window.showError("The request timed out!", "TIME OUT");
		} catch (UnknownHostException e) {
			System.out.println("UNKNOWN HOST!");
			window.showError("The IP address of the host could not be determined!", "UNKNOWN HOST");
			e.printStackTrace();
		} catch (SocketException e) {
			System.out.println("SOCKET ERROR!");
			window.showError("Could not create or access the socket!", "SOCKET ERROR");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		cSocket.close();
	}

	private void retrieveData(){
		System.out.println("SESSIONKEY: " + this.sessionKey);
		
		
		DatagramPacket recPacket = new DatagramPacket(buf, length)
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("submit")) {
			try {
				this.startFileTransfer(window.getIPAddr(), window.getPort(), window.getChunkSize(), window.getFileName());
			} catch (NumberFormatException nfe) {
				System.out.println("Could not read input!");
				window.showError("Could not read input!", "False Input");
			}
		}
	}

	public static void main(String args[]) {
		new Client();
	}

}
