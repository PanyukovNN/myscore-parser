package com.zylex.myscoreparser.controller;

import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ConsoleLogger {

    public static AtomicInteger totalWithNoCoef = new AtomicInteger(0);

    public static AtomicInteger totalPlayOffRecords = new AtomicInteger(0);

    public static AtomicInteger totalRecords = new AtomicInteger(0);

    public static AtomicLong startTime = new AtomicLong(System.currentTimeMillis());

    public static AtomicLong blockStartTime;

    public static AtomicInteger blockRecords = new AtomicInteger(0);

    public static AtomicInteger processedRecords = new AtomicInteger(0);

    public static int blockNumber = 1;

    public static void totalSummarizing() {
        long endTime = System.currentTimeMillis();
        long seconds = (endTime - startTime.get()) / 1000;
        long minutes = seconds / 60;
        long houres = 0;
        if (minutes > 60) {
            houres = minutes / 60;
            minutes = minutes % 60;
        }
        System.out.println("Total time: "
                + (houres == 0 ? "" : houres + "h. ")
                + minutes + " min. "
                + seconds % 60 + " sec.");
        System.out.println("\nTotal records: " + totalRecords);
        System.out.println("Total records without coeffitients: " + totalWithNoCoef);
        System.out.println("Total play-off records: " + totalPlayOffRecords);
    }

    public static void logRecord() {
        System.out.print("\b\b\b\b\b\b" + new DecimalFormat("#00.00").format(((double) processedRecords.get() / (double) blockRecords.get()) * 100) + "%");
    }

    public static void writeLine(String message) {
        System.out.println(message);
    }

    public static void writeInLine(String message) {
        System.out.print(message);
    }

    public static void writeBlockSeparator() {
        System.out.println("------------------------------------------\n");
    }

    public static void blockSummarizing() {
        System.out.println("Block №" + blockNumber + " is finished.\nStarting new block...");
        writeBlockSeparator();
        processedRecords.set(0);
    }
}
