package com.zylex.myscoreparser.exceptions;

public class LeagueParserException extends MyScoreParserException {

    public LeagueParserException() {
    }

    public LeagueParserException(String message) {
        super(message);
    }

    public LeagueParserException(String message, Throwable cause) {
        super(message, cause);
    }
}
