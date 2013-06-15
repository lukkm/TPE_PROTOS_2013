package ar.edu.itba.pdc.parser;

import ar.edu.itba.pdc.utils.ConfigurationCommands;

public class BooleanCommandExecutor implements CommandExecutor{

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
	
	public boolean execute(String command, String value) {
		String valueLower = value.toLowerCase();
		if (!valueLower.equals("enabled") && !valueLower.equals("disabled"))
			return false;
		commandManager.setProperty(command, value);
		return true;
	}
}