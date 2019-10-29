package com.zylex.myscoreparser.exceptions;

public class SaverParserException extends MyScoreParserException {

    public SaverParserException() {
    }

    public SaverParserException(String message) {
        super(message);
    }

    public SaverParserException(String message, Throwable cause) {
        super(message, cause);
    }
}
