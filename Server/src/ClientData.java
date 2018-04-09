import java.net.InetAddress;

public class ClientData {
	private int port;
	private int chunkSize;
	private int chunkCount;
	private int sessionId;
	private InetAddress address;
	
	public ClientData(int port, int chunkSize, int sessionId, InetAddress address) {
		this.port = port;
	}
	
	public int getPort() {
		return port;
	}
	
	public int getChunkSize() {
		return chunkSize;
	}
	
	public int getChunkCount() {
		return chunkCount;
	}
	
	public void setChunkCount(int chunkCount) {
		this.chunkCount = chunkCount;
	}
	
	public int getSessionId() {
		return sessionId;
	}
	
	public InetAddress getAddress() {
		return address;
	}
}
