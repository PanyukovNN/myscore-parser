package com.zylex.myscoreparser.controller;

import com.zylex.myscoreparser.exceptions.SaverException;
import com.zylex.myscoreparser.model.Coefficient;
import com.zylex.myscoreparser.model.Record;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class Saver {

    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd;HH:mm");

    private final DateTimeFormatter FILE_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm");

    private final String[] bookmakers = {"1XBET", "Winline", "Leon"};

    public void processSaving(List<Record> records) {
        try {
            File file = createBlockFile();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
            writeToFile(records, writer);
            writer.close();
            ConsoleLogger.blockSummarizing();
        } catch (IOException e) {
            throw new SaverException(e.getMessage(), e);
        }
    }

    private File createBlockFile() throws IOException {
        String dirName = FILE_DATE_FORMATTER.format(LocalDateTime.now());
        new File("results/" + dirName).mkdir();
        File file = new File("results/" + dirName + "/results" + ConsoleLogger.blockNumber++ + ".csv");
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    private void writeToFile(List<Record> records, BufferedWriter writer) throws IOException {
        final String RECORD_BODY_FORMAT = "%s;%s;%s;%s;%s;%s;%d;%d";
        final String COEFFICIENT_FORMAT = ";%s;%s;%s;%s;%s;%s;%s";
        for (Record record : records) {
            if (!doesCoefficientExist(record)) {
                continue;
            }
            StringBuilder line = new StringBuilder(String.format(RECORD_BODY_FORMAT,
                    record.getCountry(),
                    record.getLeagueName(),
                    record.getSeason(),
                    DATE_FORMATTER.format(record.getGameDate()),
                    record.getFirstCommand(),
                    record.getSecondCommand(),
                    record.getFirstBalls(),
                    record.getSecondBalls()));
            Map<String, Coefficient> coefficients = record.getCoefficients();
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
                    line.append(";-;-;-;-;-;-;-");
                }
            }
            line.append("\n");
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

    private boolean doesCoefficientExist(Record record) {
        return !record.getCoefficients().isEmpty();
    }
}
