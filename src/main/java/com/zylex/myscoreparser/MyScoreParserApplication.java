package com.zylex.myscoreparser;

import com.zylex.myscoreparser.controller.ConsoleLogger;
import com.zylex.myscoreparser.controller.Saver;
import com.zylex.myscoreparser.model.Game;
import com.zylex.myscoreparser.repository.Repository;
import com.zylex.myscoreparser.service.DriverManager;
import com.zylex.myscoreparser.service.ParseProcessor;

import java.util.List;

public class MyScoreParserApplication {

    public static void main(String[] args) {
        int threads = Integer.parseInt(args[0]);
        Context context = Context.getInstance();
        context.init();
        Repository repository = context.getRepository();
        DriverManager driverManager = context.getDriverFactory(threads);
        ParseProcessor parseProcessor = context.getParseProcessor();
        Saver saver = context.getSaver();
        String dirName = saver.createDirectory();
        try {
            repository.readDiscreteLeaguesFromFile(threads).forEach(leagueList -> {
                List<Game> games = parseProcessor.process(driverManager, leagueList);
                saver.processSaving(dirName, games);
            });
        } finally {
            driverManager.quitDrivers();
            ConsoleLogger.totalSummarizing();
        }
    }
}