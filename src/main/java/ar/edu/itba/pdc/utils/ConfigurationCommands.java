package ar.edu.itba.pdc.utils;

import java.io.FileInputStream;
import java.util.Properties;

public class ConfigurationCommands {
	private Properties props;
	private FileInputStream fis;

	private static ConfigurationCommands instance;
	
	public static ConfigurationCommands getInstance() {
		if (instance == null) 
			instance = new ConfigurationCommands();
		return instance;
	}
	
	private ConfigurationCommands() {
		this.props = new Properties();
		try {
			String current = new java.io.File(".").getCanonicalPath();
			this.fis = new FileInputStream(
					current
							+ "/src/main/java/ar/edu/itba/pdc/resources/parsedcommands.properties");
			props.load(fis);

		} catch (Exception e) {
			System.out.println("IO Error");
		}
	}

	public String getProperty(String property) {
		return props.get(property).toString();
	}

	public void setProperty(String property, String value) {
		props.setProperty(property, value);
	}

}
