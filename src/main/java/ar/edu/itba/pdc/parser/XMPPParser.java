package ar.edu.itba.pdc.parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import ar.edu.itba.pdc.exceptions.IncompleteElementsException;
import ar.edu.itba.pdc.stanzas.Stanza;

public class XMPPParser {

	public List<Stanza> parse (ByteBuffer xmlStream, int length) throws ParserConfigurationException, IOException, IncompleteElementsException {
		
		String xmlString = new String(xmlStream.array());
		xmlString = xmlString.substring(0, xmlStream.position());
		
		if(xmlString.contains("<stream:")) {
			Stanza s = new Stanza();
			s.setXMLString(xmlString);
			List<Stanza> streamList = new LinkedList<Stanza>();
			streamList.add(s);
			return streamList;
		} else {		
			String newString = "<xmpp-proxy>" + xmlString + "</xmpp-proxy>";
			byte[] xmlBytes = newString.getBytes();
			System.out.println("Parsing XML: " + newString);
			InputStream is = new ByteArrayInputStream(xmlBytes);
			SAXParserFactory factory = SAXParserFactory.newInstance();
		    factory.setNamespaceAware(false);
		    factory.setValidating(false);
		    SAXParser parser;
			try {
				parser = factory.newSAXParser();
				XMPPHandler handler = new XMPPHandler();
				parser.parse(is, handler);
				if (handler.hasIncompleteElements()) {
					throw new IncompleteElementsException();
				}
				return handler.getStanzaList();
			} catch (SAXException e) {
				throw new IncompleteElementsException();
			}
		}
	   
	}
	
	
}
