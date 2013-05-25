package ar.edu.itba.pdc.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;

import ar.edu.itba.pdc.server.ServerProtocol;

public class TCPServerSelector {
    private static final int BUFSIZE = 1000; 
    private static final int TIMEOUT = 3000; 

    public static void main(String[] args) throws IOException {
        if (args.length < 1) { 
            throw new IllegalArgumentException("Parameter(s): <Port> ...");
        }
        
        ServerProtocol serverProtocol = new ServerProtocol();
        EchoSelectorProtocol clientProtocol = new EchoSelectorProtocol(BUFSIZE);
        
        clientProtocol.setServerConnector(serverProtocol);
        serverProtocol.setClientProtocol(clientProtocol);
        
        //serverProtocol.start();
        
        Selector selector = Selector.open();
        for (String arg : args) {
            ServerSocketChannel listnChannel = ServerSocketChannel.open();
            listnChannel.socket().bind(new InetSocketAddress(Integer.parseInt(arg)));
            listnChannel.configureBlocking(false);
            listnChannel.register(selector, SelectionKey.OP_ACCEPT);
        }

        while (true) { 
            if (selector.select(TIMEOUT) == 0) {
                System.out.print(".");
                continue;
            }

            Iterator<SelectionKey> keyIter = selector.selectedKeys().iterator();
            while (keyIter.hasNext()) {
                SelectionKey key = keyIter.next(); 
                if (key.isAcceptable()) {
                    clientProtocol.handleAccept(key);
                }

                if (key.isReadable()) {
                	clientProtocol.handleRead(key);
                }

                if (key.isValid() && key.isWritable()) {
                	clientProtocol.handleWrite(key);
                }
                keyIter.remove();
            }
        }
    }
}