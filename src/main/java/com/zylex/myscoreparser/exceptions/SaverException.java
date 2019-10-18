package com.zylex.myscoreparser.exceptions;

public class SaverException extends MyScoreException {

    public SaverException() {
    }

    public SaverException(String message) {
        super(message);
    }

    public SaverException(String message, Throwable cause) {
        super(message, cause);
    }
}
