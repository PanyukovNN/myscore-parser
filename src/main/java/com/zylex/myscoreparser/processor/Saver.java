package com.zylex.myscoreparser.processor;

import com.zylex.myscoreparser.model.Coeffitient;
import com.zylex.myscoreparser.model.Record;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class Saver {

    private final String RECORD_BODY_FORMAT = "%s;%s;%s;%s;%s;%d;%d";

    private final String COEFFITIENT_FORMAT = ";%s;%s;%s";

    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd;HH:mm");

    private final String[] bookmakers = {"1XBET", "Winline", "Leon"};

    public void processSaving(List<Record> records, String leagueHref) throws IOException {
        String fileName = processFileName(records.get(0).getSeason(), leagueHref);
        File file = new File("results/" + fileName + ".csv");
        file.createNewFile();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
        for (Record record : records) {
            String line = String.format(RECORD_BODY_FORMAT,
                    record.getCountry(),
                    record.getLeagueName(),
                    DATE_FORMATTER.format(record.getGameDate()),
                    record.getFirstCommand(),
                    record.getSecondCommand(),
                    record.getFirstBalls(),
                    record.getSecondBalls());
            Map<String, Coeffitient> coeffitients = record.getCoeffitients();
            for (String bookmaker : bookmakers) {
                if (coeffitients.containsKey(bookmaker)) {
                    Coeffitient coef = coeffitients.get(bookmaker);
                    line += String.format(COEFFITIENT_FORMAT,
                            coef.getFirstWin().replace(".", ","),
                            coef.getTie().replace(".", ","),
                            coef.getSecondWin().replace(".", ","));
                } else {
                    line += ";-";
                }
            }
            line += "\n";
            writer.write(line);
        }
        writer.flush();
        writer.close();
    }

    private String processFileName(String season, String leagueHref) {
        String fileName = leagueHref;
        if (season.equals("2019") || season.equals("2019/2020")) {
            fileName += season.replace("/", "-");
        }
        return fileName.replace("/", "_");
    }
}
