package com.zylex.myscoreparser.controller;

import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ConsoleLogger {

    private static AtomicLong startTime = new AtomicLong(System.currentTimeMillis());

    public static AtomicInteger totalWithNoCoef = new AtomicInteger(0);

    public static AtomicInteger totalPlayOffRecords = new AtomicInteger(0);

    public static AtomicInteger totalRecords = new AtomicInteger(0);

    public static AtomicLong blockStartTime;

    public static AtomicInteger blockRecords = new AtomicInteger(0);

    public static AtomicInteger processedRecords = new AtomicInteger(0);

    public static int blockNumber = 1;

    public static AtomicInteger processedArchives = new AtomicInteger(0);

    public static AtomicInteger blockArchives = new AtomicInteger(0);

    public static AtomicInteger processedSeasons = new AtomicInteger(0);

    public static AtomicInteger blockLeagues = new AtomicInteger(0);

    public static void totalSummarizing() {
        long endTime = System.currentTimeMillis();
        long seconds = (endTime - startTime.get()) / 1000;
        long minutes = seconds / 60;
        long houres = 0;
        if (minutes > 60) {
            houres = minutes / 60;
            minutes = minutes % 60;
        }
        System.out.println("\nTotal time: "
                + (houres == 0 ? "" : houres + "h. ")
                + minutes + " min. "
                + seconds % 60 + " sec.");
        System.out.println("Total records: " + totalRecords);
        System.out.println("Total records without coeffitients: " + totalWithNoCoef);
        System.out.println("Total play-off records: " + totalPlayOffRecords);
    }

    public static synchronized void logRecord() {
        String output = "Processing block №" + ConsoleLogger.blockNumber + " coefficients: " + new DecimalFormat("#00.00").format(((double) processedRecords.get() / (double) blockRecords.get()) * 100) + "%";
        writeInLine(StringUtils.repeat("\b", output.length()) + output);
    }

    public static synchronized void logArchive() {
        String output = "Processing block №" + blockNumber + " archives: " + processedArchives + " out of " + blockArchives;
        writeInLine(StringUtils.repeat("\b", output.length()) + output);
    }

    public static synchronized void logSeason() {
        String output = "Processing block №" + blockNumber + " seasons: " + processedSeasons + " out of " + blockLeagues;
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
        System.out.println("\nBlock №" + (blockNumber - 1) + " is finished.");
        System.out.println("------------------------------------------");
        processedRecords.set(0);
    }
}
