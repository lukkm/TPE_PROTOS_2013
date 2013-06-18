package ar.edu.itba.pdc.parser.executors;

import ar.edu.itba.pdc.filters.StatisticsFilter;
import ar.edu.itba.pdc.utils.ConfigurationCommands;

public class GetCommandExecutor extends AbstractCommandExecutor {

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
	
	public String execute(String command, String value) {
		commandManager.saveFile();
		String ans = null;
		if (command.equals("getStatistics")) {
			getLogger().info("Statistics answered to administrator");
			ans = StatisticsFilter.getInstance().execute();
		} else if (command.equals("monitor")) {
			//TODO
		}
		return ans;
	}
}
