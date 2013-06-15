package ar.edu.itba.pdc.filters;

import java.util.HashSet;
import java.util.Set;

import ar.edu.itba.pdc.jabber.Message;
import ar.edu.itba.pdc.stanzas.Stanza;
import ar.edu.itba.pdc.utils.ConfigurationCommands;

public class SilentUsersFilter implements Filter {

	private Set<String> mapOfSilence = null;
	private static SilentUsersFilter instance = null;
	
	public static SilentUsersFilter getInstance() {
		if (instance == null)
			instance = new SilentUsersFilter();
		return instance;
	}
	
	public SilentUsersFilter() {
		mapOfSilence = new HashSet<String>();
		String silent = ConfigurationCommands.getInstance().getProperty(
				"silenceuser");
		for (String s : silent.split(";")) {
			mapOfSilence.add(s);
		}
	}

	public void addSilencedUser(String jid) {
		if (mapOfSilence != null) {
			mapOfSilence.add(jid);
		}
	}

	public boolean isSilent(String jid) {
		for (String s : mapOfSilence) {
			if (jid.contains(s))
				return true;
		}
		return false;
	}

	public void removeSilentUser(String jid) {
		if (mapOfSilence != null)
			mapOfSilence.remove(jid);
	}

	public void apply(Stanza stanza) {
		if (stanza.isMessage()) {
			Message msg = ((Message) stanza.getElement());
			String from = msg.getFrom();
			String to = msg.getTo();
			if ((from != null && isSilent(from))) {
				msg.setTo(from);
				msg.setFrom("admin@xmpp-proxy");
				msg.setMessage("You have been silenced!");
				msg.setType("error");
				msg.setErrorCode(405);
				msg.setErrorType("cancel");
				msg.setErrorXMLBody("<not-allowed xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>");
				stanza.reject();
			} else if ((to != null && isSilent(to))) {
				stanza.reject();
			}
		}
	}

}
