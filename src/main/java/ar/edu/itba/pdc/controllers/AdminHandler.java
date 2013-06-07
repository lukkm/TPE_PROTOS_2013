package ar.edu.itba.pdc.controllers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

import ar.edu.itba.pdc.interfaces.TCPHandler;
import ar.edu.itba.pdc.utils.ChannelBuffers;

public class AdminHandler implements TCPHandler {

	private Map<SocketChannel, ChannelBuffers> config;
	private Selector selector;

	public AdminHandler(Selector selector) {
		this.selector = selector;
		config = new HashMap<SocketChannel, ChannelBuffers>();
	}

	@Override
	public void accept(SocketChannel channel) throws IOException {
		config.put(channel, new ChannelBuffers());
	}

	@Override
	public SocketChannel read(SelectionKey key) throws IOException {
		SocketChannel s = (SocketChannel)key.channel();
		ChannelBuffers channelBuffers = config.get(s);
		s.read(channelBuffers.getReadBuffer());
		channelBuffers.autoSynchronizeBuffers();
		updateSelectionKeys(s);
		return null;
	}

	@Override
	public void write(SelectionKey key) throws IOException {
		SocketChannel s = (SocketChannel)key.channel();
		ByteBuffer wrBuffer = config.get(s).getWriteBuffer();
		wrBuffer.flip();
		s.write(wrBuffer);
		updateSelectionKeys(s);
		wrBuffer.compact();
	}

	private void updateSelectionKeys(SocketChannel s) throws ClosedChannelException {
		ChannelBuffers buffers = config.get(s);
		if (buffers.getWriteBuffer().capacity() != buffers.getWriteBuffer().remaining()) {
			s.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
		} else {
			s.register(selector, SelectionKey.OP_READ);
		}
	}
}
