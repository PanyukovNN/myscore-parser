package com.zylex.myscoreparser.service.parser.gamestrategy;

public enum ParserType {
    COEFFICIENTS("coefficients"),
    STATISTICS("statistics");

    public final String archiveName;

    ParserType(String archiveName) {
        this.archiveName = archiveName;
    }
}
