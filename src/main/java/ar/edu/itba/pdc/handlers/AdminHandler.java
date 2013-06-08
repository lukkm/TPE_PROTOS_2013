package ar.edu.itba.pdc.handlers;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import ar.edu.itba.pdc.interfaces.TCPHandler;
import ar.edu.itba.pdc.parser.AdminParser;
import ar.edu.itba.pdc.proxy.ChannelBuffers;

public class AdminHandler implements TCPHandler {

	private Map<SocketChannel, ChannelBuffers> config;
	private Selector selector;
	private AdminParser parser;

	public AdminHandler(Selector selector) {
		this.selector = selector;
		config = new HashMap<SocketChannel, ChannelBuffers>();
	}

	public void accept(SocketChannel channel) throws IOException {
		config.put(channel, new ChannelBuffers());
	}

	public SocketChannel read(SelectionKey key) throws IOException {
		SocketChannel s = (SocketChannel) key.channel();
		ChannelBuffers channelBuffers = config.get(s);
		s.read(channelBuffers.getReadBuffer());

		if (!parser.parseCommand(channelBuffers.getReadBuffer()))
			System.out.println("Mala sintaxis");

		// parseCommand(channelBuffers.getReadBuffer());
		// channelBuffers.autoSynchronizeBuffers();
		// String strCommand = new
		// String(channelBuffers.getReadBuffer().array());

		channelBuffers.getReadBuffer().clear();
		updateSelectionKeys(s);
		return null;
	}

	public void write(SelectionKey key) throws IOException {
		SocketChannel s = (SocketChannel) key.channel();
		ByteBuffer wrBuffer = config.get(s).getWriteBuffer();
		wrBuffer.flip();
		s.write(wrBuffer);
		updateSelectionKeys(s);
		wrBuffer.compact();
	}

	private void updateSelectionKeys(SocketChannel s)
			throws ClosedChannelException {
		ChannelBuffers buffers = config.get(s);
		if (buffers.getWriteBuffer().capacity() != buffers.getWriteBuffer()
				.remaining()) {
			s.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
		} else {
			s.register(selector, SelectionKey.OP_READ);
		}
	}
}
