package ar.edu.itba.pdc.proxy;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.codec.binary.Base64;

import ar.edu.itba.pdc.exceptions.IncompleteElementsException;
import ar.edu.itba.pdc.filters.Filter;
import ar.edu.itba.pdc.filters.SilentUsersFilter;
import ar.edu.itba.pdc.filters.StatisticsFilter;
import ar.edu.itba.pdc.filters.TransformationFilter;
import ar.edu.itba.pdc.jabber.Message;
import ar.edu.itba.pdc.parser.XMPPParser;
import ar.edu.itba.pdc.stanzas.Stanza;


public class ProxyConnection {
	
	private SocketChannel server = null;
	private SocketChannel client = null;
	
	/* Client connection parameters */
	private String clientJID = null;
	private String clientUsername = null;
	
	/* Client Streams */
	protected static final String INITIAL_STREAM = "<?xml version='1.0' ?><stream:stream xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams' version='1.0' ";

	/* Server connection parameters */
	private String serverName = null;
	private String authorizationStream = null;
    
    /* Server Streams */
    protected static final byte[] INITIAL_SERVER_STREAM = ("<?xml version='1.0' ?><stream:stream xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams' version='1.0'>").getBytes();
    protected static final byte[] NEGOTIATION     = ("<stream:features><mechanisms xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\"><mechanism>PLAIN</mechanism></mechanisms><auth xmlns=\"http://jabber.org/features/iq-auth\"/></stream:features>").getBytes();
	
    private ConnectionState state;
    
    private List<Filter> filterList;
	private XMPPParser parser;
    
    /* Every socket channel has its own read and write buffers */
    private Map<SocketChannel, ChannelBuffers> buffersMap = new HashMap<SocketChannel, ChannelBuffers>();
	
	public ProxyConnection(SocketChannel server, SocketChannel client) {
		this(client);
		setServer(server);
	}
	
	public ProxyConnection(SocketChannel client) {
		this.client = client;
		this.state = ConnectionState.noState;
		buffersMap.put(client, new ChannelBuffers());
	
		this.parser = new XMPPParser();
		this.filterList = new LinkedList<Filter>();
		initialize();
	}
	
	private void initialize() {
		filterList.add(SilentUsersFilter.getInstance());
		filterList.add(StatisticsFilter.getInstance());
		filterList.add(TransformationFilter.getInstance());
	}
	
	public void setServer(SocketChannel server) {
		this.server = server;
		buffersMap.put(server, new ChannelBuffers());
	}
	
	public SocketChannel getServerChannel() {
		return server;
	}
	
	public SocketChannel getClientChannel() {
		return client;
	}
	
	public void setServerName(String name) {
		this.serverName = name;
		this.clientJID = clientUsername + "@" + serverName;
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
		return buffersMap.get(s) != null && buffersMap.get(s).hasInformationFor(BufferType.write);
	}
	
	public boolean hasServer() {
		return server != null;
	}
	
	public boolean hasConnectedServer() {
		return server != null && state == ConnectionState.connected;
	}
	
	public int writeTo(SocketChannel s) throws IOException {
		int bytesWrote = 0;
		if (hasInformationForChannel(s)) {
			ChannelBuffers channelBuffers = buffersMap.get(s);
			if (channelBuffers != null && channelBuffers.hasRemainingFor(BufferType.write)) {
				channelBuffers.flipBuffer(BufferType.write);
				bytesWrote = s.write(channelBuffers.getBuffer(BufferType.write));
				channelBuffers.clearBuffer(BufferType.write);
			}
		}
		return bytesWrote;
	}
	
	private int read(SocketChannel s) throws IOException {
		int bytesRead = s.read(buffersMap.get(s).getBuffer(BufferType.read));

		if (bytesRead == -1) {
			client.close();
			server.close();
			return -1;
		} else {
			if (s == client) 
				System.out.println("Leido del cliente: " + new String(buffersMap.get(s).getBuffer(BufferType.read).array()).substring(0, buffersMap.get(s).getBuffer(BufferType.read).position()));
			else
				System.out.println("Leido del server: " + new String(buffersMap.get(s).getBuffer(BufferType.read).array()).substring(0, buffersMap.get(s).getBuffer(BufferType.read).position()));
		}

		return bytesRead;
	}
	
	public int readFrom(SocketChannel s) throws IOException {
		int bytesRead = read(s);
		if (bytesRead > 0) {
			if (s == client) 
				System.out.println("Leido del cliente: " + new String(buffersMap.get(s).getBuffer(BufferType.read).array()).substring(0, buffersMap.get(s).getBuffer(BufferType.read).position()));
			else
				System.out.println("Leido del server: " + new String(buffersMap.get(s).getBuffer(BufferType.read).array()).substring(0, buffersMap.get(s).getBuffer(BufferType.read).position()));
			/* Parse what was just read */
			List<Stanza> stanzaList = null;
			
			try {
				stanzaList = parser.parse(getBuffer(s, BufferType.read));
				for (Stanza stanza : stanzaList) {
					if (stanza.getElement() != null && connected())
						if (stanza.getElement().getFrom() == null && s == client)
								stanza.getElement().setFrom(getClientJID());
					
					for (Filter f : filterList)
						f.apply(stanza);
	
					boolean rejected = false;
					
					if (stanza.isMessage()) {
						Message msg = (Message) stanza.getElement();
	
						rejected = (msg.getFrom().contains(getClientJID()) || msg
								.getTo().contains(getClientJID()))
								&& stanza.isrejected();
					
						if (rejected && client == s)
							send(s, stanza);
					}
					
					if (!rejected)
						sendToOppositeChannel(s, stanza);
				
				}
				getBuffer(s, BufferType.read).clear();
				return bytesRead;
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (IncompleteElementsException e) {
				expandBuffer(s, BufferType.read);
			}
		}
		
		return bytesRead;
	}
	
	public String getClientJID() {
		return clientJID;
	}
	
	public void appendToBuffer(SocketChannel s, BufferType buffer, byte[] bytes) {
		ChannelBuffers buffers = buffersMap.get(s);
		buffers.writeToBuffer(buffer, bytes);
	}
	
	private void sendMessage(SocketChannel s, byte[] bytes) {
		appendToBuffer(s, BufferType.write, bytes);
		buffersMap.get(s).clearBuffer(BufferType.read);
	}
	
	public void send(SocketChannel s, Stanza stanza) {
		sendMessage(s, stanza.getXMLString().getBytes());
	}
	
	public boolean readyToConnectToServer() {
		return state == ConnectionState.ready;
	}
	
	public String getClientUsername() {
		return clientUsername;
	}
	
	public void handleConnectionStanza(SocketChannel s) throws IOException {
		int length = read(s);
		String read = new String(getBuffer(s, BufferType.read).array()).substring(0, length);
		System.out.println(read);
		switch (state) {
			case noState:
				if (read.startsWith("<?xml")) {
					if (read.contains("<stream")) {
						state = ConnectionState.negotiating;		
						sendMessage(s, INITIAL_SERVER_STREAM);
						sendMessage(s, NEGOTIATION);
					} else {
						state = ConnectionState.waitingForStream;
					}
				} else {
					/* ERROR */
				}
				break;
			case waitingForStream:
				if (read.startsWith("<stream")) {
					state = ConnectionState.negotiating;
					sendMessage(s, INITIAL_SERVER_STREAM);
					sendMessage(s, NEGOTIATION);
				} else {
					/* ERROR */
				}
				break;
			case negotiating:
				if (read.startsWith("<auth")) {
					authorizationStream = read;
					byte[] data = Base64.decodeBase64(read.substring(read.indexOf(">") + 1,
                            read.lastIndexOf("<")).getBytes());
					String stringData = new String(data);
					this.clientUsername = stringData.substring(1, stringData.indexOf(0, 1));	
					this.state = ConnectionState.ready;
					buffersMap.get(client).clearBuffer(BufferType.read);
				}
				break;
			case connectingToServer:
				if (read.contains("<stream")) {
					sendMessage(server, authorizationStream.getBytes());
					this.state = ConnectionState.connected;
				} else if (read.startsWith("<?xml")) {
					this.state = ConnectionState.waitingForServerFeatures;
				}
				break;
			case waitingForServerFeatures:
				if (read.startsWith("<stream:features")) {
					sendMessage(server, authorizationStream.getBytes());
					this.state = ConnectionState.connected;
				}
				break;
		}
	}
	
	public boolean connected() {
		return this.state == ConnectionState.connected;
	}
	
	public void writeFirstStreamToServer() {
		if (serverName != null) {
			String stream = INITIAL_STREAM + "to='" + serverName + "' xml:lang=\"en\" xmlns:xml=\"http://www.w3.org/XML/1998/namespace\">";
			sendMessage(server, stream.getBytes());
			this.state = ConnectionState.connectingToServer;
		}
	}
	
	public void sendToOppositeChannel(SocketChannel s, Stanza stanza) {
		if (s == client) 
			send(server, stanza);
		else 
			send(client, stanza);
	}
}
