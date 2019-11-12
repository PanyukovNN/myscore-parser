package com.zylex.myscoreparser.controller.logger;

import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class BlockLogger extends ConsoleLogger {

    private static int blockNumber = 1;

    private AtomicLong blockStartTime = new AtomicLong(System.currentTimeMillis());

    private AtomicInteger processedGames = new AtomicInteger(0);

    private AtomicInteger blockGames = new AtomicInteger(0);

    private AtomicInteger processedArchives = new AtomicInteger(0);

    private AtomicInteger blockArchives = new AtomicInteger(0);

    private AtomicInteger processedSeasons = new AtomicInteger(0);

    private AtomicInteger blocSeasons = new AtomicInteger(0);

    private AtomicInteger blockPlayOffGames = new AtomicInteger(0);

    private AtomicInteger blockNoDataGames = new AtomicInteger(0);

    private AtomicInteger blockGamesArchiveExist = new AtomicInteger(0);

    private ParserLogger parserLogger;

    public BlockLogger(ParserLogger parserLogger) {
        this.parserLogger = parserLogger;
    }

    public void addBlockGamesArchiveExist(int gamesCount) {
        blockGamesArchiveExist.addAndGet(gamesCount);
    }

    public synchronized void startLogMessage(LogType type, Integer arg) {
        if (type == LogType.ARCHIVES) {
            blockArchives.set(arg);
            writeInLine(String.format("\nProcessing block №%d archives: ...", blockNumber));
        } else if (type == LogType.SEASONS) {
            blocSeasons.set(arg);
            writeInLine(String.format("\nProcessing block №%d seasons: ...", blockNumber));
        } else if (type == LogType.GAMES) {
            writeInLine(String.format("\nProcessing block №%d games data: ...", blockNumber));
        }
    }

    public synchronized void logArchive() {
        String output = String.format("Processing block №%d archives: %d/%d",
                blockNumber,
                processedArchives.incrementAndGet(),
                blockArchives.get());
        writeInLine(StringUtils.repeat("\b", output.length() + 1) + output);
    }

    public synchronized void logSeason(int gamesNumber) {
        parserLogger.addTotalGames(gamesNumber);
        blockGames.addAndGet(gamesNumber);
        String output = String.format("Processing block №%d seasons: %d/%d",
                blockNumber,
                processedSeasons.incrementAndGet(),
                blocSeasons.get());
        writeInLine(StringUtils.repeat("\b", output.length() + 1) + output);
    }

    public synchronized void logGame() {
        String output = String.format("Processing block №%d games data: %d/%d (%s%%)",
                blockNumber,
                processedGames.incrementAndGet() + blockGamesArchiveExist.get(),
                blockGames.get(),
                new DecimalFormat("#0.0")
                        .format(((double) (processedGames.get() + blockGamesArchiveExist.get()) / (double) (blockGames.get())) * 100)
                        .replace(",", "."));
        writeInLine(StringUtils.repeat("\b", output.length() + 1) + output);
    }

    public void blockSummarizing() {
        writeInLine(String.format("\nBlock №%d is completed in %s",
                blockNumber,
                computeTime(blockStartTime.get())));
        writeInLine(String.format("\nBlock №%d play-off games: %d",
                blockNumber,
                blockPlayOffGames.get()));
        writeInLine(String.format("\nBlock №%d games with no data: %d",
                blockNumber,
                blockNoDataGames.get()));
        writeLineSeparator();
        blockNumber++;
    }

    public void setPlayOffGames(int playOffGames) {
        parserLogger.addPlayOffGames(playOffGames);
        blockPlayOffGames.addAndGet(playOffGames);
    }

    public void setNoDataGames(int noDataGames) {
        parserLogger.addNoDataGames(noDataGames);
        blockNoDataGames.addAndGet(noDataGames);
    }
}
