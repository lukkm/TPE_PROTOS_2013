package ar.edu.itba.pdc.jabber;

public class Message extends JabberElement {
	
	private String message = null;
	private String to, activeXmlns, active, type, errorType, errorXMLBody;
	private int errorCode = 0;
	
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
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public int getErrorCode() {
		return errorCode;
	}
	
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorType() {
		return errorType;
	}
	
	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

	public String getErrorXMLBody() {
		return errorXMLBody;
	}

	public void setErrorXMLBody(String errorXML) {
		this.errorXMLBody = errorXML;
	}

	public String getXMLMessage() {
		StringBuffer xmlMessage = new StringBuffer();
		xmlMessage.append("<message from='" 
				+ getFrom() 
				+ "' to='" 
				+ getTo() 
				+ "' type='" 
				+ ((getType() != null) ? getType() : "chat")
				+ "'>");
		
		if (getActive() != null && !getActive().equals(""))
			xmlMessage.append("<active xmlns='" + getActiveXmlns() + "'>" + getActive() + "</active>");
		
		xmlMessage.append("<body>"
				+ getMessage()
				+ "</body>");
		
		if (errorCode != 0)
			xmlMessage.append("<error code='" 
					+ errorCode 
					+ "' type='" 
					+ errorType 
					+ "'>"
					+ errorXMLBody
					+ "</error>");
		
		xmlMessage.append("</message>");
		
		return xmlMessage.toString();
	}



	
}
