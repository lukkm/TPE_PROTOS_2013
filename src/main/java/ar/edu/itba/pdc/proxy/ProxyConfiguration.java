package ar.edu.itba.pdc.proxy;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ProxyConfiguration {

	private static final int BUFFER_SIZE = 1000;
	
	private SocketChannel server = null;
	private SocketChannel client = null;
	
	private Map<SocketChannel, List<ByteBuffer>> queueMap = new HashMap<SocketChannel, List<ByteBuffer>>();
	
	public ProxyConfiguration(SocketChannel server, SocketChannel client) {
		this(client);
		setServer(server);
	}
	
	public ProxyConfiguration(SocketChannel client) {
		this.client = client;
		queueMap.put(client, new LinkedList<ByteBuffer>());
	}
	
	public void setServer(SocketChannel server) {
		this.server = server;
		queueMap.put(server, new LinkedList<ByteBuffer>());
	}
	
	public SocketChannel getServerChannel() {
		return server;
	}
	
	public SocketChannel getClientChannel() {
		return client;
	}
	
	public boolean hasClient() {
		return client != null;
	}
	
	public boolean hasInformationForChannel(SocketChannel s) {
		return queueMap.get(s) != null && !queueMap.get(s).isEmpty();
	}
	
	public boolean hasServer() {
		return server != null;
	}
	
	public void writeTo(SocketChannel s) throws IOException {
		if (hasInformationForChannel(s)) {
			List<ByteBuffer> socketQueue = queueMap.get(s);
			if (socketQueue != null && !socketQueue.isEmpty()) {
				System.out.println("Escribiendo: " + new String(socketQueue.get(0).array()));
				s.write(socketQueue.get(0));
				if (!socketQueue.get(0).hasRemaining())
					socketQueue.remove(0);
			}
		}
	}
	
	public void readFrom(SocketChannel s) throws IOException {
		ByteBuffer b = ByteBuffer.allocate(BUFFER_SIZE);
		int bytesRead = s.read(b);
		if (bytesRead == -1) {
			/* EOF - Hay que cerrar el canal */
		} else if (bytesRead > 0) {
			/* Unica logica a modificar */
			if (s == server) {
				queueMap.get(client).add(b);
			} else if (s == client) {
				queueMap.get(server).add(b);
			}
			System.out.println("Leido: " + new String(b.array()));
		}
	}
}