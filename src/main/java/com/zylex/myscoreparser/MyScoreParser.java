package com.zylex.myscoreparser;

import com.zylex.myscoreparser.controller.ConsoleLogger;
import com.zylex.myscoreparser.repository.Repository;
import com.zylex.myscoreparser.service.DriverManager;
import com.zylex.myscoreparser.service.ParseProcessor;

public class MyScoreParser {

    public static void main(String[] args) {
        try {
            int threads = Integer.parseInt(args[0]);
            new ParseProcessor().process(
                    new DriverManager(threads),
                    new Repository());
        } finally {
            ConsoleLogger.totalSummarizing();
        }
    }
}