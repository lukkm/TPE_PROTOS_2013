package ar.edu.itba.pdc.jabber;

public class Message extends JabberElement {
	
	private String message = null;
	private String to;
	
	public Message(String message, String from, String to) {
		this(from, to);
		this.message = message;
	}
	
	public Message(String from, String to) {
		super(from);
		this.to = to;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	
}
