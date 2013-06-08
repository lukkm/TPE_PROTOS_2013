package ar.edu.itba.pdc.parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Properties;

import org.json.JSONException;
import org.json.JSONObject;

public class AdminParser {

	public static void main(String[] args) throws IOException {
		AdminParser a = new AdminParser();
		a.parseCommand(null);
	}

	private Properties loadFile() {
		final Properties props = new Properties();
		try {
			String current = new java.io.File(".").getCanonicalPath();
			FileInputStream fis = new FileInputStream(
					current
							+ "/src/main/java/ar/edu/itba/pdc/resources/parsedcommands.properties");
			props.load(fis);

		} catch (Exception e) {
			System.out.println("IO Error"); // TODO
		}

		return props;
	}

	private void saveFile(Properties props) {
		String current = "";
		FileOutputStream ops = null;
		try {
			current = new java.io.File(".").getCanonicalPath();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			ops = new FileOutputStream(
					current
							+ "/src/main/java/ar/edu/itba/pdc/resources/parsedcommands.properties",
					false);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			props.store(ops, "Commands");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean parseCommand(ByteBuffer readBuffer) {
		// JSONObject str = new JSONObject(readBuffer.array()); TODO DESCOMENTAR
		JSONObject jsonObj = null;
		JSONObject jsonObj2 = null;
		try {
			jsonObj = new JSONObject(
					"{silenceuser:juanjo;}");
			jsonObj2 = new JSONObject(
					"{silenceuser:pedro;}");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		jsonParse(jsonObj);
		return jsonParse(jsonObj2);
		
	}

	private boolean jsonParse(JSONObject jsonObject) {
		Properties p;
		String[] keys = JSONObject.getNames(jsonObject); // Multiples mensajes
		for (String string : keys) {
			try {
				if (string.compareTo("silenceuser") == 0) {
					p = loadFile();
					String s = p.getProperty("silenceuser");
					p.setProperty("silenceuser", s+"{"+jsonObject.get("silenceuser")
							.toString());
					saveFile(p);
					return true;
				}
				if (string.compareTo("statistics") == 0) {
					this.loadFile().setProperty("statistics",
							jsonObject.get("statistics").toString());
					return true;
				}
				if (string.compareTo("getStatistics") == 0) {
					this.loadFile().setProperty("transformation",
							jsonObject.get("transformation").toString());
					return true;
				}
				if (string.compareTo("setTransformation") == 0) {
					this.loadFile().setProperty("silenceuser",
							jsonObject.get("silenceuser").toString());
					return true;
				}
				if (string.compareTo("silenceuser") == 0) {

					this.loadFile().setProperty("silenceuser",
							jsonObject.get("silenceuser").toString());
					return true;
				}
				if (string.compareTo("silenceuser") == 0) {
					this.loadFile().setProperty("silenceuser",
							jsonObject.get("silenceuser").toString());
					return true;
				}
			} catch (JSONException e) {
				return false;
			}
		}
		return true;
	}
}
