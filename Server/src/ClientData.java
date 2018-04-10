import java.net.InetAddress;

public class ClientData {
	private int port;
	private int chunkSize;
	private int bytesRead;
	private String filename;
	private InetAddress address;
	
	ClientData(int port, int chunkSize, InetAddress address, String filename) {
		this.port = port;
		this.chunkSize = chunkSize;
		this.bytesRead = 0;
		this.address = address;
		this.filename = filename;
	}
	
	public int getPort() {
		return port;
	}
	
	public int getChunkSize() {
		return chunkSize;
	}
	
	public int getBytesRead() {
		return bytesRead;
	}
	
	public void addBytesRead(int b) {
		bytesRead += b;
	}
	
	public String getFilename(){
		return filename;
	}
	
	public InetAddress getAddress() {
		return address;
	}
}
