package ar.edu.itba.pdc.nio;

import java.io.IOException;

public class XMPPProxy {  

    public static void main(String[] args) throws IOException {
//        if (args.length < 2) { 
//            throw new IllegalArgumentException("Parameter(s): <Client Port> <Admin Port> ...");
//        }
        
        DispatcherHandler dh = new DispatcherHandler();
        dh.run();
//        ClientKeyCallback clientCallback = new ClientKeyCallback();
//        ServerKeyCallback serverCallback = new ServerKeyCallback();
//        
//        clientCallback.setServerCallback(serverCallback);
//        serverCallback.setserverCallback(clientCallback);
//        //ServerProtocol serverProtocol = new ServerProtocol();
//        //EchoSelectorProtocol clientProtocol = new EchoSelectorProtocol(BUFSIZE);
//        
//        //clientProtocol.setServerConnector(serverProtocol);
//        //serverProtocol.setClientProtocol(clientProtocol);
//        
//        //serverProtocol.start();
//        
//        Selector selector = Selector.open();
//        
//        /* Bindear el socket cliente */
//        
//        ServerSocketChannel listnChannel = ServerSocketChannel.open();
//        listnChannel.socket().bind(new InetSocketAddress(Integer.parseInt(args[0])));
//        listnChannel.configureBlocking(false);
//        listnChannel.register(selector, SelectionKey.OP_ACCEPT, clientCallback);
//        
//        /* Bindear el socket admin */
//        
//        /*ServerSocketChannel adminChannel = ServerSocketChannel.open();
//        adminChannel.socket().bind(new InetSocketAddress(Integer.parseInt(args[1])));
//        adminChannel.configureBlocking(false);
//        adminChannel.register(selector, SelectionKey.OP_ACCEPT);*/
//        
//        /* Bindear el socket servidor */
//        
//        SocketChannel serverChannel = SocketChannel.open();
//        serverChannel.connect(new InetSocketAddress("hermes.jabber.org", 5222));
//        serverChannel.configureBlocking(false);
//        serverChannel.register(selector, SelectionKey.OP_WRITE, serverCallback);
//        
//        while (true) { 
//            if (selector.select(TIMEOUT) == 0) {
//                System.out.print(".");
//                continue;
//            }
//
//            Iterator<SelectionKey> keyIter = selector.selectedKeys().iterator();
//            while (keyIter.hasNext()) {
//                SelectionKey key = keyIter.next(); 
//                if (key.isConnectable()) {
//                	((KeyCallback)key.attachment()).connect(key);
//                }
//                if (key.isAcceptable()) {
//                	((KeyCallback)key.attachment()).accept(key);
//                }
//
//                if (key.isReadable()) {
//                	((KeyCallback)key.attachment()).read(key);
//                }
//
//                if (key.isValid() && key.isWritable()) {
//                	((KeyCallback)key.attachment()).write(key);
//                }
//                keyIter.remove();
//            }
//        }
    }
}