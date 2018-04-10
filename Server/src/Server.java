import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Scanner;

class Server {
	private DatagramSocket socket;
	private HashMap<Integer, ClientData> clients = new HashMap<>();
	private int total_sessions;

	Server(int port) throws SocketException {
		socket = new DatagramSocket(port);
	}

	void run() {
		byte[] buf = new byte[256];

		String action;
		int param1;
		String param2;

		while (true) {
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			try {
				socket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}

			Scanner recievedPacket = new Scanner(new String(packet.getData()));

			recievedPacket.useDelimiter(";");
			action = recievedPacket.next();
			param1 = recievedPacket.nextInt();
			param2 = recievedPacket.next().replaceAll("\0", "");

			recievedPacket.close();

			switch (action) {
				case "HSOSSTP_INITX":
					establishConnection(packet, param1, param2);
					break;
				case "HSOSSTP_GETXX":
					String response;
					if (!clients.containsKey(param1)) {
						response = "HSOSSTP_ERROR;NOS";
					} else {
						response = sendData(param1, Integer.parseInt(param2));
						System.out.println("Sending "+response);
					}

					DatagramPacket newPacket = new DatagramPacket(response.getBytes(), response.getBytes().length, packet.getAddress(), packet.getPort());
					try {
						socket.send(newPacket);
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;
				default:
					System.out.println("Bla123");
					break;
			}
		}
	}

	private String sendData(int sessionKey, int chunkNo) {
		try {
			FileInputStream fileInputStream = new FileInputStream(clients.get(sessionKey).getFilename());
			int maxChunkSize = clients.get(sessionKey).getChunkSize();
			String returnString = "HSOSSTP_DATAX;" + chunkNo + ";";

			if (fileInputStream.skip(clients.get(sessionKey).getBytesRead()) != -1) {
				byte fileContent[] = new byte[maxChunkSize - returnString.length() - 4];
				int readBytes;

				if ((readBytes = fileInputStream.read(fileContent, 0, maxChunkSize - returnString.length() - 4)) != -1) {
					fileInputStream.close();
					clients.get(sessionKey).addBytesRead(readBytes);
					return returnString + (readBytes+returnString.length() + 4) + ";" + (new String(fileContent));
				}
			}
			fileInputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "HSOSSTP_ERROR;CNF";
	}

	private void establishConnection(DatagramPacket packet, int chunkSize, String filename) {
		String response;
		File f = new File(filename);

		if (f.exists() && !f.isDirectory()) {
			clients.put(total_sessions++, new ClientData(packet.getPort(), chunkSize, packet.getAddress(), filename));
			response = "HSOSSTP_SIDXX;" + (total_sessions - 1);
		} else {
			response = "HSOSSTP_ERROR;FNF";
		}

		DatagramPacket newPacket = new DatagramPacket(response.getBytes(), response.getBytes().length, packet.getAddress(), packet.getPort());
		try {
			socket.send(newPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
