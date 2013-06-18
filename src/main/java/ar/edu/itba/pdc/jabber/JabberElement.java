package ar.edu.itba.pdc.jabber;


public abstract class JabberElement {
	
	public String from;
	
	protected JabberElement(String from) {
		this.from = from;
	}
	
	/**
	 * Creates a new instance of a Message object
	 * 
	 * @param from
	 * @param to
	 */
	
	public static Message createMessage(String from, String to) {
		return new Message(from, to);
	}
	
	/**
	 * Creates a new instance of a Presence object
	 * 
	 * @param from
	 * @param to
	 */
	
	public static Presence createPresence(String from, String to) {
		return new Presence(from, to);
	}
	
	public String getFrom() {
		return from;
	}
	
	public void setFrom(String from) {
		this.from = from;
	}
	
}
