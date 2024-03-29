package com.zylex.myscoreparser.controller.logger;

import java.util.concurrent.atomic.AtomicInteger;

public class ParserLogger extends ConsoleLogger {

    private AtomicInteger totalGames = new AtomicInteger(0);

    private AtomicInteger totalWithNoData = new AtomicInteger(0);

    private AtomicInteger totalPlayOffGames = new AtomicInteger(0);

    public synchronized void totalSummarizing() {
        writeInLine(String.format("\nTotal games: %d\n", totalGames.get()));
        writeInLine(String.format("Total play-off games: %d\n", totalPlayOffGames.get()));
        writeInLine(String.format("Total games with no data: %d\n", totalWithNoData.get()));
        writeInLine(String.format("Parsing completed in %s\n", computeTime(programStartTime.get())));
    }

    void addTotalGames(int gamesNumber) {
        totalGames.addAndGet(gamesNumber);
    }

    synchronized void addPlayOffGames(int playOffGames) {
//        System.out.println(playOffGames);
        totalPlayOffGames.addAndGet(playOffGames);
    }

    void addNoDataGames(int noDataGames) {
        totalWithNoData.addAndGet(noDataGames);
    }
}
