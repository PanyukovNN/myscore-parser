package com.zylex.myscoreparser.service;

import com.zylex.myscoreparser.controller.ConsoleLogger;
import com.zylex.myscoreparser.controller.LogType;
import com.zylex.myscoreparser.exceptions.ParseProcessorException;
import com.zylex.myscoreparser.model.Game;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class ParseProcessor {

    private DriverFactory driverFactory;

    private ExecutorService service;

    public List<Game> process(DriverFactory driverFactory, List<String> leagueSeasonLinks) {
        this.driverFactory = driverFactory;
        this.service = Executors.newFixedThreadPool(driverFactory.getThreads());
        ConsoleLogger.blockStartTime = new AtomicLong(System.currentTimeMillis());
        try {
            ConsoleLogger.startLogMessage(LogType.ARCHIVES, leagueSeasonLinks.size());
            List<String> archiveLinks = processArchiveLinks(leagueSeasonLinks);
            ConsoleLogger.startLogMessage(LogType.SEASONS, archiveLinks.size());
            List<List<Game>> leagueGames = processLeagueGames(archiveLinks);
            ConsoleLogger.startLogMessage(LogType.GAMES, null);
            return processCoefficients(leagueGames);
        } catch (InterruptedException | ExecutionException e) {
            throw new ParseProcessorException(e.getMessage(), e);
        } finally {
            service.shutdown();
        }
    }

    private List<String> processArchiveLinks(List<String> leagueSeasonLinks) throws InterruptedException, ExecutionException {
        List<CallableArchiveParser> callableArchiveParsers = new ArrayList<>();
        for (String countryLeague : leagueSeasonLinks) {
            callableArchiveParsers.add(new CallableArchiveParser(driverFactory, countryLeague));
        }
        List<Future<List<String>>> futureArchiveParsers = service.invokeAll(callableArchiveParsers);
        return convertFutureArchiveLinks(futureArchiveParsers);
    }

    private List<String> convertFutureArchiveLinks(List<Future<List<String>>> futureArchiveParsers) throws InterruptedException, ExecutionException {
        List<String> archiveLinks = new ArrayList<>();
        for (Future<List<String>> future : futureArchiveParsers) {
            archiveLinks.addAll(future.get());
        }
        return archiveLinks;
    }

    private List<List<Game>> processLeagueGames(List<String> archiveLinks) throws InterruptedException, ExecutionException {
        List<CallableLeagueParser> callableLeagueParsers = new ArrayList<>();
        for (String archiveLink : archiveLinks) {
            callableLeagueParsers.add(new CallableLeagueParser(driverFactory, archiveLink));
        }
        List<Future<List<Game>>> futureLeagueGames = service.invokeAll(callableLeagueParsers);
        List<List<Game>> gamesLinks = convertFutureLeagueGames(futureLeagueGames);
        Comparator<List<Game>> sizeComparator = (o1, o2) -> Integer.compare(o2.size(), o1.size());
        Comparator<List<Game>> seasonComparator = Comparator.comparingInt(this::getSeasonStartYear);
        return gamesLinks.stream()
                .sorted(sizeComparator.thenComparing(seasonComparator))
                .collect(Collectors.toList());
    }

    private List<List<Game>> convertFutureLeagueGames(List<Future<List<Game>>> futureLeagueGames) throws InterruptedException, ExecutionException {
        List<List<Game>> gamesLinks = new ArrayList<>();
        for (Future<List<Game>> futureLeagueGame : futureLeagueGames) {
            gamesLinks.add(futureLeagueGame.get());
        }
        return gamesLinks;
    }

    private int getSeasonStartYear(List<Game> games) {
        return Integer.parseInt(games.get(0).getSeason().substring(0, 4));
    }

    private List<Game> processCoefficients(List<List<Game>> gamesList) throws InterruptedException, ExecutionException {
        List<CallableCoefficientParser> callableCoefficientParsers = new ArrayList<>();
        for (List<Game> games : gamesList) {
            callableCoefficientParsers.add(new CallableCoefficientParser(driverFactory, games));
        }
        List<Future<List<Game>>> futureGamesLists = service.invokeAll(callableCoefficientParsers);
        return processTotalGames(futureGamesLists);
    }

    private List<Game> processTotalGames(List<Future<List<Game>>> futureGamesLists) throws InterruptedException, ExecutionException {
        List<Game> games = new ArrayList<>();
        for (Future<List<Game>> futureGamesList : futureGamesLists) {
            List<Game> gameList = futureGamesList.get();
            games.addAll(gameList);
        }
        return games;
    }
}
