package ar.edu.itba.pdc.parser;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.util.Properties;

import org.json.JSONException;
import org.json.JSONObject;

public class AdminParser {

	private Properties loadParser() {
		final Properties props = new Properties();
		try {
			String current = new java.io.File(".").getCanonicalPath();
			FileInputStream fis = new FileInputStream(current
					+ "/src/main/resources/parsedcommands.properties");
			props.load(fis);

		} catch (Exception e) {
			System.out.println("IO Error"); // TODO
		}
		return props;
	}

	public boolean parseCommand(ByteBuffer readBuffer) {
		JSONObject str = new JSONObject(readBuffer.array());
		return jsonParse(str);

	}

	private boolean jsonParse(JSONObject str) {
		String[] keys = JSONObject.getNames(str);
		for (String string : keys) {
			try {
				if (string.compareTo("silenceuser") == 0) {
					this.loadParser().setProperty("silenceuser",
							str.get("silenceuser").toString());
					return true;
				}
				if (string.compareTo("statistics") == 0) {
					this.loadParser().setProperty("statistics",
							str.get("silenceuser").toString());
					return true;
				}
				if (string.compareTo("silenceuser") == 0) {
					this.loadParser().setProperty("silenceuser",
							str.get("silenceuser").toString());
					return true;
				}
				if (string.compareTo("silenceuser") == 0) {
					this.loadParser().setProperty("silenceuser",
							str.get("silenceuser").toString());
					return true;
				}
				if (string.compareTo("silenceuser") == 0) {
					this.loadParser().setProperty("silenceuser",
							str.get("silenceuser").toString());
					return true;
				}
				if (string.compareTo("silenceuser") == 0) {
					this.loadParser().setProperty("silenceuser",
							str.get("silenceuser").toString());
					return true;
				}
			} catch (JSONException e) {
				return false;
			}
		}
		return true;
	}
}
