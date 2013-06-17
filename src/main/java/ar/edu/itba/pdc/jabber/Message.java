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

	/**
	 * Returns the body of the message
	 * 
	 * @return
	 */

	public String getMessage() {
		return message;
	}

	/**
	 * Sets the body of the message
	 */

	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Returns the value of the active tag of the message
	 * 
	 * @return
	 */

	public String getActive() {
		return active;
	}

	/**
	 * Sets the active tag of the message
	 * 
	 * @param active
	 */

	public void setActive(String active) {
		this.active = active;
	}

	/**
	 * Returns the xmlns parameter of the active tag
	 * 
	 * @return
	 */

	public String getActiveXmlns() {
		return activeXmlns;
	}

	/**
	 * Sets the xmlns of the active tag
	 * 
	 * @param xmlns
	 */

	public void setActiveXmlns(String xmlns) {
		this.activeXmlns = xmlns;
	}

	/**
	 * Returns the recipient of the message
	 * 
	 * @return
	 */

	public String getTo() {
		return to;
	}

	/**
	 * Sets the recipient of the message
	 * 
	 * @param to
	 */

	public void setTo(String to) {
		this.to = to;
	}

	/**
	 * Returns the type of the message
	 * 
	 * @return
	 */

	public String getType() {
		return type;
	}

	/**
	 * Sets the type of the message
	 * 
	 * @param type
	 */

	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Returns the error code of the message
	 * 
	 * @return
	 */

	public int getErrorCode() {
		return errorCode;
	}

	/**
	 * Sets the error code of the message
	 * 
	 * @param errorCode
	 */

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * Returns the error type of the message
	 * 
	 * @return
	 */

	public String getErrorType() {
		return errorType;
	}

	/**
	 * Sets the error type of the message
	 * 
	 * @param errorType
	 */

	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

	/**
	 * Returns the error XML body of the message
	 * 
	 * @return
	 */

	public String getErrorXMLBody() {
		return errorXMLBody;
	}

	/**
	 * Sets the error XML body of the message
	 * 
	 * @param errorXML
	 */

	public void setErrorXMLBody(String errorXML) {
		this.errorXMLBody = errorXML;
	}

	/**
	 * Returns the message as an XML string, using a default template and
	 * setting up values using the parameters contained
	 * 
	 * @return
	 */

	public String getXMLMessage() {
		StringBuffer xmlMessage = new StringBuffer();
		xmlMessage.append("<message from='" + getFrom() + "' to='" + getTo()
				+ "' type='" + ((getType() != null) ? getType() : "chat")
				+ "'>");

		if (getActive() != null && !getActive().equals(""))
			xmlMessage.append("<active xmlns='" + getActiveXmlns() + "'>"
					+ getActive() + "</active>");

		xmlMessage.append("<body>" + getMessage() + "</body>");

		if (errorCode != 0)
			xmlMessage.append("<error code='" + errorCode + "' type='"
					+ errorType + "'>" + errorXMLBody + "</error>");

		xmlMessage.append("</message>");

		return xmlMessage.toString();
	}

}
