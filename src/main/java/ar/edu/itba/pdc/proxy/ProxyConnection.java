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
import ar.edu.itba.pdc.logger.XMPPLogger;
import ar.edu.itba.pdc.parser.XMPPParser;
import ar.edu.itba.pdc.proxy.enumerations.BufferType;
import ar.edu.itba.pdc.proxy.enumerations.ConnectionState;
import ar.edu.itba.pdc.stanzas.Stanza;

public class ProxyConnection {

	/* Socket Channels */
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
	
	private XMPPLogger logger = XMPPLogger.getInstance();

	/* Server Streams */
	protected static final byte[] INITIAL_SERVER_STREAM = ("<?xml version='1.0' ?><stream:stream xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams' version='1.0'>")
			.getBytes();
	protected static final byte[] NEGOTIATION = ("<stream:features><mechanisms xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\"><mechanism>PLAIN</mechanism></mechanisms><auth xmlns=\"http://jabber.org/features/iq-auth\"/></stream:features>")
			.getBytes();

	/* Every socket channel has its own read and write buffers */
	private Map<SocketChannel, ChannelBuffers> buffersMap = new HashMap<SocketChannel, ChannelBuffers>();

	private ConnectionState state;

	private List<Filter> filterList;
	private XMPPParser parser;

	public ProxyConnection(SocketChannel server, SocketChannel client) {
		this(client);
		setServer(server);
	}

	public ProxyConnection(SocketChannel client) {
		logger.info("Connected Client");
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

	public SocketChannel getServerChannel() {
		return server;
	}

	public SocketChannel getClientChannel() {
		return client;
	}

	/**
	 * Sets the server name before connecting to it.
	 * 
	 * Also resolves the client JID using the username plus the server address
	 * to generate it's complete JID.
	 * 
	 * @param name
	 */

	public void setServerName(String name) {

		this.serverName = name;
		this.clientJID = clientUsername + "@" + serverName;
		logger.info("Client ID: " + clientJID);
		logger.info("To server: " + name);

	}

	/**
	 * Once the account multiplexing was resolved, sets the server to connect.
	 * 
	 * Also allocates a new ChannelBuffers object which will contain the read
	 * and write buffers related to this channel.
	 * 
	 * @param server
	 *            Server Socket Channel.
	 */

	public void setServer(SocketChannel server) {
		this.server = server;
		buffersMap.put(server, new ChannelBuffers());
	}

	/**
	 * Expands one of the channel related buffers.
	 * 
	 * @param s
	 * @param type
	 *            Can be read or write, depending on which buffer we want to
	 *            expand.
	 */

	public void expandBuffer(SocketChannel s, BufferType type) {
		ChannelBuffers buffers = buffersMap.get(s);
		buffers.expandBuffer(type);
		System.out.println("Quedo en el buffer : "
				+ new String(buffers.getBuffer(type).array()));
	}

	/**
	 * Returns a specific buffer from the connection.
	 * 
	 * @param s
	 * @param bufType
	 *            Can be read or write, depending on which buffer we want to
	 *            obtain
	 */

	public ByteBuffer getBuffer(SocketChannel s, BufferType bufType) {
		if (buffersMap.get(s) == null)
			return null;
		return buffersMap.get(s).getBuffer(bufType);
	}

	/**
	 * Returns true if the connection has an assigned socket channel for the
	 * client
	 */

	public boolean hasClient() {
		return client != null;
	}

	/**
	 * Returns true if the connection has an assigned socket channel for the
	 * server
	 */

	public boolean hasServer() {
		return server != null;
	}

	/**
	 * Returns the raw client username.
	 */

	public String getClientUsername() {
		return clientUsername;
	}

	/**
	 * Returns the client JID once it is well formed
	 * <strong>client_username@server_name</strong>
	 * 
	 * @return
	 */

	public String getClientJID() {
		return clientJID;
	}

	/**
	 * Returns true if the connection has an assigned socket channel for the
	 * server and has already established a connection (sent the initial
	 * connection stanzas and finished negotiating the features)
	 */

	public boolean hasConnectedServer() {
		return server != null && state == ConnectionState.connected;
	}

	/**
	 * Returns true if the connection with the client and the server has been
	 * established.
	 */

	public boolean connected() {
		return this.state == ConnectionState.connected;
	}

	/**
	 * Returns true if there's pending information in the write buffer of the
	 * socket channel received by parameter
	 * 
	 * @param s
	 */

	public boolean hasInformationForChannel(SocketChannel s) {
		return buffersMap.get(s) != null
				&& buffersMap.get(s).hasInformationFor(BufferType.write);
	}

	/**
	 * Performs a read operation from a given socket channel into it's
	 * respective read buffer contained in the ChannelBuffers object associated
	 * with it.
	 * 
	 * @param s
	 */

	private int read(SocketChannel s) throws IOException {
		int bytesRead = s.read(buffersMap.get(s).getBuffer(BufferType.read));

		if (bytesRead == -1) {
			client.close();
			server.close();
			return -1;
		}

		return bytesRead;
	}

	/**
	 * Performs a read operation and then applies the application's logic to
	 * process it and marshal it into a Stanza list. <a>(See Stanza class)</a>
	 * 
	 * First reads from the given socket channel.
	 * 
	 * Second, the bytes read are passed to the parser which will convert the
	 * xml stream into an object list to be processed by the proxy.
	 * 
	 * Finally, all proxy filters are applied resulting in a changed object
	 * list, every Stanza object resulting is then sent to the appropiate
	 * channel.
	 * 
	 * @param s
	 * @return
	 * @throws IOException
	 */

	public int readFrom(SocketChannel s) throws IOException {
		int bytesRead = read(s);
		
		process(bytesRead, s);
		
		return bytesRead;
	}
	
	private synchronized void process(int bytesRead, SocketChannel s) throws IOException {
		if (bytesRead > 0) {
			/* Codigo para borrar despues */
			if (s == client)
				System.out.println("Leido del cliente: "
						+ new String(buffersMap.get(s)
								.getBuffer(BufferType.read).array()).substring(
								0, buffersMap.get(s).getBuffer(BufferType.read)
										.position()));
			else
				System.out.println("Leido del server: "
						+ new String(buffersMap.get(s)
								.getBuffer(BufferType.read).array()).substring(
								0, buffersMap.get(s).getBuffer(BufferType.read)
										.position()));
			/* Hasta aca */
			/* Parse what was just read */
			List<Stanza> stanzaList = null;

			try {
				stanzaList = parser.parse(getBuffer(s, BufferType.read));
				for (Stanza stanza : stanzaList) {
					if (stanza.getElement() != null && connected())
						if (stanza.getElement().getFrom() == null
								&& s == client)
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
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (IncompleteElementsException e) {
				expandBuffer(s, BufferType.read);
			}
		}
	}

	/**
	 * Performs the write operation to a given socket channel
	 * 
	 * @param s
	 */

	public synchronized int writeTo(SocketChannel s) throws IOException {
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

	/**
	 * Appends information to the given buffer of the given channel specified by
	 * parameter.
	 * 
	 * @param s
	 * @param buffer
	 * @param bytes
	 */

	public void appendToBuffer(SocketChannel s, BufferType buffer, byte[] bytes) {
		ChannelBuffers buffers = buffersMap.get(s);
		buffers.writeToBuffer(buffer, bytes);
	}

	/**
	 * Sends the given stanza to the given channel.
	 * 
	 * Uses the getXMLString() method of the Stanza object to retrieve the XML
	 * text in order to send it as a byte array to the <i>send</i> method which
	 * will perform the operation.
	 * 
	 * @param s
	 * @param stanza
	 */

	public void send(SocketChannel s, Stanza stanza) {
		sendMessage(s, stanza.getXMLString().getBytes());
	}

	/**
	 * Sends a byte array over the given channel.
	 * 
	 * @param s
	 * @param bytes
	 */


	private void sendMessage(SocketChannel s, byte[] bytes) {
		appendToBuffer(s, BufferType.write, bytes);
		buffersMap.get(s).clearBuffer(BufferType.read);
	}

	/**
	 * Returns true if the negotiation with the client has already finished and
	 * the username was obtained.
	 */

	public boolean readyToConnectToServer() {
		return state == ConnectionState.ready;
	}

	/**
	 * Handles the negotiation with the client and the server before
	 * establishing the connection between them.
	 * 
	 * Uses a finite state machine (Check <code>ConnectionState.java</code> to
	 * see the states) to validate what stream was just sent/received.
	 * 
	 * @param s
	 * @throws IOException
	 */

	public void handleConnectionStanza(SocketChannel s) throws IOException {
		int length = read(s);
		String read = new String(buffersMap.get(s).getBufferArray(BufferType.read))
				.substring(0, length);
		System.out.println(read);
		switch (state) {
			case noState :
				if (read.startsWith("<?xml")) {
					if (read.contains("<stream")) {
						state = ConnectionState.negotiating;
						sendMessage(s, INITIAL_SERVER_STREAM);
						sendMessage(s, NEGOTIATION);
					} else {
						state = ConnectionState.waitingForStream;
					}
				}
				break;
			case waitingForStream :
				if (read.startsWith("<stream")) {
					state = ConnectionState.negotiating;
					sendMessage(s, INITIAL_SERVER_STREAM);
					sendMessage(s, NEGOTIATION);
				}
				break;
			case negotiating :
				if (read.startsWith("<auth")) {
					authorizationStream = read;
					byte[] data = Base64.decodeBase64(read.substring(
							read.indexOf(">") + 1, read.lastIndexOf("<"))
							.getBytes());
					String stringData = new String(data);
					this.clientUsername = stringData.substring(1,
							stringData.indexOf(0, 1));
					this.state = ConnectionState.ready;
					buffersMap.get(client).clearBuffer(BufferType.read);
				}
				break;
			case connectingToServer :
				if (read.contains("<stream")) {
					sendMessage(server, authorizationStream.getBytes());
					this.state = ConnectionState.connected;
				} else if (read.startsWith("<?xml")) {
					this.state = ConnectionState.waitingForServerFeatures;
				}
				break;
			case waitingForServerFeatures :
				if (read.startsWith("<stream:features")) {
					sendMessage(server, authorizationStream.getBytes());
					this.state = ConnectionState.connected;
				} else {
					this.state = ConnectionState.waitingForServerFeatures;
				}
				break;
			default:
				break;
		}
		buffersMap.get(s).clearBuffer(BufferType.read);
	}

	/**
	 * Writes the first stream to the server specifying the server name.
	 */

	public void writeFirstStreamToServer() {
		if (serverName != null) {
			String stream = INITIAL_STREAM
					+ "to='"
					+ serverName
					+ "' xml:lang=\"en\" xmlns:xml=\"http://www.w3.org/XML/1998/namespace\">";
			sendMessage(server, stream.getBytes());
			this.state = ConnectionState.connectingToServer;
		}
	}

	/**
	 * Sends a stanza to the opposite channel. If the client is the given socket
	 * channel, then the receiver will be the server, and the other way around.
	 * 
	 * @param s
	 * @param stanza
	 */

	private void sendToOppositeChannel(SocketChannel s, Stanza stanza) {
		send((s == client) ? server : client, stanza);
	}
}
