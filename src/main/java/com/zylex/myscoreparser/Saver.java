package com.zylex.myscoreparser;

import com.zylex.myscoreparser.model.Coeffitient;
import com.zylex.myscoreparser.model.Record;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Saver {

    private final String RECORD_BODY_FORMAT = "%s;%s;%s;%s;%s;%d;%d";

    private final String COEFFITIENT_FORMAT = ";%s;%s;%s";

    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd;HH:mm");

    private final String[] bookmakers = {"1XBET", "Winline", "Leon"};

    public void processSaving(List<Record> records, String league) throws IOException, ParseException {
        File file = new File(league.replace("/", "_") + ".txt");
        file.createNewFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
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
                            coef.getFirstWin(),
                            coef.getTie(),
                            coef.getSecondWin());
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
}
