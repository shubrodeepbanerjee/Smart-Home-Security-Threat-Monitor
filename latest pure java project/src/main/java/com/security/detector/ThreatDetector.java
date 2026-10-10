package com.security.detector;

import com.security.model.SecurityEvent;
import com.security.model.ThreatLevel;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of ThreatEvaluator using explainable rule-based anomaly scoring.
 * Demonstrates:
 * - OOP: Interface implementation (ThreatEvaluator)
 * - Collections & Generics: Map<String, Integer> failure tracking
 */
public class ThreatDetector implements ThreatEvaluator {
    // Generics & Collections: Map to track failure occurrences per IP
    private final Map<String, Integer> failures = new HashMap<>();

    @Override
    public void analyze(SecurityEvent e) {
        int score = 0;
        if (e.getEventType().equals("LOGIN_FAILED")) {
            int n = failures.merge(e.getIpAddress(), 1, Integer::sum);
            score += 20 + Math.min(n * 10, 50);
        } else if (e.getEventType().equals("UNKNOWN_USER")) {
            score += 45;
        } else if (e.getEventType().equals("SUSPICIOUS_ACCESS")) {
            score += 60;
        } else {
            score += 5;
        }

        // Off-hours time window penalty (< 06:00 or >= 23:00)
        int h = e.getTimestamp().getHour();
        if (h < 6 || h >= 23) {
            score += 15;
        }

        // Untrusted/rogue IP subnet penalty (10.99.x.x)
        if (e.getIpAddress().startsWith("10.99.")) {
            score += 15;
        }

        score = Math.min(score, 100);
        e.setRiskScore(score);
        e.setThreatLevel(score >= 76 ? ThreatLevel.CRITICAL :
                         score >= 51 ? ThreatLevel.HIGH :
                         score >= 26 ? ThreatLevel.MEDIUM : ThreatLevel.LOW);
    }

    @Override
    public String explain(SecurityEvent e) {
        return switch (e.getThreatLevel()) {
            case CRITICAL -> "Critical anomaly: repeated/abnormal login behaviour requires immediate review.";
            case HIGH -> "High-risk pattern detected, such as repeated failed logins or suspicious access.";
            case MEDIUM -> "Some unusual characteristics were found. Review the event.";
            case LOW -> "Activity appears normal based on current rules.";
        };
    }
}
