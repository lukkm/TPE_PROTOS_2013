package ar.edu.itba.pdc.interfaces;

import java.nio.ByteBuffer;

public interface CommunicationProtocol {

	public void communicate(ByteBuffer message);
	
}
