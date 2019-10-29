package com.zylex.myscoreparser.service;

import com.zylex.myscoreparser.controller.ConsoleLogger;
import com.zylex.myscoreparser.controller.LogType;
import com.zylex.myscoreparser.controller.Saver;
import com.zylex.myscoreparser.exceptions.ParseProcessorException;
import com.zylex.myscoreparser.model.Game;
import com.zylex.myscoreparser.repository.Repository;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class ParseProcessor {

    private DriverManager driverManager;

    private ExecutorService service;

    private Repository repository;

    public void process(DriverManager driverManager, Repository repository) {
        this.driverManager = driverManager;
        this.service = Executors.newFixedThreadPool(driverManager.getThreads());
        this.repository = repository;
        try {
            List<List<String>> leagueSeasonLinksList = repository.readDiscreteLeaguesFromFile(driverManager.getThreads());
            for (List<String> leagueSeasonLinks : leagueSeasonLinksList) {
                processBlock(leagueSeasonLinks);
            }
        } finally {
            service.shutdown();
            driverManager.quitDrivers();
            new Saver().processArchiveSaving(repository);
        }
    }

    private void processBlock(List<String> leagueSeasonLinks) {
        ConsoleLogger.blockStartTime = new AtomicLong(System.currentTimeMillis());
        try {
            ConsoleLogger.startLogMessage(LogType.ARCHIVES, leagueSeasonLinks.size());
            List<String> archiveLinks = processArchiveLinks(leagueSeasonLinks);
            ConsoleLogger.startLogMessage(LogType.SEASONS, archiveLinks.size());
            List<List<Game>> leagueGames = processLeagueGames(archiveLinks);
            ConsoleLogger.startLogMessage(LogType.GAMES, null);
            processCoefficients(leagueGames);
        } catch (InterruptedException | ExecutionException e) {
            throw new ParseProcessorException(e.getMessage(), e);
        } finally {
            ConsoleLogger.blockSummarizing();
        }
    }

    private List<String> processArchiveLinks(List<String> leagueSeasonLinks) throws InterruptedException, ExecutionException {
        List<CallableArchiveParser> callableArchiveParsers = new ArrayList<>();
        for (String countryLeague : leagueSeasonLinks) {
            callableArchiveParsers.add(new CallableArchiveParser(driverManager, countryLeague));
        }
        List<String> archiveLinks = new ArrayList<>();
        for (Future<List<String>> future : service.invokeAll(callableArchiveParsers)) {
            archiveLinks.addAll(future.get());
        }
        return archiveLinks;
    }

    private List<List<Game>> processLeagueGames(List<String> archiveLinks) throws InterruptedException, ExecutionException {
        List<CallableLeagueParser> callableLeagueParsers = new ArrayList<>();
        for (String archiveLink : archiveLinks) {
            callableLeagueParsers.add(new CallableLeagueParser(driverManager, repository, archiveLink));
        }
        List<Future<List<Game>>> futureLeagueGames = service.invokeAll(callableLeagueParsers);
        List<List<Game>> gamesLinks = new ArrayList<>();
        for (Future<List<Game>> futureLeagueGame : futureLeagueGames) {
            gamesLinks.add(futureLeagueGame.get());
        }
        return sortGameLinks(gamesLinks);
    }

    private List<List<Game>> sortGameLinks(List<List<Game>> gamesLinks) {
        Comparator<List<Game>> sizeComparator = (o1, o2) -> Integer.compare(o2.size(), o1.size());
        Comparator<List<Game>> seasonComparator = Comparator.comparingInt(this::getSeasonStartYear);
        gamesLinks = gamesLinks.stream()
                .sorted(sizeComparator.thenComparing(seasonComparator))
                .collect(Collectors.toList());
        return gamesLinks;
    }

    private int getSeasonStartYear(List<Game> games) {
        if (games.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(games.get(0).getSeason().substring(0, 4));
    }

    private void processCoefficients(List<List<Game>> gamesList) throws InterruptedException, ExecutionException {
        List<CallableCoefficientParser> callableCoefficientParsers = new ArrayList<>();
        for (List<Game> games : gamesList) {
            callableCoefficientParsers.add(new CallableCoefficientParser(driverManager, repository.getArchiveGames(), games));
        }
        for (Future<List<Game>> future : service.invokeAll(callableCoefficientParsers)) {
            future.get();
        }
    }
}
