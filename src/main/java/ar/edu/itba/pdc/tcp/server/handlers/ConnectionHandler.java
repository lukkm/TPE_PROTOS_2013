package ar.edu.itba.pdc.tcp.server.handlers;

import java.io.IOException;
import java.net.Socket;

public interface ConnectionHandler {

	 public void handle(Socket s) throws IOException;
	
}
