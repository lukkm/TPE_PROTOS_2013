package ar.edu.itba.pdc.controllers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

import ar.edu.itba.pdc.interfaces.TCPHandler;
import ar.edu.itba.pdc.proxy.ProxyConnection;

public class ClientHandler implements TCPHandler {

	private Map<SocketChannel, ProxyConnection> config;
	private Selector selector;
	
	public ClientHandler(Selector selector) {
		this.selector = selector;
		config = new HashMap<SocketChannel, ProxyConnection>();
	}
	
	/*
	 * Ver ConcurrentHashMap para ver que socket fue con cada thread	
	 */
	
	@Override
	public SocketChannel accept(SelectionKey key) throws IOException {
		SocketChannel clientChannel = ((ServerSocketChannel)key.channel()).accept();
    	clientChannel.configureBlocking(false);
    	clientChannel.register(selector, SelectionKey.OP_READ);
        
    	config.put(clientChannel, new ProxyConnection(clientChannel));
    	
    	return clientChannel;
	}

	@Override
	public SocketChannel read(SelectionKey key) throws IOException {
		
		ProxyConnection configuration = config.get(key.channel());
		
		SocketChannel serverChannel = null;
		
		if (!configuration.hasServer()) {
			
			/* A implementar bien dependiendo del read que haga */
	    	
			serverChannel = SocketChannel.open();
	        serverChannel.connect(new InetSocketAddress("hermes.jabber.org", 5222));
	        serverChannel.configureBlocking(false);
	        serverChannel.register(selector, SelectionKey.OP_READ);
	        configuration.setServer(serverChannel);
	        config.put(serverChannel, configuration);
	        
	        /* Hasta aca */	        
		}
		
		/* Perform the read operation */
		configuration.readFrom((SocketChannel)key.channel());
		updateSelectionKeys(configuration);
		return serverChannel;
		
	}

	@Override
	public void write(SelectionKey key) throws IOException {
		ProxyConnection configuration = config.get(key.channel());
		configuration.writeTo((SocketChannel)key.channel());
		updateSelectionKeys(configuration);
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
