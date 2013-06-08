package ar.edu.itba.pdc.processor;

import java.util.HashSet;
import java.util.Set;

public class SilentUsers {

	private Set<String> mapOfSilence = null;

	public SilentUsers() {
		if (mapOfSilence == null)
			mapOfSilence = new HashSet<String>();
	}

	public void addSilentUser(String jid) {
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
}
