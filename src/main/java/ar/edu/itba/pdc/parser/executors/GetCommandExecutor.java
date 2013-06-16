package ar.edu.itba.pdc.parser.executors;

import ar.edu.itba.pdc.utils.ConfigurationCommands;

public class GetCommandExecutor implements CommandExecutor{

	private static GetCommandExecutor instance = null;
	private ConfigurationCommands commandManager;
	
	public static GetCommandExecutor getInstance() {
		if (instance == null)
			instance = new GetCommandExecutor();
		return instance;
	}
	
	public GetCommandExecutor() {
		commandManager = ConfigurationCommands.getInstance();
	}
	
	public boolean execute(String command, String value) {
		commandManager.saveFile(); //???
		return false;
	}
}
