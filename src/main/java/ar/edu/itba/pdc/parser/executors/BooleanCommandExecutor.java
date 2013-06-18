package ar.edu.itba.pdc.parser.executors;

import ar.edu.itba.pdc.utils.ConfigurationCommands;

public class BooleanCommandExecutor extends AbstractCommandExecutor {

	private static BooleanCommandExecutor instance = null;
	private ConfigurationCommands commandManager;
	
	
	public static BooleanCommandExecutor getInstance() {
		if (instance == null)
			instance = new BooleanCommandExecutor();
		return instance;
	}
	
	public BooleanCommandExecutor() {
		commandManager = ConfigurationCommands.getInstance();
	}
	
	public String execute(String command, String value) {
		String valueLower = value.toLowerCase();
		if (!valueLower.equals("enabled") && !valueLower.equals("disabled")) {
			getLogger().info("Syntax error trying to set property " + command);
			return null;
		}
		getLogger().info("Set property " + command + " with value " + value);
		commandManager.setProperty(command, value);
		return "OK";
	}
}