package ar.edu.itba.pdc.jabber;

public abstract class JabberElement {

	private String from;
	private String to;
	
	protected JabberElement(String from, String to) {
		this.from = from;
		this.to = to;
	}
	
}
