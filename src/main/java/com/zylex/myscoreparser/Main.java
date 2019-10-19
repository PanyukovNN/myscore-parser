package com.zylex.myscoreparser;

import com.zylex.myscoreparser.model.Record;
import com.zylex.myscoreparser.repository.Repository;
import com.zylex.myscoreparser.service.ParseProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    public static AtomicInteger totalWithNoCoef = new AtomicInteger(0);

    public static AtomicInteger totalPlayOffRecords = new AtomicInteger(0);

    public static AtomicInteger totalRecords = new AtomicInteger(0);

    public static AtomicInteger recordsProcessed = new AtomicInteger(0);

    public static void main(String[] args) {
        DriverFactory.THREADS = 8;
        long startTime = System.currentTimeMillis();
        try {
            Repository repository = Repository.getInstance();
            List<String> leagueLinks = repository.readLeaguesFromFile();
            List<List<String>> discreteList = new ArrayList<>();
            while (true) {
                discreteList.add(leagueLinks.subList(0, DriverFactory.THREADS));
                leagueLinks = leagueLinks.subList(DriverFactory.THREADS, leagueLinks.size());
                if (leagueLinks.size() < DriverFactory.THREADS) {
                    discreteList.add(leagueLinks);
                    break;
                }
            }
            for (List<String> list : discreteList) {
                ParseProcessor parseProcessor = new ParseProcessor(list);
                List<Record> records = parseProcessor.process();
                Saver saver = new Saver();
                String fileNumbers = String.format("%d-%d", discreteList.indexOf(list) * list.size() + 1, ((discreteList.indexOf(list) + 1) * list.size()));
                saver.processSaving(fileNumbers, records);
            }
        } finally {
            DriverFactory.quitDrivers();
            summarizing(startTime);
        }
    }

    private static void summarizing(long startTime) {
        long endTime = System.currentTimeMillis();
        long seconds = (endTime - startTime) / 1000;
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