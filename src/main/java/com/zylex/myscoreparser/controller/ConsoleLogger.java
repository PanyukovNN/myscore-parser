package com.zylex.myscoreparser.controller;

import com.google.common.util.concurrent.AtomicDouble;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ConsoleLogger {

    public static AtomicInteger totalWithNoCoef = new AtomicInteger(0);

    public static AtomicInteger totalPlayOffRecords = new AtomicInteger(0);

    public static AtomicInteger totalRecords = new AtomicInteger(0);

    public static AtomicInteger recordsProcessed = new AtomicInteger(0);

    public static AtomicDouble progress = new AtomicDouble(0);

    public static AtomicLong startTime = new AtomicLong(System.currentTimeMillis());

    public static void summarizing() {
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
        System.out.println("Total records: " + totalRecords);
        System.out.println("Total records without coeffitients: " + totalWithNoCoef);
        System.out.println("Total play-off records: " + totalPlayOffRecords);
    }
}
