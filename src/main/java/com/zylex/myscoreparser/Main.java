package com.zylex.myscoreparser;

import com.zylex.myscoreparser.processor.ConnectionProcessor;
import com.zylex.myscoreparser.processor.ParseArchive;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    public static AtomicInteger totalWithNoCoef = new AtomicInteger(0);

    public static AtomicInteger totalRecords = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        System.setProperty("webdriver.chrome.driver", "C:\\java\\external-files\\chromedriver.exe");
        String[] countryLeagues = {"australia/a-league",
                "austria/tipico-bundesliga",
                "austria/2-liga",
                "azerbaijan/premier-league",
                "algeria/division-1",
                "england/championship",
                "argentina/superliga",
                "argentina/primera-nacional",
                "armenia/premier-league",
                "bahrain/premier-league",
                "belarus/vysshaya-liga",
                "belgium/jupiler-league",
                "belgium/proximus-league"};
        Map<String, List<String>> archive = ParseArchive.processArchive(countryLeagues);
        ExecutorService service = Executors.newFixedThreadPool(6);
        List<ConnectionProcessor> futureList = new ArrayList<>();
        for (String countryLeague : countryLeagues) {
            for (String leagueHref : archive.get(countryLeague)) {
                futureList.add(new ConnectionProcessor(leagueHref));
            }
        }
        service.invokeAll(futureList);
        service.shutdown();
        summarizing(startTime);
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