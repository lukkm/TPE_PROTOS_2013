package ar.edu.itba.pdc.nio;

import java.io.IOException;

public class XMPPProxy {  

    public static void main(String[] args) throws IOException {
        DispatcherHandler dh = new DispatcherHandler();
        dh.run();
    }
}