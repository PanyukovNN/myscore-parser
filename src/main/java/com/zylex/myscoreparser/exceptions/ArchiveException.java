package com.zylex.myscoreparser.exceptions;

public class ArchiveException extends MyScoreParserException {

    public ArchiveException() {
    }

    public ArchiveException(String message) {
        super(message);
    }

    public ArchiveException(String message, Throwable cause) {
        super(message, cause);
    }
}
