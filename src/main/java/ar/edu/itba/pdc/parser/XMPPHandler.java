package ar.edu.itba.pdc.parser;

import java.util.LinkedList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import ar.edu.itba.pdc.stanzas.Stanza;

public class XMPPHandler extends DefaultHandler {
	
	private List<Stanza> stanzas;
	private Stanza currentStanza;
	private int indentCount;
	
	public XMPPHandler() {
		stanzas = new LinkedList<Stanza>();
		indentCount = 0;
	}
	
	public void startElement(String s, String s1, String elementName, Attributes attributes) throws SAXException {
		 if (indentCount == 0) {
			 currentStanza = new Stanza();
			 stanzas.add(currentStanza);
		 } 
		 indentCount++;
		 
		 System.out.println("Elemento: " + elementName);
		 /* Procesar segun el tipo de elemento */
		 
	}	
	 
	public void endElement(String s, String s1, String element) throws SAXException {
		 indentCount--;
		 if (indentCount == 0) 
			 currentStanza.complete();
	}
	
	public List<Stanza> getStanzaList() {
		return stanzas;
	}

}
