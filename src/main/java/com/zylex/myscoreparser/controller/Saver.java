package com.zylex.myscoreparser.controller;

import com.zylex.myscoreparser.exceptions.SaverParserException;
import com.zylex.myscoreparser.model.Coefficient;
import com.zylex.myscoreparser.model.Game;
import com.zylex.myscoreparser.model.StatisticsValue;
import com.zylex.myscoreparser.service.parser.ParseProcessor;
import com.zylex.myscoreparser.service.parser.gamestrategy.ParserType;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Saver {

    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd;HH:mm");

    private final String[] bookmakers = {"1XBET", "Winline", "Leon"};

    private final String[] itemNames = {"Владение мячом", "Удары", "Удары в створ", "Удары мимо", "Блок-но ударов",
            "Штрафные", "Угловые", "Офсайды", "Сэйвы", "Фолы", "Желтые карточки", "Красные карточки",
            "Всего передач", "Завершено передач", "Oтборы", "Атаки", "Опасные атаки"};

    private ParseProcessor parseProcessor;

    private ParserType parserType;

    public Saver(ParseProcessor parseProcessor) {
        this.parseProcessor = parseProcessor;
        this.parserType = parseProcessor.getParserType();
    }

    public synchronized void processSaving() {
        try {
            List<Game> archiveGames = sortGames(parseProcessor.process());
            File file = createArchiveFile();
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), StandardCharsets.UTF_8))) {
                writeToFile(archiveGames, writer);
            }
        } catch (IOException e) {
            throw new SaverParserException(e.getMessage(), e);
        }
    }

    private List<Game> sortGames(List<Game> games) {
        return games.stream()
                .sorted(Comparator.comparing(Game::getCountry)
                        .thenComparing(Game::getLeagueName)
                        .thenComparing(Game::getSeason)
                        .thenComparing(Game::getGameDate)
                ).collect(Collectors.toList());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private File createArchiveFile() throws IOException {
        File file = new File(String.format("results/%s.csv", parserType.archiveName));
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    private void writeToFile(List<Game> games, BufferedWriter writer) throws IOException {
        final String GAME_BODY_FORMAT = "%s;%s;%s;%s;%s;%s;%d;%d;%s";
        for (Game game : games) {
            if (game.getCoefficients().isEmpty() && game.getStatisticsItems().isEmpty()) {
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
                    game.getSecondBalls(),
                    game.getLink()));
            if (parserType == ParserType.COEFFICIENTS) {
                addCoefficientsToLine(game, line);
            } else if (parserType == ParserType.STATISTICS) {
                addStatisticsToLine(game, line);
            }
            line.append("\n");//line.append(String.format(";%s\n", game.getCoefHref()));
            writer.write(line.toString());
        }
    }

    private void addCoefficientsToLine(Game game, StringBuilder line) {
        final String COEFFICIENT_FORMAT = ";%s;%s;%s;%s;%s;%s;%s";
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
    }

    private String formatDouble(String value) {
        try {
            return new DecimalFormat("#.00").format(Double.parseDouble(value))
                    .replace('.', ',');
        } catch (NumberFormatException e) {
            return value;
        }
    }

    private void addStatisticsToLine(Game game, StringBuilder line) {
        final String STATISTICS_FORMAT = ";%s:%s;%s";
        Map<String, StatisticsValue> statisticsItems = game.getStatisticsItems();
        for (String item : itemNames) {
            if (statisticsItems.containsKey(item)) {
                StatisticsValue values = statisticsItems.get(item);
                line.append(String.format(STATISTICS_FORMAT,
                        item,
                        values.getHomeValue(),
                        values.getAwayValue()));
            } else {
                line.append(String.format(";%s:-;-", item));
            }
        }
    }
}
