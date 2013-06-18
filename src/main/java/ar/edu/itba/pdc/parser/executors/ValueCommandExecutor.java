package ar.edu.itba.pdc.parser.executors;

import ar.edu.itba.pdc.filters.StatisticsFilter;
import ar.edu.itba.pdc.utils.ConfigurationCommands;

public class ValueCommandExecutor implements CommandExecutor {
	
	private static ValueCommandExecutor instance = null;
	private ConfigurationCommands commandManager;
	
	public static ValueCommandExecutor getInstance() {
		if (instance == null)
			instance = new ValueCommandExecutor();
		return instance;
	}
	
	private ValueCommandExecutor() {
		commandManager = ConfigurationCommands.getInstance();
	}
	
	public String execute(String command, String value) {	

		Integer newValue;
		
		if ((newValue = Integer.parseInt(value)) == null)
			return null;
		else {
			commandManager.setProperty(command, value);
			if (command.equals("interval"))
				StatisticsFilter.getInstance().setInterval(newValue);
			if (command.equals("byteUnit"))
				StatisticsFilter.getInstance().setByteUnit(newValue);
		}
		return "OK";
	}

}
