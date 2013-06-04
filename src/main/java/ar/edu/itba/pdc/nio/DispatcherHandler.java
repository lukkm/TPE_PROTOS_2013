package ar.edu.itba.pdc.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ar.edu.itba.pdc.controllers.ClientHandler;
import ar.edu.itba.pdc.interfaces.TCPHandler;

public class DispatcherHandler { 
    private static final int TIMEOUT = 3000; 
   
    Map<AbstractSelectableChannel, TCPHandler> handlerMap;
    
    public DispatcherHandler() {
    	handlerMap = new HashMap<AbstractSelectableChannel, TCPHandler>();
    }

    public void run() throws IOException {
//        if (args.length < 2) { 
//            throw new IllegalArgumentException("Parameter(s): <Client Port> <Admin Port> ...");
//        }
//        
        
//        ClientKeyCallback clientCallback = new ClientKeyCallback();
//        ServerKeyCallback serverCallback = new ServerKeyCallback();
//        
//        clientCallback.setServerCallback(serverCallback);
//        serverCallback.setserverCallback(clientCallback);
//        //ServerProtocol serverProtocol = new ServerProtocol();
        //EchoSelectorProtocol clientProtocol = new EchoSelectorProtocol(BUFSIZE);
        
        //clientProtocol.setServerConnector(serverProtocol);
        //serverProtocol.setClientProtocol(clientProtocol);
        
        //serverProtocol.start();
        
    	/* Genero los handlers */
    	
    	
        Selector selector = Selector.open();
        
        ClientHandler clientHandler = new ClientHandler(selector);

        /* Bindear el socket cliente */
        
        ServerSocketChannel clientChannel = ServerSocketChannel.open();
        clientChannel.socket().bind(new InetSocketAddress(5678));
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, SelectionKey.OP_ACCEPT);
        handlerMap.put(clientChannel, clientHandler);
        /* Bindear el socket admin */
        
//        ServerSocketChannel adminChannel = ServerSocketChannel.open();
//        adminChannel.socket().bind(new InetSocketAddress(Integer.parseInt(args[1])));
//        adminChannel.configureBlocking(false);
//        adminChannel.register(selector, SelectionKey.OP_ACCEPT, new AdminFactory());
        
        /* Bindear el socket servidor */
        
//        SocketChannel serverChannel = SocketChannel.open();
//        serverChannel.connect(new InetSocketAddress("hermes.jabber.org", 5222));
//        serverChannel.configureBlocking(false);
//        serverChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, serverCallback);
//        
        while (!Thread.interrupted()) { 
            if (selector.select(TIMEOUT) == 0) {
                System.out.print(".");
                continue;
            }

            Iterator<SelectionKey> keyIter = selector.selectedKeys().iterator();
            while (keyIter.hasNext()) {
                SelectionKey key = keyIter.next(); 
                if (key.isAcceptable()) {
                	SocketChannel channel = handlerMap.get(key.channel()).accept(key);
                	if (channel != null)
                		handlerMap.put(channel, handlerMap.get(key.channel()));
                }

                if (key.isReadable()) {
                	SocketChannel channel = handlerMap.get(key.channel()).read(key);
                	if (channel != null)
                		handlerMap.put(channel, handlerMap.get(key.channel()));
                }

                if (key.isValid() && key.isWritable()) {
                	handlerMap.get(key.channel()).write(key);
                }
                keyIter.remove();
            }
        }
    }
}