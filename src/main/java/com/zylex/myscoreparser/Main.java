package com.zylex.myscoreparser;

import com.zylex.myscoreparser.repository.Repository;
import com.zylex.myscoreparser.service.ParseProcessor;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    public static AtomicInteger totalWithNoCoef = new AtomicInteger(0);

    public static AtomicInteger totalRecords = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        System.setProperty("webdriver.chrome.driver", "C:\\java\\external-files\\chromedriver.exe");
        long startTime = System.currentTimeMillis();
        try {
            Repository repository = Repository.getInstance();
            ParseProcessor parseProcessor = new ParseProcessor(repository);
            parseProcessor.process();
        } finally {
            summarizing(startTime);
        }
    }

    private static void summarizing(long startTime) {
        long endTime = System.currentTimeMillis();
        long seconds = (endTime - startTime) / 1000;
        long minutes = seconds / 60;
        System.out.println("Total time: " + minutes + " min. " + seconds % 60 + " sec.");
        System.out.println("Total records: " + totalRecords);
        System.out.println("Total records without coeffitients: " + totalWithNoCoef);
    }
}