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

    public void processSaving(List<Record> records, String leagueHref) {
        try {
            String fileName = processFileName(records.get(0).getSeason(), leagueHref);
            File file = new File("results/" + fileName + ".csv");
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
                            coef.getFirstWin().replace(".", ","),
                            coef.getTie().replace(".", ","),
                            coef.getSecondWin().replace(".", ","),
                            coef.getMax1x2().replace(".", ","),
                            coef.getMin1x2().replace(".", ","),
                            coef.getMaxDch().replace(".", ","),
                            coef.getMinDch().replace(".", ",")));
                } else {
                    line.append(";-;-;-;-;-;-;-");
                }
            }
            line.append("\n");
            writer.write(line.toString());
        }
    }

    private String processFileName(String season, String leagueHref) {
        String fileName = leagueHref
                .substring(0, leagueHref.length() - 1)
                .replace("/", "-");
        if (season.substring(0, 4).equals("2019")) {
            if (season.equals("2019")) {
                fileName += "-" + season.substring(0, 4);
            } else {
                fileName += "-" + season.substring(0, 4) + "-" + season.substring(4);
            }
        }
        return fileName;
    }

    private boolean doesCoefficientExist(Record record) {
        return !record.getCoefficients().isEmpty();
    }
}
