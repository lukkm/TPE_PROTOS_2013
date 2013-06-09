package ar.edu.itba.pdc.handlers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import ar.edu.itba.pdc.exceptions.IncompleteElementsException;
import ar.edu.itba.pdc.interfaces.TCPHandler;
import ar.edu.itba.pdc.jabber.JIDConfiguration;
import ar.edu.itba.pdc.jabber.Message;
import ar.edu.itba.pdc.jabber.Presence;
import ar.edu.itba.pdc.parser.XMPPParser;
import ar.edu.itba.pdc.processor.Filter;
import ar.edu.itba.pdc.processor.SilentUsersFilter;
import ar.edu.itba.pdc.processor.TransformationFilter;
import ar.edu.itba.pdc.proxy.BufferType;
import ar.edu.itba.pdc.proxy.ProxyConnection;
import ar.edu.itba.pdc.stanzas.Stanza;

public class ClientHandler implements TCPHandler {

	private Map<SocketChannel, ProxyConnection> connections;
	private XMPPParser parser;
	private Selector selector;
	private List<Filter> filterList;
	
	public ClientHandler(Selector selector) {
		this.selector = selector;
		this.connections = new HashMap<SocketChannel, ProxyConnection>();
		this.parser = new XMPPParser();
		this.filterList = new LinkedList<Filter>();
		initialize();
	}
	
	private void initialize() {
		filterList.add(new SilentUsersFilter());
		//filterList.add(new StatisticsFilter());
		//filterList.add(new TransformationFilter());
	}
	
	/*
	 * Ver ConcurrentHashMap para ver que socket fue con cada thread	
	 */
	
	public void accept(SocketChannel channel) throws IOException {
    	connections.put(channel, new ProxyConnection(channel));
	}

	public SocketChannel read(SelectionKey key) throws IOException {
		
		SocketChannel s = (SocketChannel)key.channel();
		
		ProxyConnection connection = connections.get(s);

		SocketChannel serverChannel = null;
		
		if (!connection.hasServer()) {
			
			/* A implementar bien dependiendo del read que haga */
	    	
			serverChannel = SocketChannel.open();
	        serverChannel.connect(new InetSocketAddress("hermes.jabber.org", 5222));
	        serverChannel.configureBlocking(false);
	        serverChannel.register(selector, SelectionKey.OP_READ);
	        connection.setServer(serverChannel);
	        connections.put(serverChannel, connection);
	        
	        /* Hasta aca */	        
		}
		
		/* Perform the read operation */
		int bytes = connection.readFrom(s);
		
		/* Parse what was just read */
		List<Stanza> stanzaList = null;
		try {
			stanzaList = parser.parse(connection.getBuffer(s, BufferType.read), connection.getStoredBytes() + bytes);
			connection.clearStoredBytes();
			for (Stanza stanza : stanzaList) {
				if (stanza.isMessage()) {
					Message msg = (Message)stanza.getElement();
					if (msg.getFrom() == null)
						msg.setFrom(connection.getClientJID());
				}
				for (Filter f : filterList) {
					f.apply(stanza);
				}
				if (stanza.isMessage()) {
					if (((Message)stanza.getElement()).getTo().contains(connection.getClientJID())) {
						if (stanza.rejected()) {							
							sendMessage(s, connection, stanza);
						} else {
							if (s == connection.getClientChannel()) {
								sendMessage(connection.getServerChannel(), connection, stanza);
							} else {
								sendMessage(connection.getClientChannel(), connection, stanza);
							}							
						}
						updateSelectionKeys(connection);
						return null;
					}
				}
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IncompleteElementsException e) {
			connection.expandBuffer(s, BufferType.read);
			connection.storeBytes(bytes);
		}
		
		if (stanzaList != null) {
			for (Stanza stanza : stanzaList) {
				if (stanza.isMessage()) {
					Message message = (Message)stanza.getElement();
					System.out.println("<--------------------------- MESSAGE --------------------------->");
					System.out.println("From: " + message.getFrom());
					System.out.println("To: " + message.getTo());
					System.out.println("Body: " + message.getMessage());
					System.out.println("<--------------------------------------------------------------->");
				} else if (stanza.isJIDConfiguration()) {
					JIDConfiguration jid = (JIDConfiguration)stanza.getElement();
					connection.setClientJID(jid.getJID());
					System.out.println("<--------------------------- JID CONFIGURATION --------------------------->");
					System.out.println("JID: " + jid.getJID());
					System.out.println("<------------------------------------------------------------------------->");
				} else if (stanza.isPresence()) {
					Presence presence = (Presence)stanza.getElement();
					System.out.println("<--------------------------- PRESENCE --------------------------->");
					System.out.println("From: " + presence.getFrom());
					System.out.println("To: " + presence.getTo());
					System.out.println("Type: " + presence.getType());
					System.out.println("<---------------------------------------------------------------->");
				}
			}
		}
		
		if (!connection.hasStoredBytes()) {
			connection.synchronizeChannelBuffers(s);
			updateSelectionKeys(connection);
		}

		return serverChannel;
		
	}

	public void write(SelectionKey key) throws IOException {
		ProxyConnection connection = connections.get(key.channel());
		connection.writeTo((SocketChannel)key.channel());
		updateSelectionKeys(connection);
	}
	
	private void updateSelectionKeys(ProxyConnection configuration) throws ClosedChannelException {
		if (configuration.hasInformationForChannel(configuration.getServerChannel())) {
			configuration.getServerChannel().register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
		} else {
			configuration.getServerChannel().register(selector, SelectionKey.OP_READ);
		}
		
		if (configuration.hasInformationForChannel(configuration.getClientChannel())) {
			configuration.getClientChannel().register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
		} else {
			configuration.getClientChannel().register(selector, SelectionKey.OP_READ);
		}
	}
	
	private void sendMessage(SocketChannel s, ProxyConnection conn, Stanza stanza) {
		Message stanzaMessage = (Message)stanza.getElement();
		String message = "<message from='" 
				+ stanzaMessage.getFrom() 
				+ "' to='" 
				+ stanzaMessage.getTo() + "' type='chat'>" 
				+ "<body>"
				+ stanzaMessage.getMessage()
				+ "</body>"
				+ "</message>";
		conn.appendToBuffer(s, BufferType.write, message.getBytes());
	}

}
