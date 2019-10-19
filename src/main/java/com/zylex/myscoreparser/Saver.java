package com.zylex.myscoreparser;

import com.zylex.myscoreparser.exceptions.SaverException;
import com.zylex.myscoreparser.model.Coefficient;
import com.zylex.myscoreparser.model.Record;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class Saver {

    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd;HH:mm");

    private final String[] bookmakers = {"1XBET", "Winline", "Leon"};

    public void processSaving(String fileNumbers, List<Record> records) {
        try {
            File file = new File("results/results" + fileNumbers + ".csv");
            if (file.createNewFile()) {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
                writeToFile(records, writer);
                writer.close();
            }
        } catch (IOException e) {
            throw new SaverException(e.getMessage(), e);
        }
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
                    formatDouble(coef.getMaxDch()),
                    formatDouble(coef.getMinDch())));
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
            return String.format("%.2f", Double.parseDouble(value))
                    .replace(',', '.');
        } catch (NumberFormatException e) {
            return value;
        }
    }

    private boolean doesCoefficientExist(Record record) {
        return !record.getCoefficients().isEmpty();
    }
}
