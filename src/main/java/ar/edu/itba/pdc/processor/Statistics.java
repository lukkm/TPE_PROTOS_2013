package ar.edu.itba.pdc.processor;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import ar.edu.itba.pdc.jabber.Message;

public class Statistics {

	private boolean monitorStatus = false;
	
	private Map<String, PersonalStatistics> usersStatistics = null;

	public Statistics() {
		if (usersStatistics == null)
			usersStatistics = new HashMap<String, PersonalStatistics>();
	}

	public void getStatistics() {
		try {
			FileWriter fstream = new FileWriter("~/ProxyGlobalStatistics" + System.currentTimeMillis() + ".txt");
			BufferedWriter out = new BufferedWriter(fstream);
			out.write("Global Accesses to the proxy:" + globalAccess);
			out.write("Global Accesses to the proxy:" + globalBytesTransferred);
			for (String s : usersStatistics.keySet()) {
				
			}
			out.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	public boolean isMonitored() {
		return monitorStatus;
	}
	
	

	private class PersonalStatistics {

		List<>
		int access = 0;
		int bytesTransferred = 0;
		String jid = null;

		PersonalStatistics(String jid) {
			this.jid = jid;
		}
		
		void addAccess() {
			access++;
		}
		
		void countBytesInMessage (Message m) {
//			bytesTransferred += m.getLength();
		}
		
		
		

	}

}
