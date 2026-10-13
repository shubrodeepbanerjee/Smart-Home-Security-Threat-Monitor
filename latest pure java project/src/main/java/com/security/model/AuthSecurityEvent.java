package com.security.model;

import java.time.LocalDateTime;

/**
 * Represents gateway login and authentication incidents (Brute force, unknown user).
 * Demonstrates:
 * - OOP: Inheritance (extends SecurityEvent)
 * - OOP: Polymorphism (overrides getFormattedSummary())
 */
public class AuthSecurityEvent extends SecurityEvent {
    private final int attemptCount;
    private final boolean isKnownSubnet;

    public AuthSecurityEvent(LocalDateTime timestamp, String username, String ipAddress,
                             String eventType, String description,
                             int attemptCount, boolean isKnownSubnet) {
        super(timestamp, username, ipAddress, eventType, description);
        this.attemptCount = attemptCount;
        this.isKnownSubnet = isKnownSubnet;
    }

    // Polymorphic method override
    @Override
    public String getFormattedSummary() {
        return String.format("[AUTH ATTEMPT - %s] User '%s' from IP %s (Attempt #%d, Trusted Subnet: %b) | Risk: %d/100",
                getThreatLevel(), getUsername(), getIpAddress(), attemptCount, isKnownSubnet, getRiskScore());
    }

    public int getAttemptCount() { return attemptCount; }
    public boolean isKnownSubnet() { return isKnownSubnet; }
}
