package ar.edu.itba.pdc.processor;

import java.util.HashSet;
import java.util.Set;

import ar.edu.itba.pdc.jabber.Message;
import ar.edu.itba.pdc.stanzas.Stanza;

public class SilentUsersFilter implements Filter{

	private Set<String> mapOfSilence = null;

	public SilentUsersFilter() {
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

	public void apply(Stanza stanza) {
		if (stanza.isMessage()) {
			Message msg = ((Message)stanza.getElement());
			String from = msg.getFrom();
			if (mapOfSilence.contains(from)) {
				msg.setTo(from);
				msg.setFrom("admin@xmpp-proxy");
				msg.setMessage("You have been silenced!");
				stanza.reject();
			}
		}
	}
	
	
}
