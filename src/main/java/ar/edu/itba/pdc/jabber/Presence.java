package ar.edu.itba.pdc.jabber;

public class Presence extends JabberElement {

	private String to, type, delay;
	
	public Presence(String from, String to) {
		super(from);
		this.to = to;
	}
	
	/**
	 * Returns the type of the presence
	 * 
	 * @return
	 */
	
	public String getType() {
		return type;
	}

	/**
	 * Returns the delay tag of the presence
	 * 
	 * @return
	 */
	
	public String getDelay() {
		return delay;
	}

	/**
	 * Sets the type of the presence
	 * 
	 * @param type
	 */
	
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * Sets the delay tag of the presence
	 * 
	 * @param delay
	 */
	
	public void setDelay(String delay) {
		this.delay = delay;
	}
	
	/**
	 * Returns the recipient of the presence
	 * 
	 * @return
	 */
	
	public String getTo() {
		return to;
	}
	
	/**
	 * Sets the recipient of the presence
	 * 
	 * @param to
	 */
	
	public void setTo(String to) {
		this.to = to;
	}
	
	
}
