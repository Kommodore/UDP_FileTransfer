import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
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

	private ClientWindow window;

	private DatagramSocket cSocket;
	private InetAddress serverAddr;
	private String sessionKey, fileName;
	private int port, chunkSize, currentChunk, recvChunkSize, recvHeaderSize, currentFile;

	private File file;
	private FileOutputStream fos;

	public Client() {
		window = new ClientWindow();
		window.addCustomActionListener(this);

		currentFile = 1;
	}

	public void startFileTransfer() {
		String recvString;
		byte[] recvData = new byte[this.chunkSize];

		DatagramPacket send, recv;

		String initConnection = "HSOSSTP_INITX;" + chunkSize + ";" + fileName;
		System.out.println("> " + initConnection);

		try {
			cSocket = new DatagramSocket();

			send = new DatagramPacket(initConnection.getBytes(), initConnection.getBytes().length, this.serverAddr, this.port);
			cSocket.send(send);
			recv = new DatagramPacket(recvData, this.chunkSize);
			cSocket.setSoTimeout(5000);
			cSocket.receive(recv);
			cSocket.setSoTimeout(0);

			recvString = new String(recv.getData()).replaceAll("\0", "");
			processMsg(recvString);

		} catch (SocketTimeoutException e) {
			System.out.println("TIMED OUT!");
			window.showError("The request timed out!", "TIME OUT");
		} catch (SocketException e) {
			System.out.println("SOCKET ERROR!");
			window.showError("Could not create or access the socket!", "SOCKET ERROR");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		cSocket.close();
	}

	private boolean processMsg(String recvString) {
		boolean result = true;
		String action, param1, param2, data;
		Scanner sc = new Scanner(recvString);
		sc.useDelimiter(";");
		action = sc.next();

		if (action.equals("HSOSSTP_ERROR")) {
			param1 = sc.next();
			
			switch (param1) {
				case "FNF":
					System.out.println("FILE NOT FOUND!\t" + param1);
					window.showError("The requested file was not found!", "FNF");
					break;
				case "CNF":
					System.out.println("CHUNK NOT FOUND\t" + param1);
					window.showError("The requested chunk was not found!", "CNF");
					break;
				case "NOS":
					System.out.println("NO SESSION!\t" + param1);
					window.showError("Your session does not exist!", "NOS");
					break;
				default:
					System.out.println("UNKNOWN ERROR!\t" + param1);
					window.showError("An unkown error has occurred!", "UNKNOWN");
					break;
			}
			result = false;
		} else if (action.equals("HSOSSTP_SIDXX")) {
			param1 = sc.next();
			this.sessionKey = param1;
			this.retrieveData();
		} else if (action.equals("HSOSSTP_DATAX")) {
			param1 = sc.next();
			param2 = sc.next();

			data = sc.next();

			recvChunkSize = Integer.parseInt(param2);
			System.out.println("Bytes recieved: " + recvChunkSize);

			try {
				this.fos.write(data.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
			System.out.println("UNKNOWN ACTION!");
			window.showError("An unkown action was sent!", "UNKNOWN");
			result = false;
		}

		sc.close();
		return result;
	}

	private void retrieveData() {
		String getDataMsg, data;
		byte[] recvData = new byte[this.chunkSize];

		int tries = 0;

		DatagramPacket send, recv;

		this.file = new File(currentFile + this.fileName);
		try {
			file.createNewFile();
			this.fos = new FileOutputStream(file, true);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		++currentFile;

		while (true) {
			System.out.println("Asking...");
			try {
				getDataMsg = "HSOSSTP_GETXX;" + this.sessionKey + ";" + this.currentChunk + ";";
				send = new DatagramPacket(getDataMsg.getBytes(), getDataMsg.getBytes().length, this.serverAddr, this.port);
				cSocket.send(send);
				recv = new DatagramPacket(recvData, this.chunkSize);
				cSocket.receive(recv);

				data = new String(recv.getData()).replaceAll("\0", "");

				if (!processMsg(data)) {
					--this.currentChunk;
					++tries;
					if (tries >= 5) {
						System.out.println("Could not retrieve data!");
						window.showError("Could not retrieve data!", "DATA ERROR");
						return;
					}
				}

				if (recvChunkSize < chunkSize - recvChunkSize) {
					System.out.println("DONE with just " + recvChunkSize + " bytes.");
					fos.flush();
					fos.close();
					return;
				}

				++this.currentChunk;

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("submit")) {
			try {
				this.serverAddr = InetAddress.getByName(window.getIPAddr());
				this.fileName = window.getFileName();
				this.port = window.getPort();
				this.chunkSize = window.getChunkSize();

				this.startFileTransfer();
			} catch (NumberFormatException nfe) {
				System.out.println("Could not read input!");
				window.showError("Could not read input!", "False Input");
			} catch (UnknownHostException uhe) {
				System.out.println("UNKNOWN HOST!");
				window.showError("The IP address of the host could not be determined!", "UNKNOWN HOST");
				uhe.printStackTrace();
			}
		}
	}

	public static void main(String args[]) {
		new Client();
	}

}
