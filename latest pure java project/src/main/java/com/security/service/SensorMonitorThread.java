package com.security.service;

import com.security.detector.ThreatEvaluator;
import com.security.model.SecurityEvent;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;

/**
 * Background worker thread for real-time sensor event polling and monitoring.
 * Runs as a daemon thread and uses synchronized queue methods to coordinate
 * incoming sensor telemetry safely across threads.
 */
public class SensorMonitorThread implements Runnable {
    private final LogSimulator simulator;
    private final ThreatEvaluator detector;
    private final Consumer<SecurityEvent> eventConsumer;
    private final Queue<SecurityEvent> eventQueue = new LinkedList<>();
    private volatile boolean running = true;

    public SensorMonitorThread(LogSimulator simulator, ThreatEvaluator detector, Consumer<SecurityEvent> eventConsumer) {
        this.simulator = simulator;
        this.detector = detector;
        this.eventConsumer = eventConsumer;
    }

    // Synchronized method to enqueue events safely across threads
    public synchronized void enqueueEvent(SecurityEvent event) {
        eventQueue.offer(event);
        notifyAll(); // Awake waiting worker
    }

    // Synchronized method to poll events safely
    private synchronized SecurityEvent dequeueEvent() throws InterruptedException {
        while (eventQueue.isEmpty() && running) {
            wait(5000); // Wait up to 5 seconds for new sensor pulse
            if (!running) return null;
            // Generate periodic heartbeat if queue empty
            if (eventQueue.isEmpty()) {
                SecurityEvent simulated = simulator.next();
                detector.analyze(simulated);
                return simulated;
            }
        }
        return eventQueue.poll();
    }

    @Override
    public void run() {
        while (running) {
            try {
                SecurityEvent event = dequeueEvent();
                if (event != null && eventConsumer != null) {
                    eventConsumer.accept(event);
                }
                Thread.sleep(6000); // 6-second heartbeat interval between autonomous sensor checks
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void stop() {
        this.running = false;
        synchronized (this) {
            notifyAll();
        }
    }
}
