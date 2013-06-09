package ar.edu.itba.pdc.jabber;

public class Presence extends JabberElement {

	private String from, to, type, delay;
	
	public Presence(String from, String to) {
		this.from = from;
		this.to = to;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public void setDelay(String delay) {
		this.delay = delay;
	}
		
	public String getFrom() {
		return from;
	}
	
	public String getTo() {
		return to;
	}
	
	public String getType() {
		return type;
	}
	
	public String getDelay() {
		return delay;
	}
	
}
