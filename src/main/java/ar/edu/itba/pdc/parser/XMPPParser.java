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

import ar.edu.itba.pdc.stanzas.Stanza;

public class XMPPParser {

	public List<Stanza> parse (ByteBuffer xmlStream, int length) throws SAXException, IOException, ParserConfigurationException {
		
		/* Medio feo, ver como se soluciona */
		String xmlString = new String(xmlStream.array()).substring(0, length);
		
		/* Cambiar (Esto es porq el elemento stream nunca cierra y crashea) */
		if(xmlString.contains("<stream:stream")) {
			System.out.println("Empieza el stream!");
			return new LinkedList<Stanza>();
		} else {		
			/* Cambiar, atado a la implementacion anterior */
			byte[] xmlBytes = xmlString.getBytes();
			InputStream is = new ByteArrayInputStream(xmlBytes);
			SAXParserFactory factory = SAXParserFactory.newInstance();
		    factory.setNamespaceAware(true);
		    factory.setValidating(false);
		    SAXParser parser = factory.newSAXParser();
		    XMPPHandler handler = new XMPPHandler();
		    parser.parse(is, handler);
		    return handler.getStanzaList();
		}
		    
	}
	
	
}
