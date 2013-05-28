package ar.edu.itba.pdc.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import ar.edu.itba.pdc.interfaces.KeyCallback;

public class TCPServerSelector { 
    private static final int TIMEOUT = 3000; 

    public static void main(String[] args) throws IOException {
        if (args.length < 2) { 
            throw new IllegalArgumentException("Parameter(s): <Client Port> <Admin Port> ...");
        }
        
        //ServerProtocol serverProtocol = new ServerProtocol();
        //EchoSelectorProtocol clientProtocol = new EchoSelectorProtocol(BUFSIZE);
        
        //clientProtocol.setServerConnector(serverProtocol);
        //serverProtocol.setClientProtocol(clientProtocol);
        
        //serverProtocol.start();
        
        Selector selector = Selector.open();
        
        /* Bindear el socket cliente */
        
        ServerSocketChannel listnChannel = ServerSocketChannel.open();
        listnChannel.socket().bind(new InetSocketAddress(Integer.parseInt(args[0])));
        listnChannel.configureBlocking(false);
        listnChannel.register(selector, SelectionKey.OP_ACCEPT);
        
        /* Bindear el socket admin */
        
        ServerSocketChannel adminChannel = ServerSocketChannel.open();
        adminChannel.socket().bind(new InetSocketAddress(Integer.parseInt(args[1])));
        adminChannel.configureBlocking(false);
        adminChannel.register(selector, SelectionKey.OP_ACCEPT);
        
        /* Bindear el socket servidor */
        
        SocketChannel serverChannel = SocketChannel.open();
        serverChannel.connect(new InetSocketAddress("hermes.jabber.org", 5222));
        serverChannel.configureBlocking(false);
        serverChannel.register(selector, SelectionKey.OP_READ);
        
        while (true) { 
            if (selector.select(TIMEOUT) == 0) {
                System.out.print(".");
                continue;
            }

            Iterator<SelectionKey> keyIter = selector.selectedKeys().iterator();
            while (keyIter.hasNext()) {
                SelectionKey key = keyIter.next(); 
                if (key.isAcceptable()) {
                	((KeyCallback)key.attachment()).accept(key);
                    //clientProtocol.handleAccept(key);
                }

                if (key.isReadable()) {
                	((KeyCallback)key.attachment()).read(key);
//                	clientProtocol.handleRead(key);
                }

                if (key.isValid() && key.isWritable()) {
                	((KeyCallback)key.attachment()).write(key);
//                	clientProtocol.handleWrite(key);
                }
                keyIter.remove();
            }
        }
    }
}