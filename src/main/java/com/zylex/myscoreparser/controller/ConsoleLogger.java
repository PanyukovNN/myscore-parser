package com.zylex.myscoreparser.controller;

import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ConsoleLogger {

    private static AtomicLong programStartTime = new AtomicLong(System.currentTimeMillis());

    public static AtomicInteger totalWithNoCoef = new AtomicInteger(0);

    public static AtomicInteger totalPlayOffRecords = new AtomicInteger(0);

    private static AtomicInteger totalRecords = new AtomicInteger(0);

    public static AtomicLong blockStartTime;

    public static AtomicInteger blockRecords = new AtomicInteger(0);

    private static AtomicInteger processedRecords = new AtomicInteger(0);

    public static int blockNumber = 1;

    private static AtomicInteger processedArchives = new AtomicInteger(0);

    public static AtomicInteger blockArchives = new AtomicInteger(0);

    private static AtomicInteger processedSeasons = new AtomicInteger(0);

    public static AtomicInteger blockLeagues = new AtomicInteger(0);

    public static synchronized void logArchive() {
        String output = String.format("Processing block №%d archives: %d/%d",
                blockNumber,
                processedArchives.incrementAndGet(),
                blockArchives.get());
        writeInLine(StringUtils.repeat("\b", output.length()) + output);
    }

    public static synchronized void logSeason(int recordsSize) {
        totalRecords.addAndGet(recordsSize);
        blockRecords.addAndGet(recordsSize);
        String output = String.format("Processing block №%d seasons: %d/%d",
                blockNumber,
                processedSeasons.incrementAndGet(),
                blockLeagues.get());
        writeInLine(StringUtils.repeat("\b", output.length()) + output);
    }

    public static synchronized void logRecord() {
        String output = String.format("Processing block №%d coefficients: %d/%d (%s%%)",
                blockNumber,
                processedRecords.incrementAndGet(),
                blockRecords.get(),
                new DecimalFormat("#0.0").format(((double) processedRecords.get() / (double) blockRecords.get()) * 100).replace(",", "."));
        writeInLine(StringUtils.repeat("\b", output.length()) + output);
    }

    public static void dropBlockLog() {
        processedArchives.set(0);
        processedSeasons.set(0);
        processedRecords.set(0);
        blockRecords.set(0);
    }

    public static synchronized void writeInLine(String message) {
        System.out.print(message);
    }

    static void blockSummarizing() {
        System.out.println(String.format("\nBlock №%d is completed in %s",
                blockNumber - 1,
                computeTime(blockStartTime.get())));
        System.out.print(StringUtils.repeat("-", 50));
        processedRecords.set(0);
    }

    public static void totalSummarizing() {
        System.out.println("\nTotal records: " + totalRecords);
        System.out.println("Total play-off records: " + totalPlayOffRecords);
        System.out.println("Total records without coeffitients: " + totalWithNoCoef);
        System.out.println("Parsing completed in " + computeTime(programStartTime.get()));
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
}
