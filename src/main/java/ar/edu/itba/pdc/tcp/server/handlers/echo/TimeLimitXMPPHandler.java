/*
 * Copyright (c) 2011 Popego Corporation -- All rights reserved
 */
package ar.edu.itba.pdc.tcp.server.handlers.echo;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import ar.edu.itba.pdc.tcp.server.handlers.ConnectionHandler;

/**
 * Connection handler que implementa un servicio ECHO limitado en tiempo
 * 
 * 
 * @author Fernando Zunino
 * @since Sep 20, 2011
 */
public class TimeLimitXMPPHandler implements ConnectionHandler {
    private static final int BUFSIZE = 32; // Size (bytes) of buffer
    private static final String TIMELIMIT = "10000"; // Default limit (ms)
    private static final String TIMELIMITPROP = "Timelimit"; // Property
    private int timelimit;

    /**
     * Creates the TimeLimitEchoHandler.
     * 
     */
    public TimeLimitXMPPHandler() {
        // Get the time limit from the System properties or take the default
        timelimit = Integer.parseInt(System.getProperty(TIMELIMITPROP, TIMELIMIT));
    }

    /** @see ar.edu.itba.pdc.tcp.server.handlers.ConnectionHandler#handle(java.net.Socket) */
    @Override
    public void handle(Socket s) throws IOException {
        try {
            // Get the input
            InputStream in = s.getInputStream();
            //OutputStream out = s.getOutputStream();
            //int totalBytesEchoed = 0; // Bytes received from client
            byte[] xmppBuffer = new byte[BUFSIZE]; // Receive buffer
            long endTime = System.currentTimeMillis() + timelimit;
            int timeBoundMillis = timelimit;
            s.setSoTimeout(timeBoundMillis);
            // Receive until client closes connection, indicated by -1
            while ((timeBoundMillis > 0) && // catch zero values
                    (in.read(xmppBuffer) != -1)) {
                //out.write(echoBuffer, 0, recvMsgSize);
                //totalBytesEchoed += recvMsgSize;
                timeBoundMillis = (int) (endTime - System.currentTimeMillis());
                s.setSoTimeout(timeBoundMillis);
            }
            System.out.println("Client " + s.getRemoteSocketAddress() + " sent " + xmppBuffer);
        } catch (IOException ex) {
            System.out.println("Exception in protocol: " + ex);
        }

    }

}
