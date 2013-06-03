package ar.edu.itba.pdc.factories;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import ar.edu.itba.pdc.interfaces.Factory;

public class ClientFactory implements Factory {

	@Override
	public void create(SelectionKey key) {
        try {
        	
        	/* Bindeo cliente */
        	SocketChannel clientChannel = ((ServerSocketChannel) key.channel()).accept();
        	clientChannel.configureBlocking(false); 
			clientChannel.register(key.selector(), SelectionKey.OP_READ);
			
			/* Bindeo servidor */
			SocketChannel serverChannel = SocketChannel.open();
	        serverChannel.connect(new InetSocketAddress("hermes.jabber.org", 5222));
	        serverChannel.configureBlocking(false);
	        serverChannel.register(key.selector(), SelectionKey.OP_READ);
	        
		} catch (ClosedChannelException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void register(Selector selector) {
		
		
	}

}
