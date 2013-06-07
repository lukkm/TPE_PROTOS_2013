package ar.edu.itba.pdc.utils;

import java.nio.ByteBuffer;

public class ChannelBuffers {
	
	private ByteBuffer readBuffer;
	private ByteBuffer writeBuffer;

	public ChannelBuffers(ByteBuffer readBuffer, ByteBuffer writeBuffer) {
		this.readBuffer = readBuffer;
		this.writeBuffer = writeBuffer;
	}

	public void setReadBuffer(ByteBuffer readBuffer) {
		this.readBuffer = readBuffer;
	}
	
	public void setWriteBuffer(ByteBuffer writeBuffer) {
		this.writeBuffer = writeBuffer;
	}
	
	public ByteBuffer getReadBuffer() {
		return readBuffer;
	}
	
	public ByteBuffer getWriteBuffer() {
		return writeBuffer;
	}
	
	public void synchronizeBuffers(ChannelBuffers channelBuffers) {
		ByteBuffer wrBuffer = channelBuffers.getWriteBuffer();
		readBuffer.flip();
		if (wrBuffer.remaining() > (readBuffer.capacity() - readBuffer.remaining())) {
			channelBuffers.setWriteBuffer(wrBuffer.put(readBuffer));
		} else {
			wrBuffer.flip();
			channelBuffers.setWriteBuffer(ByteBuffer.allocateDirect(wrBuffer.capacity() * 2).put(wrBuffer).put(readBuffer));
		}
		readBuffer.clear();
	}
	
}
