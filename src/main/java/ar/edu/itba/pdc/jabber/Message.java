package ar.edu.itba.pdc.jabber;

public class Message extends JabberElement {
	
	private String message = null;
	private String to, activeXmlns, active;
	
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
	
	public void setActive(String active) {
		this.active = active;
	}
	
	public void setActiveXmlns(String xmlns) {
		this.activeXmlns = xmlns;
	}
	
	public String getActive() {
		return active;
	}
	
	public String getActiveXmlns() {
		return activeXmlns;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}
	
	public String getXMLMessage() {
		String xmlMessage = "<message from='" 
				+ getFrom() 
				+ "' to='" 
				+ getTo() + "' type='chat'>";
		if (getActive() != null && !getActive().equals("")) {
			xmlMessage = xmlMessage + "<active xmlns='" + getActiveXmlns() + "'>" + getActive() + "</active>";
		}
		xmlMessage = xmlMessage + ("<body>"
				+ getMessage()
				+ "</body>"
				+ "</message>");
		return xmlMessage;
	}

	
}
