package com.zylex.myscoreparser.controller;

import com.zylex.myscoreparser.exceptions.SaverParserException;
import com.zylex.myscoreparser.model.Coefficient;
import com.zylex.myscoreparser.model.Game;
import com.zylex.myscoreparser.repository.Repository;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class Saver {

    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd;HH:mm");

    private final String[] bookmakers = {"1XBET", "Winline", "Leon"};

    public synchronized void processArchiveSaving(Repository repository) {
        try {
            repository.sortArchive();
            File file = createArchiveFile();
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), StandardCharsets.UTF_8))) {
                writeToFile(repository.getArchiveGames(), writer);
            }
        } catch (IOException e) {
            throw new SaverParserException(e.getMessage(), e);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private File createArchiveFile() throws IOException {
        File file = new File("results/total_statistics.csv");
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    private void writeToFile(List<Game> games, BufferedWriter writer) throws IOException {
        final String GAME_BODY_FORMAT = "%s;%s;%s;%s;%s;%s;%d;%d";
        final String COEFFICIENT_FORMAT = ";%s;%s;%s;%s;%s;%s;%s";
        for (Game game : games) {
            if (!doesCoefficientExist(game)) {
                continue;
            }
            StringBuilder line = new StringBuilder(String.format(GAME_BODY_FORMAT,
                    game.getCountry(),
                    game.getLeagueName(),
                    game.getSeason(),
                    DATE_FORMATTER.format(game.getGameDate()),
                    game.getFirstCommand(),
                    game.getSecondCommand(),
                    game.getFirstBalls(),
                    game.getSecondBalls()));
            Map<String, Coefficient> coefficients = game.getCoefficients();
            for (String bookmaker : bookmakers) {
                if (coefficients.containsKey(bookmaker)) {
                    Coefficient coef = coefficients.get(bookmaker);
                    line.append(String.format(COEFFICIENT_FORMAT,
                    formatDouble(coef.getFirstWin()),
                    formatDouble(coef.getTie()),
                    formatDouble(coef.getSecondWin()),
                    formatDouble(coef.getMax1x2()),
                    formatDouble(coef.getMin1x2()),
                    formatDouble(coef.getDch1X()),
                    formatDouble(coef.getDchX2())));
                } else {
                    line.append(";-;-;-;-;-;-;-;-");
                }
            }
            line.append(String.format(";%s\n", game.getCoefHref()));
            writer.write(line.toString());
        }
    }

    private String formatDouble(String value) {
        try {
            return new DecimalFormat("#.00").format(Double.parseDouble(value))
                    .replace('.', ',');
        } catch (NumberFormatException e) {
            return value;
        }
    }

    private boolean doesCoefficientExist(Game game) {
        return !game.getCoefficients().isEmpty();
    }
}
