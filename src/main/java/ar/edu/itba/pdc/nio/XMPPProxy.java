package ar.edu.itba.pdc.nio;

import java.io.IOException;

import ar.edu.itba.pdc.logger.XMPPLogger;

public class XMPPProxy {  
	
    public static void main(String[] args) {
        DispatcherHandler dh = new DispatcherHandler();
        try {
			dh.run();
		} catch (IOException e) {
			XMPPLogger.getInstance().error("Cannot start application, IO error.");
		} catch (Exception e) {
			XMPPLogger.getInstance().error("Unexpected error, closing application");
		}
    }
}