package ar.edu.itba.pdc.processor;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import ar.edu.itba.pdc.jabber.Message;
import ar.edu.itba.pdc.stanzas.Stanza;

public class Statistics implements Filter{
	
	private static final int DEFAULT_INTERVAL = 600; 
	private static int interval = DEFAULT_INTERVAL;
	
	private boolean monitorStatus = false;
	private long initialMonitoringTime;
	
	private Map<String, PersonalStatistic> usersStatistics = null;

	public Statistics() {
		if (usersStatistics == null) {
			usersStatistics = new HashMap<String, PersonalStatistic>();
//			interval = desde archivo conf
		}
	}
	

	public void getStatistics() {
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
	
	public boolean isMonitored() {
		return monitorStatus;
	}
	
	public void beginMonitor() {
		initialMonitoringTime = System.currentTimeMillis(); 
	}
	
	
	
	private class PersonalStatistic {
		
		Map<Integer,Integer> accessBetweenIntervals = new HashMap<Integer,Integer>();
		Map<Integer,Integer> bytesBetweenIntervals = new HashMap<Integer,Integer>();
		String jid = null;

		PersonalStatistic(String jid) {
			this.jid = jid;
		}
		
		void applyFilter(Message m) {
			int position = getInterval();
			bytesBetweenIntervals.put(position, bytesBetweenIntervals.get(position) + m.getMessage().length());
		}
		
//		void applyFilter(Presence p) {
//			/* Si el to esta en null es un mensaje broadcast y por lo tanto se esta conectando*/
//			/* Pensar que pasa cuando retransmite este mensaje (bastante posible) */
//			if (p.to == null) {
//				int position = getInterval();
//				accessBetweenIntervals.put(position, accessBetweenIntervals.get(position) + 1);
//			}
//		}
		
		int getInterval() {
			return ((int)(System.currentTimeMillis()-initialMonitoringTime))%(interval*1000);
		}
		
	}



	public void applyFilter(Stanza stanza) {
		String from = stanza.getElement().getFrom();
		if (!usersStatistics.containsKey(from)) {
			usersStatistics.put(from, new PersonalStatistic(from));
		}
//		usersStatistics.get(from).applyFilter(stanza.getElement());
	}

}
