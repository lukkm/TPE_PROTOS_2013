package ar.edu.itba.pdc.logger;

import java.io.IOException;

import org.apache.log4j.FileAppender;
import org.apache.log4j.HTMLLayout;
import org.apache.log4j.Logger;

public class XMPPLogger {

	private static XMPPLogger instance;
	private Logger logger;

	private XMPPLogger() throws IOException {
		logger = Logger.getLogger(XMPPLogger.class);
		logger.addAppender(new FileAppender(new HTMLLayout(), "logs.html"));
	}

	public static XMPPLogger getInstance() {
		try {
			if (instance == null)
				instance = new XMPPLogger();
		} catch (IOException e) {
			System.out.println("Error opening the logger");
		}
		return instance;
	}

	public void info(Object message) {
		logger.info(message);
	}

	public void warn(Object message) {
		logger.warn(message);
	}

	public void debug(Object message) {
		logger.debug(message);
	}

	public void error(Object message) {
		logger.error(message);
	}

	public void error(Object message, Throwable t) {
		logger.error(message, t);
	}
}
