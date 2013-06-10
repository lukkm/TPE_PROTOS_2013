package ar.edu.itba.pdc.proxy;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class ChannelBuffers {
	private static final int BUFFER_SIZE = 1024;
	private ByteBuffer readBuffer;
	private ByteBuffer writeBuffer;
	
	private Map<BufferType, ByteBuffer> buffers = new HashMap<BufferType, ByteBuffer>();;

	public ChannelBuffers() {
		this.readBuffer = ByteBuffer.allocate(BUFFER_SIZE);
		this.writeBuffer = ByteBuffer.allocate(BUFFER_SIZE);
		initializeMap();
	}

	public ChannelBuffers(ByteBuffer readBuffer, ByteBuffer writeBuffer) {
		this.readBuffer = readBuffer;
		this.writeBuffer = writeBuffer;
		initializeMap();
	}
	
	private void initializeMap() {
		buffers.put(BufferType.read, readBuffer);
		buffers.put(BufferType.write, writeBuffer);
	}

	public void setReadBuffer(ByteBuffer readBuffer) {
		this.readBuffer = readBuffer;
	}

	public void setWriteBuffer(ByteBuffer writeBuffer) {
		this.writeBuffer = writeBuffer;
	}

	public ByteBuffer getBuffer(BufferType type) {
		return buffers.get(type);
	}
	
	public ByteBuffer getReadBuffer() {
		return readBuffer;
	}
	
	public ByteBuffer getWriteBuffer() {
		return writeBuffer;
	}
	
	public void synchronizeBuffers(ChannelBuffers channelBuffers) {
		ByteBuffer wrBuffer = channelBuffers.getBuffer(BufferType.write);
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
	
	public void clearBuffer(BufferType type) {
		buffers.get(type).clear();
	}
	
	public void flipBuffer(BufferType type) {
		buffers.get(type).flip();
	}
	
	public void compactBuffer(BufferType type) {
		buffers.get(type).compact();
	}
	
	public boolean hasInformation(BufferType type) {
		return buffers.get(type).capacity() != buffers.get(type).remaining();
	}
	
	public boolean hasRemaining(BufferType type) {
		return buffers.get(type).hasRemaining();
	}
	
}
