package ar.edu.itba.pdc.processor;

import ar.edu.itba.pdc.jabber.Message;
import ar.edu.itba.pdc.stanzas.Stanza;

public class TransformationFilter implements Filter{
	
	char[] changedVocabulary = "4B<D3FGH1JKLMN0PQRSTUVWQYZ4b<d3fgh1jklmn0pqrstuvwqyz".toCharArray();
	
	public void apply(Stanza stanza) {
		String transformedMessage = "";
		if (stanza.isMessage()) {
			for (char c : ((Message)stanza.getElement()).getMessage().toCharArray())
				transformedMessage += changedVocabulary[c - 'A'];
			((Message)stanza.getElement()).setMessage(transformedMessage);
		}
	}

}
