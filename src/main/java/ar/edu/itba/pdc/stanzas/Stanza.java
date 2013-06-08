package ar.edu.itba.pdc.stanzas;

import java.nio.ByteBuffer;

import ar.edu.itba.pdc.jabber.JabberElement;

public class Stanza {

	private ByteBuffer buffer;
	private StanzaType type;
	private boolean completed;
	private JabberElement element;
	
	public Stanza() {
		this.completed = false;
	}
	
	public Stanza(ByteBuffer buffer, StanzaType type) {
		this();
		this.buffer = buffer;
		this.type = type;
	}
	
	public Stanza(ByteBuffer buffer, StanzaType type, JabberElement element) {
		this(buffer, type);
		this.element = element;
	}
	
	public String getType() {
		return type.toString();
	}
	
	public void complete() {
		this.completed = true;
	}
	
	public void setType(StanzaType type) {
		this.type = type;
	}
	
	public void setBuffer(ByteBuffer buffer) {
		this.buffer = buffer;
	}
	
	public boolean isComplete() {
		return completed;
	}
	
	@Override
	public String toString() {
		if (buffer != null)
			return new String(buffer.array());
		else
			return element.toString();
	}
	
	public void setElement(JabberElement element) {
		this.element = element;
	}
	
}
