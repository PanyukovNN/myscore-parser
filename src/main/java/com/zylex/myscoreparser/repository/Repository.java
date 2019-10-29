package com.zylex.myscoreparser.repository;

import com.zylex.myscoreparser.exceptions.ArchiveException;
import com.zylex.myscoreparser.exceptions.RepositoryException;
import com.zylex.myscoreparser.model.Coefficient;
import com.zylex.myscoreparser.model.Game;
import com.zylex.myscoreparser.service.DriverManager;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Repository {

    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd;HH:mm");

    private String[] bookmakers = {"1XBET", "Winline", "Leon"};

    private List<Game> archiveGames;

    private Set<String> leagueSeasons = new HashSet<>();

    public Repository() {
        archiveGames = readArchiveGames();
    }

    public List<Game> getArchiveGames() {
        return archiveGames;
    }

    public Set<String> getLeagueSeasons() {
        return leagueSeasons;
    }

    private List<Game> readArchiveGames() {
        try {
            File file = new File("results/total_statistics.csv");
            List<String> lines = Files.readAllLines(file.toPath());
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
            return games;
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

    public void sortArchive() {
        archiveGames = archiveGames.stream()
                .sorted(Comparator.comparing(Game::getCountry)
                        .thenComparing(Game::getLeagueName)
                        .thenComparing(Game::getSeason)
                        .thenComparing(Game::getGameDate)
                ).collect(Collectors.toList());
    }


    public List<List<String>> readDiscreteLeaguesFromFile(int threads) {
        try {
            InputStream inputStream = DriverManager.class.getResourceAsStream("/leagues.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            List<String> leagueLinks = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.contains("//") && !line.isEmpty()) {
                    leagueLinks.add(line);
                }
            }
            return getDiscreteLeagueList(threads, leagueLinks);
        } catch (IOException e) {
            throw new RepositoryException(e.getMessage(), e);
        }
    }

    private List<List<String>> getDiscreteLeagueList(int threads, List<String> leagueLinks) {
        List<List<String>> discreteList = new ArrayList<>();
        while (true) {
            if (leagueLinks.size() <= threads) {
                discreteList.add(leagueLinks);
                break;
            }
            discreteList.add(leagueLinks.subList(0, threads));
            leagueLinks = leagueLinks.subList(threads, leagueLinks.size());
        }
        return discreteList;
    }
}
