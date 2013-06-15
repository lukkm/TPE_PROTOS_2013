package ar.edu.itba.pdc.parser;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import ar.edu.itba.pdc.exceptions.BadSyntaxException;
import ar.edu.itba.pdc.utils.ConfigurationCommands;

public class AdminParser {

	private Map<String, CommandExecutor> commandTypes = new HashMap<String, CommandExecutor>();
	private ConfigurationCommands commandManager;

	public AdminParser() {
		commandManager = ConfigurationCommands.getInstance();
		commandTypes.put("silenceuser", ListCommandExecutor.getInstance());
		commandTypes.put("statistics", BooleanCommandExecutor.getInstance());
		commandTypes.put("monitor", BooleanCommandExecutor.getInstance());
		commandTypes.put("getStatistics", GetCommandExecutor.getInstance());
		commandTypes.put("transformation", BooleanCommandExecutor.getInstance());
		commandTypes.put("interval", BooleanCommandExecutor.getInstance());
	}

	public boolean parseCommand(ByteBuffer readBuffer, int bytesRead) throws BadSyntaxException {
		String fullCommand = new String(readBuffer.array()).substring(0, bytesRead);
		Map<String, String> commands = new HashMap<String, String>();
		for (String s : fullCommand.split(";")) {
			String[] aux = s.split("=");
			if (aux.length > 1) {
				commands.put(aux[0].trim(), aux[1].trim());
			} else {
				throw new BadSyntaxException();
			}
		}
		return takeActions(commands);
	}	

	private boolean takeActions(Map<String, String> commands) throws BadSyntaxException {
		
		if (commands.isEmpty()) {
			throw new BadSyntaxException();
		}
		for (String cmd : commands.keySet()) {
			boolean accepted = commandTypes.get(cmd).execute(cmd, commands.get(cmd));
			if (accepted) {
				commandManager.saveFile();
			}
			else
				throw new BadSyntaxException();
		}
		return true;
	}
}
