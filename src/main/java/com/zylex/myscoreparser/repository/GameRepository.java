package com.zylex.myscoreparser.repository;

import com.zylex.myscoreparser.exceptions.ArchiveException;
import com.zylex.myscoreparser.model.Coefficient;
import com.zylex.myscoreparser.model.Game;
import com.zylex.myscoreparser.service.parser.gamestrategy.ParserType;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class GameRepository {

    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd;HH:mm");

    private String[] bookmakers = {"1XBET", "Winline", "Leon"};

    private List<Game> archiveGames;

    private Set<String> leagueSeasons = new HashSet<>();

    public List<Game> getArchiveGames() {
        return archiveGames;
    }

    public Set<String> getLeagueSeasons() {
        return leagueSeasons;
    }

    private ParserType parserType;

    public GameRepository(ParserType parserType) {
        this.parserType = parserType;
    }

    public ParserType getParserType() {
        return parserType;
    }

    public void readArchiveGames() {
        try {
            File file = new File(String.format("results/%s.csv", parserType.arhiveName));
            List<String> lines = new ArrayList<>();
            if (file.exists()) {
                lines = Files.readAllLines(file.toPath());
            }
            List<Game> games = new ArrayList<>();
            for (String line : lines) {
                String[] fields = line.replace(",", ".").split(";");
                Game game = new Game(fields[0], fields[1], fields[2], LocalDateTime.parse(fields[3] + ";" + fields[4], DATE_FORMATTER), fields[5], fields[6],
                        Integer.parseInt(fields[7]), Integer.parseInt(fields[8]), null);
                Map<String, Coefficient> coefficients = game.getCoefficients();
                coefficients.put(bookmakers[0], getCoefficient(bookmakers[0], Arrays.copyOfRange(fields, 9, 16)));
                coefficients.put(bookmakers[1], getCoefficient(bookmakers[1], Arrays.copyOfRange(fields, 16, 23)));
                coefficients.put(bookmakers[2], getCoefficient(bookmakers[2], Arrays.copyOfRange(fields, 23, 30)));
                games.add(game);
                leagueSeasons.add(String.format("%s_%s_%s", fields[0], fields[1], fields[2]));
            }
            this.archiveGames = games;
        } catch (IOException e) {
            throw new ArchiveException(e.getMessage(), e);
        }
    }

    private static Coefficient getCoefficient(String bookmaker, String[] fields) {
        Coefficient coefficient = new Coefficient(bookmaker, fields[0], fields[1], fields[2], fields[3], fields[4]);
        coefficient.setDch1X(fields[5]);
        coefficient.setDchX2(fields[6]);
        return coefficient;
    }
}
