package ar.edu.itba.pdc.processor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

public class Statistics {

	private int globalAccess = 0;
	private int globalBytesTransferred = 0;
	private Map<String, PersonalStatistics> usersStatistics = null;

	public Statistics() {
		if (usersStatistics == null)
			usersStatistics = new HashMap<String, PersonalStatistics>();
	}

	public void getStatistics() {
		File f = new File();
		try {
			FileWriter fstream = new FileWriter("~/XMPPproxyStatistics" + System.currentTimeMillis() + ".txt");
			BufferedWriter out = new BufferedWriter(fstream);
			out.write("Global Accesses to the proxy");
			out.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}

	}

	private class PersonalStatistics {

		int access = 0;
		int bytesTransferred = 0;
		String jid = null;

		PersonalStatistics(String jid) {
			this.jid = jid;
		}

	}

}
