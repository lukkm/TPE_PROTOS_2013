package ar.edu.itba.pdc.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import ar.edu.itba.pdc.interfaces.CommunicationProtocol;

public class EchoSelectorProtocol implements TCPProtocol, CommunicationProtocol {
    private int bufSize; 
    private boolean hasInformation = false;
    private ByteBuffer pendingInformation = ByteBuffer.allocate(50000);
    private CommunicationProtocol serverConnector;
    private SelectionKey clientSelectionKey;
    
    public EchoSelectorProtocol(int bufSize) {
        this.bufSize = bufSize;
    }
    
    public void setServerConnector(CommunicationProtocol serverConnector) {
    	this.serverConnector = serverConnector;
    }

    public void handleAccept(SelectionKey key) throws IOException {
        SocketChannel clntChan = ((ServerSocketChannel) key.channel()).accept();
        clntChan.configureBlocking(false); 
        clntChan.register(key.selector(), SelectionKey.OP_READ, ByteBuffer.allocate(bufSize));
    }

    public void handleRead(SelectionKey key) throws IOException {
        SocketChannel clntChan = (SocketChannel) key.channel();
        ByteBuffer buf = (ByteBuffer) key.attachment();
        long bytesRead = clntChan.read(buf);
        if (bytesRead == -1) {
            clntChan.close();
        } else if (bytesRead > 0) {
        	System.out.println("Received from client: " + new String(buf.array()) + "\n");
    		serverConnector.communicate(ByteBuffer.wrap(buf.array(), 0, (int)bytesRead));
    		buf.clear();
    		this.clientSelectionKey = key;
    		key.interestOps(SelectionKey.OP_READ);
        }
    }

    public void handleWrite(SelectionKey key) throws IOException {

        //ByteBuffer buf = (ByteBuffer) key.attachment();
//        pendingInformation.flip();
        SocketChannel clntChan = (SocketChannel) key.channel();
        while(pendingInformation.hasRemaining()) {
        	System.out.println("Sending to client: " + new String(pendingInformation.array()) + "\n");
        	clntChan.write(pendingInformation);
    	}
        	//pendingInformation.clear();
        key.interestOps(SelectionKey.OP_READ);
//        pendingInformation.compact();
    }

	@Override
	public void communicate(ByteBuffer message) {
		if (clientSelectionKey != null) {
			pendingInformation.put(message);
			clientSelectionKey.interestOps(SelectionKey.OP_WRITE);
		} else {
			System.out.println("Mensaje irrelevante de juanjo: " + new String(message.array()));
		}
	}
}