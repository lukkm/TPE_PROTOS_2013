package ar.edu.itba.pdc.keyImpl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import ar.edu.itba.pdc.interfaces.CommunicationProtocol;
import ar.edu.itba.pdc.interfaces.KeyCallback;

public class ClientKeyCallback implements KeyCallback, CommunicationProtocol {

	private static final int BUFSIZE = 10000;
	private CommunicationProtocol serverCommunicator;
	private ByteBuffer buf = ByteBuffer.allocate(BUFSIZE);
	private ByteBuffer pendingInformation = ByteBuffer.allocate(BUFSIZE);
	private boolean hasInformation = false;
	private SelectionKey clientKey;
	
	public void setServerCallback(CommunicationProtocol serverCommunicator) {
		this.serverCommunicator = serverCommunicator;
	}
	
	@Override
	public void accept(SelectionKey key) {
		try {
			clientKey = key;
        	SocketChannel clntChan = ((ServerSocketChannel) key.channel()).accept();
        	clntChan.configureBlocking(false);
			clntChan.register(key.selector(), SelectionKey.OP_READ);
			buf = ByteBuffer.allocate(BUFSIZE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void read(SelectionKey key) {
		try {
			long bytesRead;
			SocketChannel clntChan = (SocketChannel) key.channel();	
	        bytesRead = clntChan.read(buf);
	        if (bytesRead == -1) {
	            clntChan.close();
	        } else if (bytesRead > 0) {
	        	System.out.println("Received from client: " + new String(buf.array()) + "\n");
	    		serverCommunicator.communicate(ByteBuffer.wrap(buf.array(), 0, (int)bytesRead));
	    		buf.clear();
	    		key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
	        }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void write(SelectionKey key) {
		  SocketChannel clntChan = (SocketChannel) key.channel();
	        System.out.println("Sending to client: " + new String(pendingInformation.array()) + "\n");
	        while(!hasInformation);
	        while(pendingInformation.hasRemaining())
				try {
					clntChan.write(pendingInformation);
				} catch (IOException e) {
					e.printStackTrace();
				}
	        //pendingInformation.clear();
	        hasInformation = false;
	        key.interestOps(SelectionKey.OP_READ);
//	        pendingInformation.compact();
	}

	@Override
	public void communicate(ByteBuffer message) {
		pendingInformation.put(message);
		hasInformation = true;
		clientKey.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
	}

}
