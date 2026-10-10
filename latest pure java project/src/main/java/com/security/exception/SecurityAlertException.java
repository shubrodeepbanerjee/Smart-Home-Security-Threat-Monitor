package com.security.exception;

/**
 * Custom exception thrown when a critical security threshold is breached.
 */
public class SecurityAlertException extends Exception {
    private final int riskScore;

    public SecurityAlertException(String message, int riskScore) {
        super(message);
        this.riskScore = riskScore;
    }

    public int getRiskScore() {
        return riskScore;
    }
}
