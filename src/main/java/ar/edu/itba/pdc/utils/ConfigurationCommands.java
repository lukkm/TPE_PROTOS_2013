package ar.edu.itba.pdc.utils;

import java.io.FileInputStream;
import java.util.Properties;

public class ConfigurationCommands {
	private Properties props;
	private FileInputStream fis;

	public ConfigurationCommands() {
		this.props = new Properties();
		try {
			String current = new java.io.File(".").getCanonicalPath();
			this.fis = new FileInputStream(
					current
							+ "/src/main/java/ar/edu/itba/pdc/resources/parsedcommands.properties");
			props.load(fis);

		} catch (Exception e) {
			System.out.println("IO Error"); // TODO
		}
	}

	public String getStatistic() {
		return props.get("statistics").toString();
	}

	public String getSilencedUsers() {
		return props.get("silencedusers").toString();
	}

	public String getTransformation() {
		return props.get("transformations").toString();
	}

	public String getInterval() {
		return props.get("interval").toString();
	}

}
