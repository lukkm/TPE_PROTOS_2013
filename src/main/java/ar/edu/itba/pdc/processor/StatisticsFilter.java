package ar.edu.itba.pdc.processor;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import ar.edu.itba.pdc.jabber.JabberElement;
import ar.edu.itba.pdc.jabber.Message;
import ar.edu.itba.pdc.jabber.Presence;
import ar.edu.itba.pdc.stanzas.Stanza;

public class StatisticsFilter implements Filter{
	
	private static final int DEFAULT_INTERVAL = 600; 
	private static int interval = DEFAULT_INTERVAL;
	
	private boolean isEnabled = false;
	private long initialStatisticsTime = -1;
	
	private Map<String, PersonalStatistic> usersStatistics = null;

	public StatisticsFilter() {
		if (usersStatistics == null) {
			usersStatistics = new HashMap<String, PersonalStatistic>();
//			interval = desde archivo conf
		}
	}
	

	public void getStatistics() {
		int[] accesses = new int[getCurrentInterval()];
		int[] byteTransfer = new int[getCurrentInterval()];
		try {
			FileWriter fstream = new FileWriter("~/ProxyGlobalStatistics" + System.currentTimeMillis() + ".txt");
			BufferedWriter out = new BufferedWriter(fstream);
//			for (PersonalStatistic ps : usersStatistics) {
//				
//			}
			out.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	public void enableStatistics() {
		if (initialStatisticsTime == -1)
			beginStatistics();
		isEnabled = true;
	}
	
	public void disableStatistics() {
		isEnabled = false;
	}
	
	public void beginStatistics() {
		initialStatisticsTime = System.currentTimeMillis(); 
	}
	
	/* inicio clase interna */
	
	private class PersonalStatistic {
		
		Map<Integer,Integer> accessBetweenIntervals = new HashMap<Integer,Integer>();
		Map<Integer,Integer> bytesBetweenIntervals = new HashMap<Integer,Integer>();
		String jid = null;

		PersonalStatistic(String jid) {
			this.jid = jid;
		}
		
		private void applyFilter(Message m) {
			int position = getCurrentInterval();
			bytesBetweenIntervals.put(position, bytesBetweenIntervals.get(position) + m.getMessage().length());
		}

		/* Si el to esta en null es un mensaje broadcast y por lo tanto se esta conectando*/
		/* Pensar que pasa cuando retransmite este mensaje (bastante posible) */
		private void applyFilter(Presence p) {
			if (p.getTo() == null) {
				int position = getCurrentInterval();
				accessBetweenIntervals.put(position, accessBetweenIntervals.get(position) + 1);
			}
		}
		
		/* Si me manda algo q no sea un message o un presence */		
		private void applyFilter(JabberElement je) {
		}
	}
	
	/* fin clase interna */
	
	public void apply(Stanza stanza) {
		if (stanza.getElement() != null) {
			String from = stanza.getElement().getFrom();
			if (from != null) {
				if (!usersStatistics.containsKey(from)) {
					usersStatistics.put(from, new PersonalStatistic(from));
				}
				if (stanza.isMessage()) {
					usersStatistics.get(from).applyFilter((Message) stanza.getElement());
				} else if (stanza.isPresence()) {
					usersStatistics.get(from).applyFilter((Presence) stanza.getElement());
				}
			}
		}
	}
	
	private int getCurrentInterval() {
		return ((int)(System.currentTimeMillis()-initialStatisticsTime))%(interval*1000);
	}
}
