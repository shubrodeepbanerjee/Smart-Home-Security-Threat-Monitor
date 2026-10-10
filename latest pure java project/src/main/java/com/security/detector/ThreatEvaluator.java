package com.security.detector;

import com.security.model.SecurityEvent;

/**
 * Interface defining the contract for threat evaluation and anomaly scoring.
 */
public interface ThreatEvaluator {
    void analyze(SecurityEvent event);
    String explain(SecurityEvent event);
}
