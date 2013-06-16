package ar.edu.itba.pdc.handlers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

import ar.edu.itba.pdc.exceptions.BadSyntaxException;
import ar.edu.itba.pdc.parser.AdminParser;
import ar.edu.itba.pdc.proxy.ChannelBuffers;
import ar.edu.itba.pdc.proxy.enumerations.BufferType;

public class AdminHandler implements TCPHandler {

	private Map<SocketChannel, ChannelBuffers> config;
	private Selector selector;
	private AdminParser parser;

	public AdminHandler(Selector selector) {
		this.selector = selector;
		config = new HashMap<SocketChannel, ChannelBuffers>();
		parser = new AdminParser();
	}

	/**
	 * Handles incoming connections to admin port.
	 * 
	 * Creates a new ChannelBuffers object which will contain the read and write
	 * buffers related to the channel.
	 * 
	 */

	public void accept(SocketChannel channel) throws IOException {
		config.put(channel, new ChannelBuffers());
	}

	/**
	 * Handles incoming reads from administrators.
	 * 
	 * Parses the message and validates the syntax.
	 * 
	 */

	public SocketChannel read(SelectionKey key) throws IOException {
		SocketChannel s = (SocketChannel) key.channel();
		ChannelBuffers channelBuffers = config.get(s);
		int bytesRead = s.read(channelBuffers.getBuffer(BufferType.read));

		try {
			if (!parser.parseCommand(channelBuffers.getBuffer(BufferType.read),
					bytesRead))
				System.out.println("Mala sintaxis");
		} catch (BadSyntaxException e) {
			System.out.println("Bad syntax");
		} catch (Exception e) {
			System.out.println("Careta fixea");
		}
		// parseCommand(channelBuffers.getReadBuffer());
		// channelBuffers.autoSynchronizeBuffers();
		// String strCommand = new
		// String(channelBuffers.getReadBuffer().array());

		channelBuffers.getBuffer(BufferType.read).clear();
		updateSelectionKeys(s);
		return null;
	}

	/**
	 * Handles write operations.
	 * 
	 * Gets the buffer from the ChannelBuffers object and writes directly into
	 * it.
	 * 
	 */

	public void write(SelectionKey key) throws IOException {
		SocketChannel s = (SocketChannel) key.channel();
		ByteBuffer wrBuffer = config.get(s).getBuffer(BufferType.write);
		wrBuffer.flip();
		s.write(wrBuffer);
		updateSelectionKeys(s);
		wrBuffer.compact();
	}

	/**
	 * Updates selector keys for a specific connection.
	 * 
	 * Always sets the OP_READ flag, in case any endpoint wants to write
	 * something.
	 * 
	 * In case there's pending information in the write buffer for a specific
	 * channel, sets the channel OP_WRITE flag.
	 * 
	 * @param connection
	 * @throws ClosedChannelException
	 */

	private void updateSelectionKeys(SocketChannel s)
			throws ClosedChannelException {
		ChannelBuffers buffers = config.get(s);
		if (buffers.getBuffer(BufferType.write).capacity() != buffers
				.getBuffer(BufferType.write).remaining()) {
			s.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
		} else {
			s.register(selector, SelectionKey.OP_READ);
		}
	}
}
