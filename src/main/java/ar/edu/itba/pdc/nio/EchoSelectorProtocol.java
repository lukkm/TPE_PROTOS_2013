package ar.edu.itba.pdc.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class EchoSelectorProtocol implements TCPProtocol {
    private int bufSize; 

    public EchoSelectorProtocol(int bufSize) {
        this.bufSize = bufSize;
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
        	System.out.println(new String(buf.array()));
            key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        }
    }

    public void handleWrite(SelectionKey key) throws IOException {

        ByteBuffer buf = (ByteBuffer) key.attachment();
        buf.flip();
        SocketChannel clntChan = (SocketChannel) key.channel();
        clntChan.write(buf);
        if (!buf.hasRemaining()) { 
            key.interestOps(SelectionKey.OP_READ);
        }
        buf.compact(); 
    }
}