package ar.edu.itba.pdc.stanzas;

import ar.edu.itba.pdc.jabber.JabberElement;
import ar.edu.itba.pdc.jabber.Message;
import ar.edu.itba.pdc.jabber.Presence;

public class Stanza {

	private StanzaType type;
	private boolean completed, rejected;
	private JabberElement element;
	private String xmlString = "";

	public Stanza() {
		this.completed = false;
		this.rejected = false;
	}

	public Stanza(StanzaType type) {
		this();
		this.type = type;
	}

	public Stanza(StanzaType type, JabberElement element) {
		this(type);
		this.element = element;
	}

	/**
	 * Returns the type of a stanza (See StanzaType.java)
	 * 
	 * @return
	 */

	public String getType() {
		return type.toString();
	}

	/**
	 * Sets the type of the stanza (See StanzaType.java)
	 * 
	 * @param type
	 */

	public void setType(StanzaType type) {
		this.type = type;
	}

	/**
	 * Returns the XML string equivalent to the marshaled stanza object.
	 * 
	 * @return
	 */

	public String getXMLString() {
		if (isMessage())
			return ((Message) element).getXMLMessage();
		return xmlString;
	}

	/**
	 * Sets the XML string equivalent to the marshaled stanza object
	 * 
	 * @param xmlString
	 */

	public void setXMLString(String xmlString) {
		this.xmlString = xmlString;
	}

	/**
	 * Confirms the stanza was parsed correctly
	 */

	public void complete() {
		this.completed = true;
	}

	/**
	 * Returns true if the stanza was parsed correctly
	 * 
	 * @return
	 */

	public boolean isComplete() {
		return completed;
	}

	/**
	 * Returns true if the stanza's element is a Message
	 * 
	 * @return
	 */

	public boolean isMessage() {
		return element != null && element.getClass() == Message.class;
	}

	/**
	 * Returns true if the stanza's element is a Presence
	 * 
	 * @return
	 */
	
	public boolean isPresence() {
		return element != null && element.getClass() == Presence.class;
	}
	
	/**
	 * Returns the jabber element contained in the stanza object
	 * 
	 * @return
	 */

	public JabberElement getElement() {
		return element;
	}
	
	/**
	 * Sets the jabber element contained in the stanza object
	 * 
	 * @param element
	 */

	public void setElement(JabberElement element) {
		this.element = element;
	}
	
	/**
	 * Rejects the stanza, blocks it from sending.
	 */

	public void reject() {
		this.rejected = true;
	}
	
	/**
	 * Returns true if the stanza is rejected
	 * 
	 * @return
	 */

	public boolean isrejected() {
		return rejected;
	}

}
