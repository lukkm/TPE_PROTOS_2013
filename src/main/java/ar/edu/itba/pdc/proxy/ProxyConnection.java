package ar.edu.itba.pdc.proxy;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

import ar.edu.itba.pdc.jabber.Message;
import ar.edu.itba.pdc.stanzas.Stanza;


public class ProxyConnection {
	
	private SocketChannel server = null;
	private SocketChannel client = null;
	
	private int storedBytes = 0;
	
	private String clientJID = null;
	
	/* Client Streams */
    protected static final String INITIAL_STREAM           = "<?xml version='1.0' ?><stream:stream xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams' version='1.0' ";
    
    /* Server Streams */
    protected static final byte[] INITIAL_SERVER_STREAM = ("<?xml version='1.0' ?><stream:stream xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams' version='1.0'>").getBytes();
    protected static final byte[] NEGOTIATION     = ("<stream:features><mechanisms xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\"><mechanism>PLAIN</mechanism></mechanisms><auth xmlns=\"http://jabber.org/features/iq-auth\"/></stream:features>").getBytes();
	
    private ConnectionState state;
    
	private Map<SocketChannel, ChannelBuffers> buffersMap = new HashMap<SocketChannel, ChannelBuffers>();
	
	public ProxyConnection(SocketChannel server, SocketChannel client) {
		this(client);
		setServer(server);
	}
	
	public ProxyConnection(SocketChannel client) {
		this.client = client;
		this.state = ConnectionState.noState;
		buffersMap.put(client, new ChannelBuffers());
	}
	
	public void setServer(SocketChannel server) {
		this.server = server;
		buffersMap.put(server, new ChannelBuffers());
	}
	
	public void storeBytes(int bytes) {
		this.storedBytes += bytes;
	}
	
	public void clearStoredBytes() {
		this.storedBytes = 0;
	}
	
	public int getStoredBytes() {
		return storedBytes;
	}
	
	public boolean hasStoredBytes() {
		return storedBytes > 0;
	}
	
	public SocketChannel getServerChannel() {
		return server;
	}
	
	public SocketChannel getClientChannel() {
		return client;
	}
	
	public void expandBuffer(SocketChannel s, BufferType type) {
		
		ChannelBuffers buffers = buffersMap.get(s);
		buffers.expandBuffer(type);
		System.out.println("Quedo en el buffer : " + new String(buffers.getBuffer(type).array()));
	}
	
	public ByteBuffer getBuffer(SocketChannel s, BufferType bufType) {
		if (buffersMap.get(s) == null)
			return null;
		return buffersMap.get(s).getBuffer(bufType);
	}
	
	public boolean hasClient() {
		return client != null;
	}
	
	public boolean hasInformationForChannel(SocketChannel s) {
		return buffersMap.get(s) != null && buffersMap.get(s).getBuffer(BufferType.write).capacity() != buffersMap.get(s).getBuffer(BufferType.write).remaining();
	}
	
	public boolean hasServer() {
		return server != null;
	}
	
	public int writeTo(SocketChannel s) throws IOException {
		int bytesWrote = 0;
		if (hasInformationForChannel(s)) {
			ChannelBuffers channelBuffers = buffersMap.get(s);
			if (channelBuffers != null && channelBuffers.getBuffer(BufferType.write).hasRemaining()) {
				System.out.println("Escribiendo");
				channelBuffers.getBuffer(BufferType.write).flip();
				bytesWrote = s.write(channelBuffers.getBuffer(BufferType.write));
				System.out.println("Bytes escritos: " + bytesWrote);
				channelBuffers.getBuffer(BufferType.write).clear();
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
		
		int bytesRead = s.read(buffersMap.get(s).getBuffer(BufferType.read));

		String message = new String(buffersMap.get(s).getBuffer(BufferType.read).array());
		
		if (s == client)
			System.out.println("Leido del cliente: " + message.substring(0, (bytesRead == -1) ? message.length() : bytesRead));
		else
			System.out.println("Leido del server: " + message.substring(0, (bytesRead == -1) ? message.length() : bytesRead));
		
		if (bytesRead == -1) {
			/* EOF - Hay que cerrar el canal */
		} else if (bytesRead > 0) {
			/* Aca vemos q hacemos */
		}
		/* Ver logica aca */
		
		return bytesRead;
	}
	
	public void synchronizeChannelBuffers(SocketChannel s) {
		if (s == client)
			buffersMap.get(s).synchronizeBuffers(buffersMap.get(server));
		else
			buffersMap.get(s).synchronizeBuffers(buffersMap.get(client));
	}

	public String getClientJID() {
		return clientJID;
	}
	
	public ConnectionState getState() {
		return state;
	}

	public void setClientJID(String clientJID) {
		this.clientJID = clientJID;
	}
	
	public void appendToBuffer(SocketChannel s, BufferType buffer, byte[] bytes) {
		ChannelBuffers buffers = buffersMap.get(s);
		buffers.writeToBuffer(buffer, bytes);
	}
	
	public void sendMessage(SocketChannel s, Stanza stanza) {
		Message message = (Message)stanza.getElement();
		sendMessage(s, message.getXMLMessage().getBytes());
	}
	
	private void sendMessage(SocketChannel s, byte[] bytes) {
		appendToBuffer(s, BufferType.write, bytes);
		buffersMap.get(s).clearBuffer(BufferType.read);
	}
	
	public boolean readyToConnectToServer() {
		return state == ConnectionState.ready;
	}
	
	public void handleConnectionStanza(SocketChannel s) throws IOException {
		readFrom(s);
		String read = new String(getBuffer(s, BufferType.read).array());
		System.out.println(read);
		switch (state) {
			case noState:
				if (read.startsWith("<xml")) {
					if (read.contains("<stream")) {
						state = ConnectionState.starting;		
						sendMessage(s, INITIAL_SERVER_STREAM);
					} else {
						state = ConnectionState.waitingForStream;
					}
				} else {
					/* ERROR */
				}
				break;
			case waitingForStream:
				if (read.startsWith("<stream"))
					state = ConnectionState.starting;
				else
					/* ERROR */
				break;
			case starting:
				break;
			case negotiating:
				break;
		}
	}
}
