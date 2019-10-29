package com.zylex.myscoreparser.exceptions;

public class RepositoryParserException extends MyScoreParserException {

    public RepositoryParserException() {
    }

    public RepositoryParserException(String message) {
        super(message);
    }

    public RepositoryParserException(String message, Throwable cause) {
        super(message, cause);
    }
}
