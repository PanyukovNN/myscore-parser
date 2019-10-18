package com.zylex.myscoreparser.exceptions;

public class ParseProcessorException extends MyScoreException {

    public ParseProcessorException() {
    }

    public ParseProcessorException(String message) {
        super(message);
    }

    public ParseProcessorException(String message, Throwable cause) {
        super(message, cause);
    }
}
