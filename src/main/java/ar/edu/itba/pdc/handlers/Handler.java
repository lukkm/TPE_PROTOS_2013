package ar.edu.itba.pdc.handlers;

import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public abstract class Handler implements TCPHandler {

	private Selector selector;
	
	public Handler(Selector selector) {
		this.selector = selector;
	}
	
	protected void updateChannelKeys(boolean pendingInformation,
			SocketChannel channel) throws ClosedChannelException {
		if (pendingInformation)
			channel.register(selector, SelectionKey.OP_READ
					| SelectionKey.OP_WRITE);
		else
			channel.register(selector, SelectionKey.OP_READ);
	}
	
	protected void register(SocketChannel s, int ops) throws ClosedChannelException {
		s.register(selector, ops);
	}
	
}
