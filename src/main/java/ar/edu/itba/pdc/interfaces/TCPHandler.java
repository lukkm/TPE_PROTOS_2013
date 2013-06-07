package ar.edu.itba.pdc.interfaces;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public interface TCPHandler {

	void accept(SocketChannel channel) throws IOException;
	SocketChannel read(SelectionKey key) throws IOException;
    void write(SelectionKey key) throws IOException;
	
}
