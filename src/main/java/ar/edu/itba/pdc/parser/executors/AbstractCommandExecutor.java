package ar.edu.itba.pdc.parser.executors;

import ar.edu.itba.pdc.logger.XMPPLogger;

public abstract class AbstractCommandExecutor implements CommandExecutor { 

	private XMPPLogger logger = XMPPLogger.getInstance();

	protected XMPPLogger getLogger() {
		return logger;
	}
	
}
