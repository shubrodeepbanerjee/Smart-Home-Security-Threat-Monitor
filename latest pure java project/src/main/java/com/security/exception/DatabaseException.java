package com.security.exception;

/**
 * Custom exception representing database connectivity and execution failures.
 */
public class DatabaseException extends Exception {
    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
