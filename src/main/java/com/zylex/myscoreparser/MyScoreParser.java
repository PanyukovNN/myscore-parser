package com.zylex.myscoreparser;

import com.zylex.myscoreparser.controller.Saver;
import com.zylex.myscoreparser.repository.GameRepository;
import com.zylex.myscoreparser.repository.LeagueRepository;
import com.zylex.myscoreparser.service.DriverManager;
import com.zylex.myscoreparser.service.parser.ParseProcessor;
import com.zylex.myscoreparser.service.parser.gamestrategy.ParserType;

public class MyScoreParser {

    public static void main(String[] args) {
        int threads = 4;
        ParserType parserType = ParserType.COEFFICIENTS;
        new Saver(
            new ParseProcessor(
                new DriverManager(threads),
                new GameRepository(parserType),
                new LeagueRepository())
        ).processSaving();
    }
}