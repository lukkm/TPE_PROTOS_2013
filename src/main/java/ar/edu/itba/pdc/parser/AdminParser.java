package ar.edu.itba.pdc.parser;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import ar.edu.itba.pdc.exceptions.BadSyntaxException;
import ar.edu.itba.pdc.parser.executors.AddToListCommandExecutor;
import ar.edu.itba.pdc.parser.executors.AuthService;
import ar.edu.itba.pdc.parser.executors.BooleanCommandExecutor;
import ar.edu.itba.pdc.parser.executors.CommandExecutor;
import ar.edu.itba.pdc.parser.executors.GetCommandExecutor;
import ar.edu.itba.pdc.parser.executors.RemoveFromListCommandExecutor;
import ar.edu.itba.pdc.utils.ConfigurationCommands;

public class AdminParser {

	private Map<String, CommandExecutor> commandTypes = new HashMap<String, CommandExecutor>();
	private ConfigurationCommands commandManager;

	/**
	 * Creates the AdminParser and set the command names with their appropriate
	 * executors.
	 */

	public AdminParser() {
		commandManager = ConfigurationCommands.getInstance();
		commandTypes.put("silenceuser", AddToListCommandExecutor.getInstance());
		commandTypes.put("statistics", BooleanCommandExecutor.getInstance());
		commandTypes.put("monitor", BooleanCommandExecutor.getInstance());
		commandTypes.put("getStatistics", GetCommandExecutor.getInstance());
		commandTypes
				.put("transformation", BooleanCommandExecutor.getInstance());
		commandTypes.put("interval", BooleanCommandExecutor.getInstance());
		commandTypes.put("unsilenceuser",
				RemoveFromListCommandExecutor.getInstance());
		commandTypes.put("auth", AuthService.getInstance());
		commandTypes.put("changePassword", AuthService.getInstance());
	}

	/**
	 * Parses a command from the read buffer and validates that it is a valid
	 * sentence inside our defined protocol.
	 * 
	 * @param readBuffer
	 * @param bytesRead
	 * @return
	 * @throws BadSyntaxException
	 */

	public String parseCommand(ByteBuffer readBuffer, int bytesRead)
			throws BadSyntaxException {

		String fullCommand = new String(readBuffer.array()).substring(0,
				bytesRead);
		Map<String, String> commands = new HashMap<String, String>();
		for (String s : fullCommand.split(";")) {

			String[] aux = s.split("=");
			String trimmed = aux[0].trim();
			if (commandTypes.containsKey(trimmed)) {
				if (aux.length > 1) {
					commands.put(trimmed, aux[1].trim());
				} else {
					commands.put(trimmed, "");
				}
			} else if (trimmed.isEmpty())
				return null;
			else
				throw new BadSyntaxException();
		}

		return takeActions(commands);
	}

	/**
	 * Once the commands were parsed, takes the appropriate action using the
	 * executors stored in the commandTypes map.
	 * 
	 * @param commands
	 * @return
	 * @throws BadSyntaxException
	 */

	private String takeActions(Map<String, String> commands)
			throws BadSyntaxException {

		String responseToAdmin = null;
		for (String cmd : commands.keySet()) {
			responseToAdmin = commandTypes.get(cmd).execute(cmd,
					commands.get(cmd));

			if (responseToAdmin != null) {
				commandManager.saveFile();
			} else {
				throw new BadSyntaxException();
			}
		}
		return responseToAdmin + '\n';
	}

}
