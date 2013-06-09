package ar.edu.itba.pdc.processor;

import java.util.HashSet;
import java.util.Set;

import ar.edu.itba.pdc.jabber.Message;
import ar.edu.itba.pdc.stanzas.Stanza;
import ar.edu.itba.pdc.utils.ConfigurationCommands;

public class SilentUsersFilter implements Filter{

	private Set<String> mapOfSilence = null;

	public SilentUsersFilter() {
		if (mapOfSilence == null)
			mapOfSilence = new HashSet<String>();
		String silentUsers = ConfigurationCommands.getInstance().getProperty("silentuser");
		for (String s : silentUsers.split(";")) {
			addSilencedUser(s);
		}
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
			for (String s : mapOfSilence) {
				if (from.contains(s) && s.length() > 0) {
					msg.setTo(from);
					msg.setFrom("admin@xmpp-proxy");
					msg.setMessage("You have been silenced!");
					stanza.reject();
					System.out.println("Rejecting user");
					return;
				}
			}
		}
	}
	
	
}
