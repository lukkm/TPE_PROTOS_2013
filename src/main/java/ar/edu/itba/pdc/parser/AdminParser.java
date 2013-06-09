package ar.edu.itba.pdc.parser;

import java.nio.ByteBuffer;

import org.json.JSONException;
import org.json.JSONObject;

import ar.edu.itba.pdc.utils.ConfigurationCommands;

public class AdminParser {

	private String[] commands = { "silenceuser", "statistics", "getStatistics",
			"setTransformation", "interval" };
	private ConfigurationCommands commandManager;

	public AdminParser() {
		commandManager = ConfigurationCommands.getInstance();
	}

//	private void saveFile(Properties props) {
//		String current = "";
//		FileOutputStream ops = null;
//		try {
//			current = new java.io.File(".").getCanonicalPath();
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		try {
//			ops = new FileOutputStream(
//					current
//							+ "/src/main/java/ar/edu/itba/pdc/resources/parsedcommands.properties",
//					false);
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		try {
//			props.store(ops, "Commands");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

	public boolean parseCommand(ByteBuffer readBuffer) {
		JSONObject json = new JSONObject(readBuffer.array()); 
//		JSONObject jsonObj = null; //PARA TEST
//		JSONObject jsonObj2 = null; //PARA TEST
//		try {
//			jsonObj = new JSONObject("{silenceuser:juanjo; statistics:luqui}");
//			jsonObj2 = new JSONObject("{silenceuser:pedro; statistics:luqi}");
//		} catch (JSONException e) {
//			System.out.println("Bad JSON Syntaxis");
//		}
//
//		jsonParse(jsonObj);
		return jsonParse(json);

	}

	private boolean jsonParse(JSONObject jsonObject) {
		String[] keys = JSONObject.getNames(jsonObject); // Multiples mensajes
		for (String string : keys) {
			for (String cmd : this.commands) {
				if (string.compareTo(cmd) == 0) {
					String s = commandManager.getProperty(cmd);

					String t = "";
					try {
						t = jsonObject.get(cmd).toString();
						if (s != null
								&& s.contains(t.subSequence(0, t.length()))) {
							break;
						}
					} catch (JSONException e) {
						System.out.println("Error JSON");
					}
					System.out.println("s :" + s);
					System.out.println("t :" + t);

					if (s != null)
						commandManager.setProperty(cmd, s + ";" + t);
					else
						commandManager.setProperty(cmd, t);
					//saveFile(p);
					break;
				}
			}
		}
		return true;
	}
}
