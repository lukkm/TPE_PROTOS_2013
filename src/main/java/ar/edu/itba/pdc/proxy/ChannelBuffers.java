package ar.edu.itba.pdc.proxy;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class ChannelBuffers {
	private static final int BUFFER_SIZE = 4096;
	
	private Map<BufferType, ByteBuffer> buffers;

	public ChannelBuffers() {
		initializeMap(ByteBuffer.allocate(BUFFER_SIZE), ByteBuffer.allocate(BUFFER_SIZE));
	}

	public ChannelBuffers(ByteBuffer readBuffer, ByteBuffer writeBuffer) {
		initializeMap(readBuffer, writeBuffer);
	}
	
	private void initializeMap(ByteBuffer readBuffer, ByteBuffer writeBuffer) {
		buffers = new HashMap<BufferType, ByteBuffer>();
		buffers.put(BufferType.read, readBuffer);
		buffers.put(BufferType.write, writeBuffer);
	}
	
	public void setBuffer(BufferType type, ByteBuffer buffer) {
		buffers.put(type, buffer);
	}
	
	public ByteBuffer getBuffer(BufferType type) {
		return buffers.get(type);
	}

	public void synchronizeBuffers(ChannelBuffers channelBuffers) {
		ByteBuffer wrBuffer = channelBuffers.getBuffer(BufferType.write);
		buffers.get(BufferType.read).flip();
		if (wrBuffer.remaining() > (buffers.get(BufferType.read).capacity() - buffers.get(BufferType.read)
				.remaining())) {
			channelBuffers.setBuffer(BufferType.write, wrBuffer.put(buffers.get(BufferType.read)));
		} else {
			int rel = buffers.get(BufferType.read).capacity() / wrBuffer.capacity();
			wrBuffer.flip();
			channelBuffers.setBuffer(BufferType.write, ByteBuffer
					.allocateDirect(wrBuffer.capacity() * ((rel >= 1) ? rel : 2)).put(wrBuffer)
					.put(buffers.get(BufferType.read)));
		}
		buffers.get(BufferType.read).clear();
	}
	
	public void autoSynchronizeBuffers() {
		synchronizeBuffers(this);
	}
	
	public void writeToBuffer(BufferType type, byte[] bytes) {
		ByteBuffer buf = (type == BufferType.read) ? buffers.get(BufferType.read) : buffers.get(BufferType.write);
		if(buf.capacity() - buf.remaining() < bytes.length) {
			buf.flip();
			buf = ByteBuffer.allocate(buf.capacity() + bytes.length).put(buf);
			if (type == BufferType.read) {
				setBuffer(BufferType.read, buf);
			} else {
				setBuffer(BufferType.write, buf);
			}
		}
		buf.put(bytes);
	}
	
	public void clearBuffer(BufferType type) {
		if (buffers.get(type) != null)
			buffers.get(type).clear();
	}
	
	public void compactBuffer(BufferType type) {
		if (buffers.get(type) != null)
			buffers.get(type).compact();
	}
	
	public void flipBuffer(BufferType type) {
		if (buffers.get(type) != null)
			buffers.get(type).flip();
	}
	
	public byte[] getBufferArray(BufferType type) {
		if (buffers.get(type) != null)
			return buffers.get(type).array();
		return null;
	}
	
	public void expandBuffer(BufferType type) {
		ByteBuffer buf = getBuffer(type);
		buf.flip();
		setBuffer(type, ByteBuffer.allocate(buf.capacity() * 2).put(buf));
	}
	
	public boolean hasRemainingFor(BufferType type) {
		return buffers.get(type) != null && buffers.get(type).hasRemaining();
	}
	
	public boolean hasInformationFor(BufferType type) {
		return buffers.get(type) != null && (buffers.get(type).capacity() != buffers.get(type).remaining());
	}
}
