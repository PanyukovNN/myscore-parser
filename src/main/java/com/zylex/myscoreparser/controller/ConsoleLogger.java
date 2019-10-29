package com.zylex.myscoreparser.controller;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;

public class ConsoleLogger {

    private static AtomicLong programStartTime = new AtomicLong(System.currentTimeMillis());

    public static AtomicInteger totalWithNoCoef = new AtomicInteger(0);

    public static AtomicInteger totalPlayOffGames = new AtomicInteger(0);

    private static AtomicInteger totalGames = new AtomicInteger(0);

    public static AtomicLong blockStartTime;

    private static AtomicInteger blockGames = new AtomicInteger(0);

    private static AtomicInteger processedGames = new AtomicInteger(0);

    static int blockNumber = 1;

    private static AtomicInteger processedArchives = new AtomicInteger(0);

    private static AtomicInteger blockArchives = new AtomicInteger(0);

    private static AtomicInteger processedSeasons = new AtomicInteger(0);

    private static AtomicInteger blockLeagues = new AtomicInteger(0);

    private static int threads;

    private static int processedDrivers = 0;

    static {
        @SuppressWarnings("unchecked")
        List<Logger> loggers = Collections.<Logger>list(LogManager.getCurrentLoggers());
        loggers.add(LogManager.getRootLogger());
        loggers.forEach(logger -> logger.setLevel(org.apache.log4j.Level.OFF));
        System.setProperty("webdriver.chrome.silentOutput", "true");
        java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);
    }

    public static synchronized void startLogMessage(LogType type, Integer arg) {
        if (type == LogType.DRIVERS) {
            threads = arg;
            writeInLine("Starting chrome drivers: 0/" + arg);
        } else if (type == LogType.ARCHIVES) {
            blockArchives.set(arg);
            writeInLine(String.format("\nProcessing block №%d archives: 0/%d",
                    blockNumber,
                    blockArchives.get()));
        } else if (type == LogType.SEASONS) {
            blockLeagues.set(arg);
            writeInLine(String.format("\nProcessing block №%d seasons: 0/%d",
                    blockNumber,
                    blockLeagues.get()));
        } else if (type == LogType.GAMES) {
            writeInLine(String.format("\nProcessing block №%d coefficients: 0/%d (0.0%%)",
                    blockNumber,
                    blockGames.get()));
        }
    }

    public static synchronized void logDriver() {
        String output = String.format("Starting chrome drivers: %d/%d",
                ++processedDrivers,
                threads);
        writeInLine(StringUtils.repeat("\b", output.length()) + output);
        if (processedDrivers == threads) {
            writeLineSeparator();
        }
    }

    public static synchronized void logArchive() {
        String output = String.format("Processing block №%d archives: %d/%d",
                blockNumber,
                processedArchives.incrementAndGet(),
                blockArchives.get());
        writeInLine(StringUtils.repeat("\b", output.length()) + output);
    }

    public static synchronized void logSeason(int gamesSize) {
        totalGames.addAndGet(gamesSize);
        blockGames.addAndGet(gamesSize);
        String output = String.format("Processing block №%d seasons: %d/%d",
                blockNumber,
                processedSeasons.incrementAndGet(),
                blockLeagues.get());
        writeInLine(StringUtils.repeat("\b", output.length()) + output);
    }

    public static synchronized void logGame() {
        String output = String.format("Processing block №%d coefficients: %d/%d (%s%%)",
                blockNumber,
                processedGames.incrementAndGet(),
                blockGames.get(),
                new DecimalFormat("#0.0").format(((double) processedGames.get() / (double) blockGames.get()) * 100).replace(",", "."));
        writeInLine(StringUtils.repeat("\b", output.length()) + output);
    }

    static void blockSummarizing() {
        writeInLine(String.format("\nBlock №%d is completed in %s",
                blockNumber - 1,
                computeTime(blockStartTime.get())));
        writeLineSeparator();
        processedArchives.set(0);
        processedSeasons.set(0);
        processedGames.set(0);
        blockGames.set(0);
    }

    public static void totalSummarizing() {
        writeInLine(String.format("\nTotal games: %d\n", totalGames.get()));
        writeInLine(String.format("Total play-off games: %d\n", totalPlayOffGames.get()));
        writeInLine(String.format("Total games without coeffitients: %d\n", totalWithNoCoef.get()));
        writeInLine(String.format("Parsing completed in %s", computeTime(programStartTime.get())));
    }

    private static String computeTime(long startTime) {
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

    private static void writeLineSeparator() {
        writeInLine("\n" + StringUtils.repeat("-", 50));
    }

    private static synchronized void writeInLine(String message) {
        System.out.print(message);
    }
}
