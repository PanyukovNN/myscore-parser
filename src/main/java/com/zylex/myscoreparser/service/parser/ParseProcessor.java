package com.zylex.myscoreparser.service.parser;

import com.zylex.myscoreparser.controller.ConsoleLogger;
import com.zylex.myscoreparser.controller.LogType;
import com.zylex.myscoreparser.exceptions.ParseProcessorException;
import com.zylex.myscoreparser.model.Game;
import com.zylex.myscoreparser.repository.GameRepository;
import com.zylex.myscoreparser.repository.LeagueRepository;
import com.zylex.myscoreparser.service.DriverManager;
import com.zylex.myscoreparser.service.parser.gamestrategy.CallableCoefficientParser;
import com.zylex.myscoreparser.service.parser.gamestrategy.CallableStatisticsParser;
import com.zylex.myscoreparser.service.parser.gamestrategy.ParserType;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class ParseProcessor {

    private DriverManager driverManager;

    private ExecutorService service;

    private GameRepository gameRepository;

    private LeagueRepository leagueRepository;

    private ParserType parserType;

    public ParserType getParserType() {
        return parserType;
    }

    public ParseProcessor(DriverManager driverManager, GameRepository gameRepository, LeagueRepository leagueRepository) {
        this.driverManager = driverManager;
        this.gameRepository = gameRepository;
        this.leagueRepository = leagueRepository;
        this.parserType = gameRepository.getParserType();
    }

    public List<Game> process() {
        try {
            driverManager.initiateDrivers();
            service = Executors.newFixedThreadPool(driverManager.getThreads());
            gameRepository.readArchiveGames();
            List<List<String>> leagueSeasonLinksList = leagueRepository.readDiscreteLeaguesFromFile(driverManager.getThreads());
            for (List<String> leagueSeasonLinks : leagueSeasonLinksList) {
                processBlock(leagueSeasonLinks);
            }
        } finally {
            service.shutdown();
            driverManager.quitDrivers();
            ConsoleLogger.totalSummarizing();
        }
        return gameRepository.getArchiveGames();
    }

    private void processBlock(List<String> leagueSeasonLinks) {
        ConsoleLogger.blockStartTime = new AtomicLong(System.currentTimeMillis());
        try {
            ConsoleLogger.startLogMessage(LogType.ARCHIVES, leagueSeasonLinks.size());
            List<String> archiveLinks = processArchiveLinks(leagueSeasonLinks);
            ConsoleLogger.startLogMessage(LogType.SEASONS, archiveLinks.size());
            List<List<Game>> leagueGames = processLeagueGames(archiveLinks);
            ConsoleLogger.startLogMessage(LogType.GAMES, null);
            processBlockGames(leagueGames);
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
            callableLeagueParsers.add(new CallableLeagueParser(driverManager, gameRepository, archiveLink));
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

    private void processBlockGames(List<List<Game>> gamesList) throws InterruptedException, ExecutionException {
        List<Callable<List<Game>>> callableGameParsers = new ArrayList<>();
        for (List<Game> games : gamesList) {
            if (parserType == ParserType.COEFFICIENTS) {
                callableGameParsers.add(new CallableCoefficientParser(driverManager, gameRepository.getArchiveGames(), games));
            } else if (parserType == ParserType.STATISTICS) {
                callableGameParsers.add(new CallableStatisticsParser(driverManager, gameRepository.getArchiveGames(), games));
            }
        }
        for (Future<List<Game>> future : service.invokeAll(callableGameParsers)) {
            future.get();
        }
    }
}
