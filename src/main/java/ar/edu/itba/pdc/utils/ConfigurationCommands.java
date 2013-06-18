package ar.edu.itba.pdc.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import ar.edu.itba.pdc.logger.XMPPLogger;

public class ConfigurationCommands {
	private Properties props;
	private FileInputStream fis;

	private static ConfigurationCommands instance;
	private XMPPLogger logger = XMPPLogger.getInstance();

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
			logger.warn("Error loading properties");
		}
	}

	public String getProperty(String property) {
		if (props.get(property) == null)
			return "";
		return props.get(property).toString();
	}

	public void setProperty(String property, String value) {
		logger.info("Applied " + property + " to " + value);
		props.setProperty(property, value);
	}

	public boolean hasProperty(String property) {
		return props.containsKey(property);
	}

	public void saveFile() {
		String current = "";
		FileOutputStream ops = null;
		try {
			current = new java.io.File(".").getCanonicalPath();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			ops = new FileOutputStream(
					current
							+ "/src/main/java/ar/edu/itba/pdc/resources/parsedcommands.properties",
					false);
		} catch (FileNotFoundException e) {
			logger.warn("Error setting properties");
		}
		try {
			props.store(ops, "Commands");
		} catch (IOException e) {
			logger.warn("Error setting properties");
		}
	}

}
