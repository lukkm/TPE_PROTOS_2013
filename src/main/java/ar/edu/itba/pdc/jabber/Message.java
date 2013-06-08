package ar.edu.itba.pdc.jabber;

public class Message extends JabberElement {
	
	private String message;

	public Message(String message, String from, String to) {
		super(from, to);
		this.message = message;
	}
	
	
}
