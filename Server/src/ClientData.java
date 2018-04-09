import java.net.InetAddress;

public class ClientData {
	private int port;
	private int chunkSize;
	private String filename;
	private InetAddress address;
	
	ClientData(int port, int chunkSize, InetAddress address, String filename) {
		this.port = port;
		this.chunkSize = chunkSize;
		this.address = address;
		this.filename = filename;
	}
	
	public int getPort() {
		return port;
	}
	
	public int getChunkSize() {
		return chunkSize;
	}
	
	public String getFilename(){
		return filename;
	}
	
	public InetAddress getAddress() {
		return address;
	}
}
