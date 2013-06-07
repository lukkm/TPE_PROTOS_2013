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

import ar.edu.itba.pdc.handlers.AdminHandler;
import ar.edu.itba.pdc.handlers.ClientHandler;
import ar.edu.itba.pdc.interfaces.TCPHandler;

public class DispatcherHandler { 
    private static final int TIMEOUT = 3000; 
   
    private Map<AbstractSelectableChannel, TCPHandler> handlerMap;
    
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
        AdminHandler adminHandler = new AdminHandler(selector);
        /* Bindear el socket cliente */
        
        ServerSocketChannel clientChannel = ServerSocketChannel.open();
        clientChannel.socket().bind(new InetSocketAddress(5678));
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, SelectionKey.OP_ACCEPT);
        handlerMap.put(clientChannel, clientHandler);
        /* Bindear el socket admin */
        
        ServerSocketChannel adminChannel = ServerSocketChannel.open();
        adminChannel.socket().bind(new InetSocketAddress(5679));
        adminChannel.configureBlocking(false);
        adminChannel.register(selector, SelectionKey.OP_ACCEPT);
        handlerMap.put(adminChannel, adminHandler);
        
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
        	/*
        	 * Se puede usar syncronized lists (listas con acceso concurrente) para registrar las
        	 * keys que quieren leer/escribir y aca leer de esa lista y registrar en el selector las 
        	 * respectivas acciones
        	 */
            if (selector.select(TIMEOUT) == 0) {
                System.out.print(selector.keys().size());
                continue;
            }
            Iterator<SelectionKey> keyIter = selector.selectedKeys().iterator();
            while (keyIter.hasNext()) {
                SelectionKey key = keyIter.next(); 
                if (key.isAcceptable()) {
                	SocketChannel newChannel = ((ServerSocketChannel)key.channel()).accept();
                	newChannel.configureBlocking(false);
                	newChannel.register(selector, SelectionKey.OP_READ);
                	TCPHandler handler = handlerMap.get(key.channel());
                	handlerMap.put(newChannel, handler);
                	handler.accept(newChannel);
                }

                if (key.isReadable()) {
                	/* 
                	 * Cambiar el read para que tampoco conozca la key y directamente el add lo haga aca.
                	 */
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