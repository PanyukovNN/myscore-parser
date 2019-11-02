package com.zylex.myscoreparser.controller.logger;

import org.apache.commons.lang3.StringUtils;

public class DriverLogger extends ConsoleLogger {

    private int threads;

    private int processedDrivers = 0;

    public synchronized void startLogMessage(LogType type, Integer arg) {
        if (type == LogType.DRIVERS) {
            threads = arg;
            writeInLine("Starting chrome drivers: 0/" + arg);
        }
    }

    public synchronized void logDriver() {
        String output = String.format("Starting chrome drivers: %d/%d",
                ++processedDrivers,
                threads);
        writeInLine(StringUtils.repeat("\b", output.length()) + output);
        if (processedDrivers == threads) {
            writeLineSeparator();
        }
    }
}
