package ar.edu.itba.pdc.proxy;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;


public class ProxyConnection {

	private static final int BUFFER_SIZE = 4096;
	
	private SocketChannel server = null;
	private SocketChannel client = null;
	
	private Map<SocketChannel, ChannelBuffers> buffersMap = new HashMap<SocketChannel, ChannelBuffers>();
	
	public ProxyConnection(SocketChannel server, SocketChannel client) {
		this(client);
		setServer(server);
	}
	
	public ProxyConnection(SocketChannel client) {
		this.client = client;
		buffersMap.put(client, new ChannelBuffers(ByteBuffer.allocate(BUFFER_SIZE), ByteBuffer.allocate(BUFFER_SIZE)));
	}
	
	public void setServer(SocketChannel server) {
		this.server = server;
		buffersMap.put(server, new ChannelBuffers(ByteBuffer.allocate(BUFFER_SIZE), ByteBuffer.allocate(BUFFER_SIZE)));
	}
	
	public SocketChannel getServerChannel() {
		return server;
	}
	
	public SocketChannel getClientChannel() {
		return client;
	}
	
	public void expandBuffer(SocketChannel s, BufferType type) {
		ChannelBuffers buffers = buffersMap.get(s);
		if (type == BufferType.read) {
			ByteBuffer buf = buffers.getReadBuffer();
			buf.flip();
			buffers.setReadBuffer(ByteBuffer.allocate(buf.capacity() * 2).put(buf));
		} else {
			ByteBuffer buf = buffers.getWriteBuffer();
			buf.flip();
			buffers.setWriteBuffer(ByteBuffer.allocate(buf.capacity() * 2).put(buf));
		}
	}
	
	public ByteBuffer getBuffer(SocketChannel s, BufferType bufType) {
		if (buffersMap.get(s) == null)
			return null;
		if (bufType == BufferType.read)
			return buffersMap.get(s).getReadBuffer();
		else
			return buffersMap.get(s).getWriteBuffer();
	}
	
	public boolean hasClient() {
		return client != null;
	}
	
	public boolean hasInformationForChannel(SocketChannel s) {
		return buffersMap.get(s) != null && buffersMap.get(s).getWriteBuffer().capacity() != buffersMap.get(s).getWriteBuffer().remaining();
	}
	
	public boolean hasServer() {
		return server != null;
	}
	
	public int writeTo(SocketChannel s) throws IOException {
		int bytesWrote = 0;
		if (hasInformationForChannel(s)) {
			ChannelBuffers channelBuffers = buffersMap.get(s);
			if (channelBuffers != null && channelBuffers.getWriteBuffer().hasRemaining()) {
				channelBuffers.getWriteBuffer().flip();
				bytesWrote = s.write(channelBuffers.getWriteBuffer());
				System.out.println("Bytes escritos: " + bytesWrote);
				channelBuffers.getWriteBuffer().clear();
			}
		}
		return bytesWrote;
	}
	
	public int readFrom(SocketChannel s) throws IOException {
		/*
		 * Ver que hago con reads mas grandes que el buffer.
		 * Inicialmente es transparente, ya que va a volver a entrar con otro read
		 * Mas adelante hay que ver como procesar esos paquetes.
		 */
		
		int bytesRead = s.read(buffersMap.get(s).getReadBuffer());
		
		if (bytesRead == -1) {
			/* EOF - Hay que cerrar el canal */
		} else if (bytesRead > 0) {
			/* Aca vemos q hacemos */
		}
		/* Ver logica aca */
		if (s == client) {
			System.out.println("Leido del cliente: " + new String(buffersMap.get(s).getReadBuffer().array()));
			buffersMap.get(s).synchronizeBuffers(buffersMap.get(server));
		} else {
			System.out.println("Leido del servidor: " + new String(buffersMap.get(s).getReadBuffer().array()));
			buffersMap.get(s).synchronizeBuffers(buffersMap.get(client));
		}
		
		return bytesRead;
	}
}
