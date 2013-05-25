package ar.edu.itba.pdc.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import ar.edu.itba.pdc.interfaces.CommunicationProtocol;

public class ServerProtocol implements CommunicationProtocol {

	private CommunicationProtocol clientProtocol;
	private ByteBuffer writeBuf;
	
	public ServerProtocol (CommunicationProtocol clientProtocol) {
		this.clientProtocol = clientProtocol;
	}
	
	public void start() throws IOException {
		final SocketChannel srvChan = SocketChannel.open();
        srvChan.configureBlocking(false);
        
        if (!srvChan.connect(new InetSocketAddress("jabber.org", 5222))) {
            while (!srvChan.finishConnect()) {
                System.out.print(".");
            }
        }
        
        new Thread(new Runnable(){

			@Override
			public void run() {
				ByteBuffer readBuf = ByteBuffer.allocate(10000);
				while (true) {
					try {
						if (srvChan.read(readBuf) != -1) {
							ServerProtocol.this.clientProtocol.communicate(readBuf);
						}
						if (ServerProtocol.this.writeBuf.hasRemaining()) {
							srvChan.write(writeBuf);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
        	
        }).start();
	}
	
	@Override
	public void communicate(ByteBuffer message) {
		ServerProtocol.this.writeBuf = message;
	}	
}
