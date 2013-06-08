package ar.edu.itba.pdc.jabber;


public abstract class JabberElement {

	private String from;
	private String to;
	
	public static Message createMessage(String from, String to) {
		return new Message(from, to);
	}
	
	protected JabberElement(String from, String to) {
		this.from = from;
		this.to = to;
	}
	
	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	
}
