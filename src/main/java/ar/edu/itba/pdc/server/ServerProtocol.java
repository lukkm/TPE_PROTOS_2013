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
	private boolean hasInformation = false;
	
	public void setClientProtocol(CommunicationProtocol clientProtocol) {		
		this.clientProtocol = clientProtocol;
	}
	
	public void start() throws IOException {
		final SocketChannel srvChan = SocketChannel.open();
        srvChan.configureBlocking(false);
        
        if (!srvChan.connect(new InetSocketAddress("proxy.eu.jabber.org", 80))) {
            while (!srvChan.finishConnect()) {
            	
            }
        }
        
        new Thread(new Runnable(){

			@Override
			public void run() {
				ByteBuffer readBuf = ByteBuffer.allocate(10000);
				int bytesRead;
				while (true) {
					try {
						if ((bytesRead = srvChan.read(readBuf)) != -1) {
							if (bytesRead > 0) {
								System.out.println("Bytes: " + bytesRead);
								System.out.println("Mensaje del servidor: " + new String(readBuf.array()));								
								ServerProtocol.this.clientProtocol.communicate(readBuf);
							}
						}
						if (ServerProtocol.this.hasInformation) {
//							System.out.println("Mensaje del gil de luki: " + new String(writeBuf.array()));
//							System.out.println("De nuevo: " + new String(writeBuf.array()) + "aaa");
							//while(ServerProtocol.this.writeBuf.hasRemaining())
							int bytes;
							while ((bytes = srvChan.write(ServerProtocol.this.writeBuf)) != 0) {
								System.out.println("asd " + bytes);
							}
							System.out.println(bytes);
							ServerProtocol.this.hasInformation = false;
							System.out.println("Lestooo");
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
		this.writeBuf = message;
		this.hasInformation = true;
	}	
}
