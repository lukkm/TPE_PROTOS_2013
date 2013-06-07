package ar.edu.itba.pdc.parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import ar.edu.itba.pdc.stanzas.Stanza;

public class XMPPParser {

	public List<Stanza> parse (ByteBuffer xmlStream, int length) throws SAXException, IOException, ParserConfigurationException {
		
		/* Medio feo, ver como se soluciona */
		byte[] array = new String(xmlStream.array()).substring(0, length).getBytes();
		
		InputStream is = new ByteArrayInputStream(array);
		
		SAXParserFactory factory = SAXParserFactory.newInstance();
	    factory.setNamespaceAware(true);
	    factory.setValidating(false);
	    SAXParser parser = factory.newSAXParser();
	    XMPPHandler handler = new XMPPHandler();
	    parser.parse(is, handler);
	   
	    return handler.getStanzaList();
	}
	
	
}
