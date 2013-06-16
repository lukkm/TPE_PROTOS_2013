package ar.edu.itba.pdc.handlers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

import ar.edu.itba.pdc.filters.Multiplexing;
import ar.edu.itba.pdc.interfaces.TCPHandler;
import ar.edu.itba.pdc.proxy.ProxyConnection;

public class ClientHandler implements TCPHandler {

	private Map<SocketChannel, ProxyConnection> connections;
	private Selector selector;
	

	public ClientHandler(Selector selector) {
		this.selector = selector;
		this.connections = new HashMap<SocketChannel, ProxyConnection>();
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
		} else {

			/* Perform the read operation */
			int bytes = connection.readFrom(s);
	
			if (bytes > 0) {
				updateSelectionKeys(connection);
			} else if (bytes == -1) {
				key.cancel();
			}
			
		}

		return serverChannel;
		
	}

	public void write(SelectionKey key) throws IOException {
		ProxyConnection connection = connections.get(key.channel());
		connection.writeTo((SocketChannel) key.channel());
		updateSelectionKeys(connection);
	}

	private void updateSelectionKeys(ProxyConnection connection)
			throws ClosedChannelException {
		if (connection.hasServer())
			updateChannelKeys(connection, connection.getServerChannel());
		if (connection.hasClient())
			updateChannelKeys(connection, connection.getClientChannel());
	}
	
	private void updateChannelKeys(ProxyConnection connection, SocketChannel channel) 
			throws ClosedChannelException {
		if (connection.hasInformationForChannel(channel)) 
			channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
		else
			channel.register(selector, SelectionKey.OP_READ);
	}

}