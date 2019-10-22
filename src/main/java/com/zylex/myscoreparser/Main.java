package com.zylex.myscoreparser;

import com.zylex.myscoreparser.controller.ConsoleLogger;
import com.zylex.myscoreparser.controller.Saver;
import com.zylex.myscoreparser.model.Record;
import com.zylex.myscoreparser.repository.Repository;
import com.zylex.myscoreparser.service.DriverFactory;
import com.zylex.myscoreparser.service.ParseProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;


public class Main {

    public static void main(String[] args) {
        try {
            Repository repository = Repository.getInstance();
            List<String> leagueLinks = repository.readLeaguesFromFile();
            List<List<String>> discreteList = new ArrayList<>();
            Saver saver = new Saver();
            while (true) {
                if (leagueLinks.size() <= DriverFactory.THREADS) {
                    discreteList.add(leagueLinks);
                    break;
                }
                discreteList.add(leagueLinks.subList(0, DriverFactory.THREADS));
                leagueLinks = leagueLinks.subList(DriverFactory.THREADS, leagueLinks.size());
            }
            for (List<String> list : discreteList) {
                ConsoleLogger.blockStartTime = new AtomicLong(System.currentTimeMillis());
                ParseProcessor parseProcessor = new ParseProcessor(list);
                List<Record> records = parseProcessor.process();
                saver.processSaving(records);
            }
        } finally {
            DriverFactory.quitDrivers();
            ConsoleLogger.totalSummarizing();
        }
    }
}