package ar.edu.itba.pdc.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

class UDPEchoServerSelector {
    private static final int TIMEOUT = 3000; // Wait timeout (milliseconds) 
    private static final int ECHOMAX = 255; // Maximum size of echo datagram 

    public static void main(String[] args) throws IOException {
        if (args.length != 1) // Test for correct argument list
        throw new IllegalArgumentException("Parameter(s): <Port>");

        int servPort = Integer.parseInt(args[0]);
        Selector selector = Selector.open();

        DatagramChannel channel = DatagramChannel.open();
        channel.configureBlocking(false);
        channel.socket().bind(new InetSocketAddress(servPort));
        channel.register(selector, SelectionKey.OP_READ, new ClientRecord());

        while (true) { 
            if (selector.select(TIMEOUT) == 0) {
                System.out.print(".");
                continue;
            }

            Iterator<SelectionKey> keyIter = selector.selectedKeys().iterator();
            while (keyIter.hasNext()) {
                SelectionKey key = keyIter.next(); 

                if (key.isReadable()) handleRead(key);

                if (key.isValid() && key.isWritable()) handleWrite(key);
                keyIter.remove();
            }
        }
    }

    public static void handleRead(SelectionKey key) throws IOException {
        DatagramChannel channel = (DatagramChannel) key.channel();
        ClientRecord clntRec = (ClientRecord) key.attachment();
        clntRec.buffer.clear();
        clntRec.clientAddress = channel.receive(clntRec.buffer);
        if (clntRec.clientAddress != null) {
            key.interestOps(SelectionKey.OP_WRITE);
        }
    }

    public static void handleWrite(SelectionKey key) throws IOException {
        DatagramChannel channel = (DatagramChannel) key.channel();
        ClientRecord clntRec = (ClientRecord) key.attachment();
        clntRec.buffer.flip();
        int bytesSent = channel.send(clntRec.buffer, clntRec.clientAddress);
        if (bytesSent != 0) { 
            key.interestOps(SelectionKey.OP_READ);
        }
    }

    static class ClientRecord {
        public SocketAddress clientAddress;
        public ByteBuffer buffer = ByteBuffer.allocate(ECHOMAX);
    }
}