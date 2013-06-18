package ar.edu.itba.pdc.parser.executors;

import org.apache.commons.codec.binary.Base64;

import ar.edu.itba.pdc.utils.ConfigurationCommands;


public class AuthService implements CommandExecutor{

	private static AuthService instance = null;
	private String password = "secreto546";
	private ConfigurationCommands commandManager;
	
	public static AuthService getInstance() {
		if (instance == null)
			instance = new AuthService();
		return instance;
	}
	
	private AuthService() { 
		this.commandManager = ConfigurationCommands.getInstance();
		if (commandManager.hasProperty("password"))
			this.password = new String(Base64.decodeBase64(commandManager.getProperty("password").getBytes()));
	}
	
	public String execute(String command, String value) {
		if (command.equals("auth"))
			return checkAuth(value);
		else if (command.equals("changePassword"))
			return passwordChange(value);
		return null;
	}
	
	private String passwordChange(String newPassword) {
		this.password = newPassword;
		this.commandManager.setProperty("password", new String(Base64.encodeBase64(newPassword.getBytes())));
		return "OK";
	}
	
	private String checkAuth(String value) {
		if (value.equals(password))
			return "PASSWORD OK";
		return "INVALID PASSWORD";
	}

}
