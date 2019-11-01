package com.zylex.myscoreparser.service.parser.gamestrategy;

public enum ParserType {
    COEFFICIENTS("coefficients"),
    STATISTICS("statistics");

    public final String arhiveName;

    private ParserType(String arhiveName) {
        this.arhiveName = arhiveName;
    }
}
