import java.net.SocketException;

public class Main {
	public static void main(String[] args) {
		Server server;
		try {
			server = new Server(8999);
			server.run();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
}
