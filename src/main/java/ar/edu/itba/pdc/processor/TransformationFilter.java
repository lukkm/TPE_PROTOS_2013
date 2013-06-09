package ar.edu.itba.pdc.processor;

import ar.edu.itba.pdc.stanzas.Stanza;

public class TransformationFilter implements Filter{
	
	char[] changedVocabulary = {};
	
	public void apply(Stanza stanza) {
		if (stanza.isMessage()) {
			
		}
	}

}
