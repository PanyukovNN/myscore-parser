package com.zylex.myscoreparser.service.parser;

import com.zylex.myscoreparser.controller.logger.BlockLogger;
import com.zylex.myscoreparser.controller.logger.LogType;
import com.zylex.myscoreparser.controller.logger.ParserLogger;
import com.zylex.myscoreparser.exceptions.ParseProcessorException;
import com.zylex.myscoreparser.model.Game;
import com.zylex.myscoreparser.controller.GameRepository;
import com.zylex.myscoreparser.controller.LeagueRepository;
import com.zylex.myscoreparser.service.DriverManager;
import com.zylex.myscoreparser.service.parser.parsing_strategy.CallableCoefficientParser;
import com.zylex.myscoreparser.service.parser.parsing_strategy.CallableStatisticsParser;
import com.zylex.myscoreparser.service.parser.parsing_strategy.ParserType;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class ParseProcessor {

    private ParserLogger logger = new ParserLogger();

    private BlockLogger blockLogger;

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
            logger.totalSummarizing();
        }
        return gameRepository.getArchiveGames();
    }

    private void processBlock(List<String> leagueSeasonLinks) {
        blockLogger = new BlockLogger(logger);
        try {
            List<String> archiveLinks = processArchiveLinks(leagueSeasonLinks);
            List<List<Game>> leagueGames = processLeagueGames(archiveLinks);
            processBlockGames(leagueGames);
        } catch (InterruptedException | ExecutionException e) {
            throw new ParseProcessorException(e.getMessage(), e);
        } finally {
            blockLogger.blockSummarizing();
        }
    }

    private List<String> processArchiveLinks(List<String> leagueSeasonLinks) throws InterruptedException, ExecutionException {
        blockLogger.startLogMessage(LogType.ARCHIVES, leagueSeasonLinks.size());
        List<CallableArchiveParser> callableArchiveParsers = new ArrayList<>();
        for (String countryLeague : leagueSeasonLinks) {
            callableArchiveParsers.add(new CallableArchiveParser(blockLogger, driverManager, countryLeague));
        }
        List<String> archiveLinks = new ArrayList<>();
        for (Future<List<String>> future : service.invokeAll(callableArchiveParsers)) {
            archiveLinks.addAll(future.get());
        }
        return archiveLinks;
    }

    private List<List<Game>> processLeagueGames(List<String> archiveLinks) throws InterruptedException, ExecutionException {
        blockLogger.startLogMessage(LogType.SEASONS, archiveLinks.size());
        List<CallableSeasonParser> callableSeasonParsers = new ArrayList<>();
        for (String archiveLink : archiveLinks) {
            callableSeasonParsers.add(new CallableSeasonParser(blockLogger, driverManager, gameRepository, archiveLink));
        }
        List<Future<List<Game>>> futureLeagueGames = service.invokeAll(callableSeasonParsers);
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
        blockLogger.startLogMessage(LogType.GAMES, null);
        List<Callable<List<Game>>> callableGameParsers = new ArrayList<>();
        for (List<Game> games : gamesList) {
            if (parserType == ParserType.COEFFICIENTS) {
                callableGameParsers.add(new CallableCoefficientParser(blockLogger, driverManager, gameRepository.getArchiveGames(), games));
            } else if (parserType == ParserType.STATISTICS) {
                callableGameParsers.add(new CallableStatisticsParser(blockLogger, driverManager, gameRepository.getArchiveGames(), games));
            }
        }
        for (Future<List<Game>> future : service.invokeAll(callableGameParsers)) {
            future.get();
        }
    }
}
