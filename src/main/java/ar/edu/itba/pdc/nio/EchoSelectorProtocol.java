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
    private ByteBuffer pendingInformation;
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
        System.out.println(key.channel());
        if (bytesRead == -1) {
            clntChan.close();
        } else if (bytesRead > 0) {
        	System.out.println(new String(buf.array()));
        	serverConnector.communicate(buf);
        	this.clientSelectionKey = key;
            key.interestOps(SelectionKey.OP_READ);
        }
    }

    public void handleWrite(SelectionKey key) throws IOException {

        //ByteBuffer buf = (ByteBuffer) key.attachment();
        pendingInformation.flip();
        SocketChannel clntChan = (SocketChannel) key.channel();
        clntChan.write(pendingInformation);
        if (!pendingInformation.hasRemaining()) { 
        	hasInformation = false;
            key.interestOps(SelectionKey.OP_READ);
        }
        pendingInformation.compact(); 
    }

	@Override
	public void communicate(ByteBuffer message) {
		if (clientSelectionKey != null) {
			System.out.println("mensaje piola de juanjo:" + new String(message.array()));
			if (hasInformation)
				return; /* TODO Avisar q esta bardeando */
			hasInformation = true;
			pendingInformation = message;
			clientSelectionKey.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
		} else {
			System.out.println("Mensaje irrelevante de juanjo: " + new String(message.array()));
		}
	}
}