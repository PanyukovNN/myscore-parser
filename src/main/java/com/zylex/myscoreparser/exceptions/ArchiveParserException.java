package com.zylex.myscoreparser.exceptions;

public class ArchiveParserException extends MyScoreException {

    public ArchiveParserException() {
    }

    public ArchiveParserException(String message) {
        super(message);
    }

    public ArchiveParserException(String message, Throwable cause) {
        super(message, cause);
    }
}
