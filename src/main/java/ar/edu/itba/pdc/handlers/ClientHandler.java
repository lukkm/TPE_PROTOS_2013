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
import ar.edu.itba.pdc.filters.Filter;
import ar.edu.itba.pdc.filters.Multiplexing;
import ar.edu.itba.pdc.filters.SilentUsersFilter;
import ar.edu.itba.pdc.filters.StatisticsFilter;
import ar.edu.itba.pdc.filters.TransformationFilter;
import ar.edu.itba.pdc.interfaces.TCPHandler;
import ar.edu.itba.pdc.jabber.Message;
import ar.edu.itba.pdc.parser.XMPPParser;
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
		filterList.add(new StatisticsFilter());
		filterList.add(new TransformationFilter());
	}

	/*
	 * Ver ConcurrentHashMap para ver que socket fue con cada thread
	 */

	public void accept(SocketChannel channel) throws IOException {
		connections.put(channel, new ProxyConnection(channel));
	}

	public SocketChannel read(SelectionKey key) throws IOException {

		SocketChannel s = (SocketChannel) key.channel();

		ProxyConnection connection = connections.get(s);

		SocketChannel serverChannel = null;

		if (!connection.hasConnectedServer()) {
			if (!connection.connected()) {				
				connection.handleConnectionStanza(s);
				if (connection.readyToConnectToServer()) {					
					/* Aca hay que hacer un get del server channel antes de conectarlo */
					String username = connection.getClientUsername();
					serverChannel = SocketChannel.open();
					String serverToConnect = Multiplexing.getInstance().getUserServer(username);
					System.out.println("---------------------------------------------------------------------");
					System.out.println("Connecting to: " + serverToConnect);
					System.out.println("---------------------------------------------------------------------");
					serverChannel.connect(new InetSocketAddress(serverToConnect, 5222));
					connection.setServerName("jabber.org");
					serverChannel.configureBlocking(false);
					serverChannel.register(selector, SelectionKey.OP_READ);
					connection.setServer(serverChannel);
					connection.writeFirstStreamToServer();
					connections.put(serverChannel, connection);
				} 
			}
			updateSelectionKeys(connection);
			return serverChannel;
			/* Hasta aca */
		} else {

			/* Perform the read operation */
			int bytes = connection.readFrom(s);
	
			if (bytes != -1) {
				
				/* Parse what was just read */
				List<Stanza> stanzaList = null;
				
				try {
					stanzaList = parser.parse(connection.getBuffer(s, BufferType.read));
					for (Stanza stanza : stanzaList) {
						if (stanza.getElement() != null && connection.connected())
							if (stanza.getElement().getFrom() == null && s == connection.getClientChannel())
									stanza.getElement().setFrom(connection.getClientJID());
						
						for (Filter f : filterList)
							f.apply(stanza);
		
						boolean rejected = false;
						
						if (stanza.isMessage()) {
							Message msg = (Message) stanza.getElement();
		
							rejected = (msg.getFrom().contains(connection.getClientJID()) || msg
									.getTo().contains(connection.getClientJID()))
									&& stanza.isrejected();
						
							if (rejected && connection.getClientChannel() == s)
								connection.send(s, stanza);
						}
						
						if (!rejected)
							sendToOppositeChannel(connection, s, stanza);
					
					}
					updateSelectionKeys(connection);
					connection.getBuffer(s, BufferType.read).clear();
					return null;
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
				} catch (IncompleteElementsException e) {
					connection.expandBuffer(s, BufferType.read);
				}
				
			} else {
				key.cancel();
			}
	
			return serverChannel;
		}
		
	}

	public void write(SelectionKey key) throws IOException {
		ProxyConnection connection = connections.get(key.channel());
		connection.writeTo((SocketChannel) key.channel());
		updateSelectionKeys(connection);
	}

	private void updateSelectionKeys(ProxyConnection connection)
			throws ClosedChannelException {
		if (connection.hasServer()) {
			if (connection.hasInformationForChannel(connection
					.getServerChannel())) {
				connection.getServerChannel().register(selector,
						SelectionKey.OP_READ | SelectionKey.OP_WRITE);
			} else {
				connection.getServerChannel().register(selector,
						SelectionKey.OP_READ);
			}
		}
		if (connection.hasClient()) {
			if (connection.hasInformationForChannel(connection
					.getClientChannel())) {
				connection.getClientChannel().register(selector,
						SelectionKey.OP_READ | SelectionKey.OP_WRITE);
			} else {
				connection.getClientChannel().register(selector,
						SelectionKey.OP_READ);
			}
		}
	}

	public void sendToOppositeChannel(ProxyConnection connection, SocketChannel s, Stanza stanza) {
		if (s == connection.getClientChannel()) {
			connection.send(
					connection.getServerChannel(), stanza);
		} else {
			connection.send(
					connection.getClientChannel(), stanza);
		}
	}

}