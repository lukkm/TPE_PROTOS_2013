package ar.edu.itba.pdc.handlers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import ar.edu.itba.pdc.interfaces.TCPHandler;
import ar.edu.itba.pdc.parser.XMPPParser;
import ar.edu.itba.pdc.proxy.BufferType;
import ar.edu.itba.pdc.proxy.ProxyConnection;
import ar.edu.itba.pdc.stanzas.Stanza;

public class ClientHandler implements TCPHandler {

	private Map<SocketChannel, ProxyConnection> connections;
	private XMPPParser parser;
	private Selector selector;
	
	public ClientHandler(Selector selector) {
		this.selector = selector;
		connections = new HashMap<SocketChannel, ProxyConnection>();
		this.parser = new XMPPParser();
	}
	
	/*
	 * Ver ConcurrentHashMap para ver que socket fue con cada thread	
	 */
	
	@Override
	public void accept(SocketChannel channel) throws IOException {
    	connections.put(channel, new ProxyConnection(channel));
	}

	@Override
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
		try {
			List<Stanza> stanzaList = parser.parse(connection.getBuffer(s, BufferType.read), bytes);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		
		updateSelectionKeys(connection);
		return serverChannel;
		
	}

	@Override
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

}
