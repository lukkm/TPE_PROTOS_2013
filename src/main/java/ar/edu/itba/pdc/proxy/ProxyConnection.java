package ar.edu.itba.pdc.proxy;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

import ar.edu.itba.pdc.utils.ChannelBuffers;

public class ProxyConnection {

	private static final int BUFFER_SIZE = 1024;
	
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
	
	public boolean hasClient() {
		return client != null;
	}
	
	public boolean hasInformationForChannel(SocketChannel s) {
		return buffersMap.get(s) != null && buffersMap.get(s).getWriteBuffer().capacity() != buffersMap.get(s).getWriteBuffer().remaining();
	}
	
	public boolean hasServer() {
		return server != null;
	}
	
	public void writeTo(SocketChannel s) throws IOException {
		if (hasInformationForChannel(s)) {
			ChannelBuffers channelBuffers = buffersMap.get(s);
			if (channelBuffers != null && channelBuffers.getWriteBuffer().hasRemaining()) {
				/*if (s == server) 
					System.out.println("Escrito al server: " + new String(channelBuffers.getWriteBuffer().array()));
				else 
					System.out.println("Escrito al cliente: " + new String(channelBuffers.getWriteBuffer().array()));
				*/ /* Ver porque mierda no se puede hacer el .array() */
				channelBuffers.getWriteBuffer().flip();
				int bytesWrote = s.write(channelBuffers.getWriteBuffer());
				System.out.println("Bytes escritos: " + bytesWrote);
				channelBuffers.getWriteBuffer().clear();
			}
		}
	}
	
	public void readFrom(SocketChannel s) throws IOException {
		/*
		 * En lugar de usar listas de buffers, usar 2 buffers read/write
		 * para cada socket y sincronizarlos con los 2 del servidor.
		 * Ver bien los metodos flip(), compact(), mark(), etc...
		 */
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
			System.out.println("Leido del cliente: " + bytesRead);
			buffersMap.get(s).synchronizeBuffers(buffersMap.get(server));
		} else {
			System.out.println("Leido del servidor: " + bytesRead);
			buffersMap.get(s).synchronizeBuffers(buffersMap.get(client));
		}
	}
}
