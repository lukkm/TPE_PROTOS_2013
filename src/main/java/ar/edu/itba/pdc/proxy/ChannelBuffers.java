package ar.edu.itba.pdc.proxy;

import java.nio.ByteBuffer;

public class ChannelBuffers {
	private static final int BUFFER_SIZE = 1024;
	private ByteBuffer readBuffer;
	private ByteBuffer writeBuffer;

	public ChannelBuffers() {
		readBuffer = ByteBuffer.allocate(BUFFER_SIZE);
		writeBuffer = ByteBuffer.allocate(BUFFER_SIZE);
	}

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
	
	public ByteBuffer getBuffer(BufferType type) {
		return type == BufferType.read ? readBuffer : writeBuffer;
	}

	public void synchronizeBuffers(ChannelBuffers channelBuffers) {
		ByteBuffer wrBuffer = channelBuffers.getWriteBuffer();
		readBuffer.flip();
		if (wrBuffer.remaining() > (readBuffer.capacity() - readBuffer
				.remaining())) {
			channelBuffers.setWriteBuffer(wrBuffer.put(readBuffer));
		} else {
			int rel = readBuffer.capacity() / wrBuffer.capacity();
			wrBuffer.flip();
			channelBuffers.setWriteBuffer(ByteBuffer
					.allocateDirect(wrBuffer.capacity() * ((rel >= 1) ? rel : 2)).put(wrBuffer)
					.put(readBuffer));
		}
		readBuffer.clear();
	}
	
	public void autoSynchronizeBuffers() {
		synchronizeBuffers(this);
	}
	
	public void writeToBuffer(BufferType type, byte[] bytes) {
		ByteBuffer buf = (type == BufferType.read) ? readBuffer : writeBuffer;
		if(buf.capacity() - buf.remaining() < bytes.length) {
			buf.flip();
			buf = ByteBuffer.allocate(buf.capacity() + bytes.length).put(buf);
			if (type == BufferType.read) {
				setReadBuffer(buf);
			} else {
				setWriteBuffer(buf);
			}
		}
		buf.put(bytes);
	}
}
