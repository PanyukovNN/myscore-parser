package com.zylex.myscoreparser;

import com.zylex.myscoreparser.controller.ArchiveSaver;
import com.zylex.myscoreparser.controller.GameRepository;
import com.zylex.myscoreparser.controller.LeagueRepository;
import com.zylex.myscoreparser.service.DriverManager;
import com.zylex.myscoreparser.service.parser.ParseProcessor;
import com.zylex.myscoreparser.service.parser.parsing_strategy.ParserType;

public class MyScoreParser {

    public static void main(String[] args) {
        int threads = 4;
        ParserType parserType = ParserType.COEFFICIENTS;
        new ArchiveSaver(
            new ParseProcessor(
                new DriverManager(threads),
                new GameRepository(parserType),
                new LeagueRepository())
        ).processSaving();
    }
}