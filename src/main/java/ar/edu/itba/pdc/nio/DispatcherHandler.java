package ar.edu.itba.pdc.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ar.edu.itba.pdc.handlers.AdminHandler;
import ar.edu.itba.pdc.handlers.ClientHandler;
import ar.edu.itba.pdc.handlers.TCPHandler;

public class DispatcherHandler {
	private static final int TIMEOUT = 3000;

	private Map<AbstractSelectableChannel, TCPHandler> handlerMap;

	public DispatcherHandler() {
		handlerMap = new HashMap<AbstractSelectableChannel, TCPHandler>();
	}

	/**
	 * Receives incoming events from open sockets and passes them to the
	 * available handlers. Should they return a new SocketChannel, it adds it to
	 * the selector with the default settings (OP_READ).
	 * 
	 * @throws IOException
	 */

	public void run() throws IOException {
		Selector selector = Selector.open();

		/* Create handlers */
		ClientHandler clientHandler = new ClientHandler(selector);
		AdminHandler adminHandler = new AdminHandler(selector);

		/* Bind client socket */
		ServerSocketChannel clientChannel = ServerSocketChannel.open();
		clientChannel.socket().bind(new InetSocketAddress(5678));
		clientChannel.configureBlocking(false);
		clientChannel.register(selector, SelectionKey.OP_ACCEPT);
		handlerMap.put(clientChannel, clientHandler);

		/* Bind admin socket */
		ServerSocketChannel adminChannel = ServerSocketChannel.open();
		adminChannel.socket().bind(new InetSocketAddress(5679));
		adminChannel.configureBlocking(false);
		adminChannel.register(selector, SelectionKey.OP_ACCEPT);
		handlerMap.put(adminChannel, adminHandler);

		while (!Thread.interrupted()) {
			if (selector.select(TIMEOUT) == 0) 
				continue;

			Iterator<SelectionKey> keyIter = selector.selectedKeys().iterator();
			while (keyIter.hasNext()) {
				SelectionKey key = keyIter.next();
				if (key.isAcceptable()) {
					SocketChannel newChannel = ((ServerSocketChannel) key
							.channel()).accept();
					newChannel.configureBlocking(false);
					newChannel.register(selector, SelectionKey.OP_READ);
					TCPHandler handler = handlerMap.get(key.channel());
					handlerMap.put(newChannel, handler);
					handler.accept(newChannel);
				}

				if (key.isReadable()) {
					SocketChannel channel = handlerMap.get(key.channel()).read(
							key);
					if (channel != null)
						handlerMap.put(channel, handlerMap.get(key.channel()));
				}

				if (key.isValid() && key.isWritable()) {
					handlerMap.get(key.channel()).write(key);
				}
				keyIter.remove();
			}
		}
	}
}