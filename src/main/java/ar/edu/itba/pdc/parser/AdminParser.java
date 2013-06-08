package ar.edu.itba.pdc.parser;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.util.Properties;

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

	private void parseCommand(ByteBuffer readBuffer) {
		String str = new String(readBuffer.array());
		this.loadParser().setProperty(key, value);
	}
}
