package com.zylex.myscoreparser;

import com.zylex.myscoreparser.controller.ConsoleLogger;
import com.zylex.myscoreparser.controller.Saver;
import com.zylex.myscoreparser.model.Record;
import com.zylex.myscoreparser.repository.Repository;
import com.zylex.myscoreparser.service.DriverFactory;
import com.zylex.myscoreparser.service.ParseProcessor;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        try {
            Repository repository = Repository.getInstance();
            List<String> leagueLinks = repository.readLeaguesFromFile();
            List<List<String>> discreteList = new ArrayList<>();
            while (true) {
                if (leagueLinks.size() <= DriverFactory.THREADS) {
                    discreteList.add(leagueLinks);
                    break;
                }
                discreteList.add(leagueLinks.subList(0, DriverFactory.THREADS));
                leagueLinks = leagueLinks.subList(DriverFactory.THREADS, leagueLinks.size());
            }
            for (List<String> list : discreteList) {
                ParseProcessor parseProcessor = new ParseProcessor(list);
                List<Record> records = parseProcessor.process();
                Saver saver = new Saver();
                saver.processSaving(String.valueOf(discreteList.indexOf(list)), records);
            }
        } finally {
            DriverFactory.quitDrivers();
            ConsoleLogger.summarizing(startTime);
        }
    }
}