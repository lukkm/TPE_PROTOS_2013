package ar.edu.itba.pdc.processor;

import java.util.HashSet;
import java.util.Set;

import ar.edu.itba.pdc.jabber.Message;
import ar.edu.itba.pdc.stanzas.Stanza;

public class SilentUsers implements Filter{

	private Set<String> mapOfSilence = null;

	public SilentUsers() {
		if (mapOfSilence == null)
			mapOfSilence = new HashSet<String>();
	}

	public void addSilencedUser(String jid) {
		if (mapOfSilence != null) {
			mapOfSilence.add(jid);
		}
	}

	public boolean isSilent(String jid) {
		return mapOfSilence.contains(jid);
	}

	public void removeSilentUser(String jid) {
		if (mapOfSilence != null)
			mapOfSilence.remove(jid);
	}

	public void applyFilter(Stanza stanza) {
		String from = ((Message) stanza.getElement()).getFrom();
		if (stanza.isMessage() && mapOfSilence.contains(from)) {
			((Message)stanza.getElement()).setTo(from);
		}
	}
	
	
}
