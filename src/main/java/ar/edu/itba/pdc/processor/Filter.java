package ar.edu.itba.pdc.processor;

import ar.edu.itba.pdc.stanzas.Stanza;

public interface Filter {
	public void applyFilter(Stanza stanza);
}
