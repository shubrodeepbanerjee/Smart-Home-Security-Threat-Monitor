package com.security.model;

import java.time.LocalDateTime;

/**
 * Base security event model.
 * Demonstrates:
 * - OOP: Base class for inheritance hierarchy
 * - Generics: Implements Comparable<SecurityEvent>
 * - Polymorphism: Overridable getFormattedSummary()
 */
public class SecurityEvent implements Comparable<SecurityEvent> {
    private Integer id;
    private final LocalDateTime timestamp;
    private final String username;
    private final String ipAddress;
    private final String eventType;
    private final String description;
    private ThreatLevel threatLevel;
    private int riskScore;

    public SecurityEvent(LocalDateTime timestamp, String username, String ipAddress, String eventType, String description) {
        this.timestamp = timestamp;
        this.username = username;
        this.ipAddress = ipAddress;
        this.eventType = eventType;
        this.description = description;
        this.threatLevel = ThreatLevel.LOW;
        this.riskScore = 0;
    }

    // Polymorphic method overridden by subclasses
    public String getFormattedSummary() {
        return String.format("[%s] %s by %s from %s (Risk: %d/100)",
                threatLevel, eventType, username, ipAddress, riskScore);
    }

    // Generic Comparable implementation for sorting by timestamp/risk
    @Override
    public int compareTo(SecurityEvent other) {
        return Integer.compare(other.riskScore, this.riskScore); // descending risk order
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getUsername() { return username; }
    public String getIpAddress() { return ipAddress; }
    public String getEventType() { return eventType; }
    public String getDescription() { return description; }
    public ThreatLevel getThreatLevel() { return threatLevel; }
    public void setThreatLevel(ThreatLevel v) { this.threatLevel = v; }
    public int getRiskScore() { return riskScore; }
    public void setRiskScore(int v) { this.riskScore = v; }
}
