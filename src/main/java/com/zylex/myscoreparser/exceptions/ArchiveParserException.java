package com.zylex.myscoreparser.exceptions;

public class ArchiveParserException extends MyScoreParserException {

    public ArchiveParserException() {
    }

    public ArchiveParserException(String message) {
        super(message);
    }

    public ArchiveParserException(String message, Throwable cause) {
        super(message, cause);
    }
}
