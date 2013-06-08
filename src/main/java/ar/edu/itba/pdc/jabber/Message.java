package ar.edu.itba.pdc.jabber;

public class Message extends JabberElement {
	
	private String message = null;

	public Message(String message, String from, String to) {
		super(from, to);
		this.message = message;
	}
	
	public Message(String from, String to) {
		super(from, to);
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
	
}
