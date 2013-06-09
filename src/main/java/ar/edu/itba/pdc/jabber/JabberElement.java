package ar.edu.itba.pdc.jabber;


public abstract class JabberElement {
	
	public static Message createMessage(String from, String to) {
		return new Message(from, to);
	}
	
	public static JIDConfiguration createJIDConfiguration() {
		return new JIDConfiguration();
	}
	
	public static Presence createPresence(String from, String to) {
		return new Presence(from, to);
	}
}
