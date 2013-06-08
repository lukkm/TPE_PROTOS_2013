package ar.edu.itba.pdc.parser;

import java.util.LinkedList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import ar.edu.itba.pdc.jabber.JabberElement;
import ar.edu.itba.pdc.jabber.Message;
import ar.edu.itba.pdc.stanzas.Stanza;

public class XMPPHandler extends DefaultHandler {
	
	private List<Stanza> stanzas;
	private Stanza currentStanza;
	private int indentCount;
	
	private ParsingState parsingState = ParsingState.parsingStart;
	
	public XMPPHandler() {
		stanzas = new LinkedList<Stanza>();
		indentCount = 0;
	}
	
	public void startElement(String s, String s1, String elementName, Attributes attributes) throws SAXException {
		if (indentCount == 1) {
			currentStanza = new Stanza();
			if (elementName.equals("message")) {
				currentStanza.setElement(JabberElement.createMessage(attributes.getValue("from"), attributes.getValue("to")));
			} else if (elementName.equals("presence")) {
				//currentStanza.setElement(JabberElement.createPresence());
			} 
		} else if (indentCount > 0){
			if (currentStanza.isMessage()) {
				if (elementName.equals("body")) {
					this.parsingState = ParsingState.messageBody;
				}
			}
		}
		
		 indentCount++; 
	}	
	 
	public void endElement(String s, String s1, String element) throws SAXException {
		 indentCount--;
		 if (indentCount == 1) {
			 currentStanza.complete();
			 stanzas.add(currentStanza);
			 System.out.println("Completada stanza: " + element);
		 }
	}
	
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		switch(parsingState) {
			case messageBody:
				((Message)(currentStanza.getElement())).setMessage(new String(ch).substring(start, start + length));
				parsingState = ParsingState.parsingStart;
				break;
			default:
		}
	}
	
	public List<Stanza> getStanzaList() {
		return stanzas;
	}
	
	public boolean hasIncompleteElements() {
		return !currentStanza.isComplete();
	}

}
