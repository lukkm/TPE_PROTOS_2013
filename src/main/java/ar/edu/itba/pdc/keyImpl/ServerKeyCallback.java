package ar.edu.itba.pdc.keyImpl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import ar.edu.itba.pdc.interfaces.CommunicationProtocol;
import ar.edu.itba.pdc.interfaces.KeyCallback;

public class ServerKeyCallback implements KeyCallback, CommunicationProtocol {

	private static final int BUFSIZE = 10000;
	private CommunicationProtocol serverCommunicator;
	private ByteBuffer buf = ByteBuffer.allocate(BUFSIZE);
	private ByteBuffer pendingInformation = ByteBuffer.allocate(BUFSIZE);
	
	public void setserverCallback(CommunicationProtocol serverCommunicator) {
		this.serverCommunicator = serverCommunicator;
	}
	
	@Override
	public void accept(SelectionKey key) {
		
	}

	@Override
	public void read(SelectionKey key) {
		try {
			
			long bytesRead;
			SocketChannel srvChan = (SocketChannel) key.channel();
	        bytesRead = srvChan.read(buf);
	        if (bytesRead == -1) {
	            srvChan.close();
	        } else if (bytesRead > 0) {
	        	System.out.println("Received from hermes: " + new String(buf.array()) + "\n");
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
		SocketChannel srvChan = (SocketChannel) key.channel();
        if (pendingInformation.hasRemaining())
        	System.out.println("Sending to hermes: " + new String(pendingInformation.array()) + "\n");
        while(pendingInformation.hasRemaining())
			try {
				srvChan.write(pendingInformation);
			} catch (IOException e) {
				e.printStackTrace();
			}
        //pendingInformation.clear();
        key.interestOps(SelectionKey.OP_READ);
//        pendingInformation.compact();
	}

	@Override
	public void communicate(ByteBuffer message) {
		pendingInformation.put(message);
	}

	@Override
	public void connect(SelectionKey key) {
		key.interestOps(SelectionKey.OP_WRITE);
	}

}