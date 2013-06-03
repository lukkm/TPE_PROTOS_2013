package ar.edu.itba.pdc.controllers;

import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;

import ar.edu.itba.pdc.interfaces.KeyCallback;
import ar.edu.itba.pdc.interfaces.TCPHandler;

public class Controller implements Runnable {

	private Selector selector;
	private Map<SocketChannel, TCPHandler> handlerMap;
	
	
	@Override
	public void run() {
		Iterator<SelectionKey> keyIter = selector.selectedKeys().iterator();
        while (keyIter.hasNext()) {
            SelectionKey key = keyIter.next(); 
            if (key.isAcceptable()) {
            	System.out.println("Eeeh gato que hace aca?");
            }

            if (key.isReadable()) {
//            	TCPHandler handler = handlerMap.get(key.channel()).read(key);
            }

            if (key.isValid() && key.isWritable()) {
            	((KeyCallback)key.attachment()).write(key);
//            	clientProtocol.handleWrite(key);
            }
            keyIter.remove();
        }
	}
	
	public void register(SocketChannel channel, TCPHandler handler) throws ClosedChannelException {
		handlerMap.put(channel, handler);
		channel.register(selector, SelectionKey.OP_READ);
	}

}
