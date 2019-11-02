package com.zylex.myscoreparser.service.parser.parsing_strategy;

public enum ParserType {
    COEFFICIENTS("coefficients"),
    STATISTICS("statistics");

    public final String archiveName;

    ParserType(String archiveName) {
        this.archiveName = archiveName;
    }
}
