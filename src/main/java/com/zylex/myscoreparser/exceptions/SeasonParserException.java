package com.zylex.myscoreparser.exceptions;

public class SeasonParserException extends MyScoreParserException {

    public SeasonParserException() {
    }

    public SeasonParserException(String message) {
        super(message);
    }

    public SeasonParserException(String message, Throwable cause) {
        super(message, cause);
    }
}
