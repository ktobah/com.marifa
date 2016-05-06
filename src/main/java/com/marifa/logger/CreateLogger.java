package com.marifa.logger;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

/**
 * This class handles the creation of loggers.
 * It is replaced by log4j, and kept for historical reference.
 */
public class CreateLogger {

    private static Logger log;
    private LogFormatter formatter;
    private ConsoleHandler consoleHandler;

    public CreateLogger() {
        formatter = new LogFormatter();
        log = Logger.getLogger(CreateLogger.class.getName());
        consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(formatter);
        log.setUseParentHandlers(false);
        log.addHandler(consoleHandler);
    }

    public Logger setLogger() {
        return log;
    }
}
