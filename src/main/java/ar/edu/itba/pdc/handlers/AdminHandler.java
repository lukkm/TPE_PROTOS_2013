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
import ar.edu.itba.pdc.logger.XMPPLogger;
import ar.edu.itba.pdc.parser.AdminParser;
import ar.edu.itba.pdc.proxy.ChannelBuffers;
import ar.edu.itba.pdc.proxy.enumerations.BufferType;

public class AdminHandler extends Handler {

	private Map<SocketChannel, ChannelBuffers> config;
	private AdminParser parser;
	private boolean logged = false;
	private XMPPLogger logger = XMPPLogger.getInstance();
	
	public AdminHandler(Selector selector) {
		super(selector);
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
		logger.info("New admin connected");
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
			String response;
			if ((response = parser.parseCommand(
					channelBuffers.getBuffer(BufferType.read), bytesRead)) != null) {
				if (logged || response.equals("PASSWORD OK\n")) {
					logged = true;
					s.write(ByteBuffer.wrap(response.getBytes()));
				} else if (response.equals("INVALID PASSWORD\n")){
					s.write(ByteBuffer.wrap(response.getBytes()));
				} else {
					s.write(ByteBuffer.wrap("Not logged in!\n".getBytes()));
				}
			}
		} catch (BadSyntaxException e) {
			System.out.println("Bad syntax");
			s.write(ByteBuffer.wrap("BAD SYNTAX\n".getBytes()));
		} catch (Exception e) {
			logged = false;
			logger.error("Lost connection with the admin");
			s.close();
			key.cancel();
			return null;
		}
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
		updateChannelKeys(buffers.hasInformationFor(BufferType.write), s);
	}
}
