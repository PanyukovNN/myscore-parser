package com.zylex.myscoreparser;

import com.zylex.myscoreparser.controller.ConsoleLogger;
import com.zylex.myscoreparser.controller.Saver;
import com.zylex.myscoreparser.model.Record;
import com.zylex.myscoreparser.repository.Repository;
import com.zylex.myscoreparser.service.DriverFactory;
import com.zylex.myscoreparser.service.ParseProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class Main {

    public static void main(String[] args) {
        try {
            System.setProperty("webdriver.chrome.silentOutput", "true");
            java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);
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
                System.out.println(discreteList.indexOf(list) + " block is finished.\n" + "Starting new block...");
            }
        } finally {
            DriverFactory.quitDrivers();
            ConsoleLogger.summarizing();
        }
    }
}