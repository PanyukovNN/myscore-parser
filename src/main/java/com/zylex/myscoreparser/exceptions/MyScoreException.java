package com.zylex.myscoreparser.exceptions;

public class MyScoreException extends RuntimeException {

    public MyScoreException() {
    }

    public MyScoreException(String message) {
        super(message);
    }

    public MyScoreException(String message, Throwable cause) {
        super(message, cause);
    }
}
