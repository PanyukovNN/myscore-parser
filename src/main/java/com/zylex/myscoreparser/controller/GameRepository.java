package com.zylex.myscoreparser.controller;

import com.zylex.myscoreparser.exceptions.GameRepositoryException;
import com.zylex.myscoreparser.model.Coefficient;
import com.zylex.myscoreparser.model.Game;
import com.zylex.myscoreparser.model.StatisticsValue;
import com.zylex.myscoreparser.service.parser.parsing_strategy.ParserType;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class GameRepository {

    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd;HH:mm");

    private String[] bookmakers = {"1XBET", "Winline", "Leon"};

    private List<Game> archiveGames = new ArrayList<>();

    private ParserType parserType;

    public GameRepository(ParserType parserType) {
        this.parserType = parserType;
    }

    public List<Game> getArchiveGames() {
        return archiveGames;
    }

    public ParserType getParserType() {
        return parserType;
    }

    public void readArchiveGames() {
        try {
            File file = new File(String.format("results/%s.csv", parserType.archiveName));
            List<String> lines = new ArrayList<>();
            if (file.exists()) {
                lines = Files.readAllLines(file.toPath());
            }
            for (String line : lines) {
                String[] fields = line.replace(",", ".").split(";");
                Game game = new Game(fields[0], fields[1], fields[2], LocalDateTime.parse(fields[3] + ";" + fields[4], DATE_FORMATTER), fields[5], fields[6],
                        Integer.parseInt(fields[7]), Integer.parseInt(fields[8]), fields[9]);
                if (parserType == ParserType.COEFFICIENTS) {
                    readCoefficients(fields, game);
                } else if (parserType == ParserType.STATISTICS) {
                    readStatistics(fields, game);
                }
                archiveGames.add(game);
            }
        } catch (IOException e) {
            throw new GameRepositoryException(e.getMessage(), e);
        }
    }

    private void readCoefficients(String[] fields, Game game) {
        Map<String, Coefficient> coefficients = game.getCoefficients();
        coefficients.put(bookmakers[0], getCoefficient(bookmakers[0], Arrays.copyOfRange(fields, 10, 17)));
        coefficients.put(bookmakers[1], getCoefficient(bookmakers[1], Arrays.copyOfRange(fields, 17, 24)));
        coefficients.put(bookmakers[2], getCoefficient(bookmakers[2], Arrays.copyOfRange(fields, 24, 31)));
    }

    private static Coefficient getCoefficient(String bookmaker, String[] fields) {
        Coefficient coefficient = new Coefficient(bookmaker, fields[0], fields[1], fields[2], fields[3], fields[4]);
        coefficient.setDch1X(fields[5]);
        coefficient.setDchX2(fields[6]);
        return coefficient;
    }

    private void readStatistics(String[] fields, Game game) {
        Map<String, StatisticsValue> statisticsItems = game.getStatisticsItems();
        for (int i = 0; i < 17; i++) {
            int index = (i * 2) + 10;
            String[] temp = fields[index].split(":");
            String itemName = temp[0];
            String homeValue = temp[1];
            String awayValue = fields[index + 1];
            statisticsItems.put(itemName, new StatisticsValue(homeValue, awayValue));
        }
    }
}
