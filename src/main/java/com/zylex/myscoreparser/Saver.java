package com.zylex.myscoreparser;

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

    public void processSaving(List<Record> records, String leagueHref) throws IOException {
        String fileName = processFileName(records.get(0).getSeason(), leagueHref);
        File file = new File("results/" + fileName + ".csv");
        if (file.createNewFile()) {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
            writeToFile(records, writer);
            writer.close();
        }
    }

    private void writeToFile(List<Record> records, BufferedWriter writer) throws IOException {
        final String RECORD_BODY_FORMAT = "%s;%s;%s;%s;%s;%d;%d";
        final String COEFFICIENT_FORMAT = ";%s;%s;%s";
        for (Record record : records) {
            StringBuilder line = new StringBuilder(String.format(RECORD_BODY_FORMAT,
                    record.getCountry(),
                    record.getLeagueName(),
                    DATE_FORMATTER.format(record.getGameDate()),
                    record.getFirstCommand(),
                    record.getSecondCommand(),
                    record.getFirstBalls(),
                    record.getSecondBalls()));
            Map<String, Coefficient> coeffitients = record.getCoefficients();
            for (String bookmaker : bookmakers) {
                if (coeffitients.containsKey(bookmaker)) {
                    Coefficient coef = coeffitients.get(bookmaker);
                    line.append(String.format(COEFFICIENT_FORMAT,
                            coef.getFirstWin().replace(".", ","),
                            coef.getTie().replace(".", ","),
                            coef.getSecondWin().replace(".", ",")));
                } else {
                    line.append(";-");
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
            fileName += "-" + season.replace("/", "-");
        }
        return fileName;
    }
}
