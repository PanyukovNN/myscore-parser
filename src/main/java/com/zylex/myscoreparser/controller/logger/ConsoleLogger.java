package com.zylex.myscoreparser.controller.logger;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.atomic.AtomicLong;

public abstract class ConsoleLogger {

    int blockNumber = 1;

    final AtomicLong programStartTime = new AtomicLong(System.currentTimeMillis());

    void writeLineSeparator() {
        writeInLine("\n" + StringUtils.repeat("-", 50));
    }

    synchronized void writeInLine(String message) {
        System.out.print(message);
    }

    String computeTime(long startTime) {
        long endTime = System.currentTimeMillis();
        long seconds = (endTime - startTime) / 1000;
        long minutes = seconds / 60;
        long houres = 0;
        if (minutes > 60) {
            houres = minutes / 60;
            minutes = minutes % 60;
        }
        return (houres == 0 ? "" : houres + "h. ")
                + minutes + " min. "
                + seconds % 60 + " sec.";
    }
}
