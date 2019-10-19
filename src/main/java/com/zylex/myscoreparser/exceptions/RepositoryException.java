package com.zylex.myscoreparser.exceptions;

public class RepositoryException extends MyScoreException {

    public RepositoryException() {
    }

    public RepositoryException(String message) {
        super(message);
    }

    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
