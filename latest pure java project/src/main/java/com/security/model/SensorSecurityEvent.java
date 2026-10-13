package com.security.model;

import java.time.LocalDateTime;

/**
 * Represents a physical hardware/sensor security incident (Door, Window, Motion, Smoke).
 * Demonstrates:
 * - OOP: Inheritance (extends SecurityEvent)
 * - OOP: Polymorphism (overrides getFormattedSummary())
 */
public class SensorSecurityEvent extends SecurityEvent {
    private final String sensorLocation;
    private final String hardwareProtocol; // Zigbee, Z-Wave, MQTT

    public SensorSecurityEvent(LocalDateTime timestamp, String username, String ipAddress,
                               String eventType, String description,
                               String sensorLocation, String hardwareProtocol) {
        super(timestamp, username, ipAddress, eventType, description);
        this.sensorLocation = sensorLocation;
        this.hardwareProtocol = hardwareProtocol;
    }

    // Polymorphic method override
    @Override
    public String getFormattedSummary() {
        return String.format("[SENSOR EVENT - %s] %s at %s via %s | Risk: %d/100",
                getThreatLevel(), getDescription(), sensorLocation, hardwareProtocol, getRiskScore());
    }

    public String getSensorLocation() { return sensorLocation; }
    public String getHardwareProtocol() { return hardwareProtocol; }
}
