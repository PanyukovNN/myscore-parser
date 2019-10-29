package com.zylex.myscoreparser.exceptions;

public class MyScoreParserException extends RuntimeException {

    public MyScoreParserException() {
    }

    public MyScoreParserException(String message) {
        super(message);
    }

    public MyScoreParserException(String message, Throwable cause) {
        super(message, cause);
    }
}
