package ar.edu.itba.pdc.nio;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import ar.edu.itba.pdc.controllers.Controller;
import ar.edu.itba.pdc.factories.ClientFactory;
import ar.edu.itba.pdc.interfaces.KeyCallback;
import ar.edu.itba.pdc.interfaces.TCPHandler;

public class DispatcherHandler { 
    private static final int TIMEOUT = 3000; 
    
    private Random random;
    List<Controller> controllerList; 

    public void run() {
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
        
        Selector selector = Selector.open();
        
        /* Bindear el socket cliente */
        
        ServerSocketChannel listnChannel = ServerSocketChannel.open();
        listnChannel.socket().bind(new InetSocketAddress(Integer.parseInt(args[0])));
        listnChannel.configureBlocking(false);
        listnChannel.register(selector, SelectionKey.OP_ACCEPT, new ClientFactory());
        
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
                	SocketChannel clientChannel = ((ServerSocketChannel) key.channel()).accept();
                	clientChannel.configureBlocking(false);
                	int rand = random.nextInt(controllerList.size());
					Controller randomController = controllerList
							.get(rand);
					randomController.register(clientChannel, new TCPHandler(){});
                }

                if (key.isReadable()) {
                	((KeyCallback)key.attachment()).read(key);
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