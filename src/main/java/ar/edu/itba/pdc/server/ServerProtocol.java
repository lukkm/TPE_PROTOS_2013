package ar.edu.itba.pdc.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import ar.edu.itba.pdc.interfaces.CommunicationProtocol;

public class ServerProtocol implements CommunicationProtocol {

	private CommunicationProtocol clientProtocol;
	private ByteBuffer writeBuf = ByteBuffer.allocate(50000);
	private boolean hasInformation = false;
	
	public void setClientProtocol(CommunicationProtocol clientProtocol) {		
		this.clientProtocol = clientProtocol;
	}
	
	public void start() throws IOException {
		final SocketChannel srvChan = SocketChannel.open();
        srvChan.configureBlocking(false);
        
        if (!srvChan.connect(new InetSocketAddress("208.68.163.221", 5222))) {
            while (!srvChan.finishConnect()) {
            	
            }
        }
        
        new Thread(new Runnable(){

			@Override
			public void run() {
				ByteBuffer readBuf = ByteBuffer.allocate(50000);
				int bytesRead;
				while (true) {
					try {
						if ((bytesRead = srvChan.read(readBuf)) != -1) {
							if (bytesRead > 0) {
								System.out.println("Received from hermes: " + new String(readBuf.array()) + "\n");
								clientProtocol.communicate(ByteBuffer.wrap(readBuf.array(), 0, (int)bytesRead));
								readBuf.clear();
							}
						}
						if (hasInformation) {
							while(writeBuf.hasRemaining()) {
								System.out.println("Sending to hermes: " + new String(writeBuf.array()) + "\n");
								int i = srvChan.write(writeBuf);
								writeBuf.position(writeBuf.position() + i);
							}
							hasInformation = false;
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
		this.writeBuf.put(message);
//		message.clear();
		this.hasInformation = true;
	}	
}
