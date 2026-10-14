# Smart Home Security Threat Monitor: Viva Voce & Oral Defense Guide

This document is prepared to assist **Member 4** and team members in successfully demonstrating and answering technical questions during the project viva voce, oral examination, or faculty evaluation.

---

## Table of Contents
1. [General Project & Architectural Questions](#1-general-project--architectural-questions)
2. [Object-Oriented Programming (OOP) Concepts](#2-object-oriented-programming-oop-concepts)
3. [Inheritance, Domain Models & Polymorphism](#3-inheritance-domain-models--polymorphism)
4. [Multithreading & Concurrency Mechanics](#4-multithreading--concurrency-mechanics)
5. [Threat Detection & Anomaly Heuristics](#5-threat-detection--anomaly-heuristics)
6. [Database Persistence & DAO Pattern](#6-database-persistence--dao-pattern)
7. [Exception Handling Architecture](#7-exception-handling-architecture)
8. [Collections, Generics & Java Features](#8-collections-generics--java-features)
9. [Member 4 Specific Contributions & Integration](#9-member-4-specific-contributions--integration)
10. [Rapid-Fire & Tricky Technical Questions](#10-rapid-fire--tricky-technical-questions)

---

## 1. General Project & Architectural Questions

### Q1. What is the Smart Home Security Threat Monitor?
**Answer:**  
The Smart Home Security Threat Monitor is an explainable, 100% offline desktop cybersecurity application developed in pure Java (Java SE 17+ and Java Swing). It ingests real-time and simulated smart home telemetry (door/window sensors, motion detectors, smoke detectors, and gateway authentication logs), evaluates potential intrusions using an explainable heuristic scoring engine (0–100 risk score), classifies threats into four levels (`LOW`, `MEDIUM`, `HIGH`, `CRITICAL`), logs events securely to an SQLite database via a Data Access Object (DAO) pattern, and provides an interactive desktop dashboard.

### Q2. Why did you build this as an offline desktop app rather than a cloud-based web app?
**Answer:**  
1. **Privacy & Data Locality**: Smart home telemetry contains private homeowner lifestyle patterns (wake times, room occupancy, door activity). Streaming this telemetry to third-party cloud servers introduces data breach vulnerabilities.
2. **Zero Cloud Latency & Zero Subscriptions**: Local offline processing guarantees sub-millisecond threat evaluation without internet connectivity or recurring cloud fees.
3. **Resilience**: Even if an attacker cuts the home's external broadband connection, the local monitor remains fully operational.

### Q3. Can you explain the end-to-end data flow pipeline of the system?
**Answer:**  
1. **Ingestion**: Telemetry is generated either by `LogSimulator` or by user interaction with sensor cards on the Swing UI.
2. **Buffering**: Events are passed to `SensorMonitorThread` and stored in a synchronized FIFO queue (`LinkedList<SecurityEvent>`).
3. **Evaluation**: When dequeued, the event is analyzed by `ThreatDetector` (implementing `ThreatEvaluator`), which calculates a risk score (0–100) and assigns a `ThreatLevel`.
4. **Persistence**: `DatabaseManager` (implementing `EventDao`) writes the event to SQLite (`security.db`) using a parameterized `PreparedStatement`.
5. **Presentation**: The UI updates KPI counters, appends rows to the `JTable`, triggers alert popups for `HIGH`/`CRITICAL` events, and displays rule breakdown explanations in the Explainable AI inspector.

---

## 2. Object-Oriented Programming (OOP) Concepts

### Q4. What OOP principles are implemented in this project?
**Answer:**  
All four primary pillars of OOP, along with modern software design principles:
- **Encapsulation**: Private and final fields, public getters, controlled setters, and data hiding.
- **Inheritance**: Subclasses `SensorSecurityEvent` and `AuthSecurityEvent` inherit common fields and methods from base class `SecurityEvent`.
- **Polymorphism**: Dynamic method overriding in `getFormattedSummary()`, interface polymorphism (`ThreatEvaluator`, `EventDao`, `Runnable`), and generic interface implementation (`Comparable<SecurityEvent>`).
- **Abstraction**: Interfaces `ThreatEvaluator` and `EventDao` decouple callers from concrete scoring and storage implementations.

### Q5. Where is Abstraction used in your codebase?
**Answer:**  
Abstraction is achieved primarily through Java interfaces:
1. **`ThreatEvaluator` Interface**: Specifies `analyze(SecurityEvent)` and `explain(SecurityEvent)`. The caller (`SensorMonitorThread` or UI) does not know how penalties are calculated—only that the contract will evaluate and explain the event.
2. **`EventDao` Interface**: Specifies `initDatabase()`, `saveEvent()`, `allEvents()`, and `countByThreatLevel()`. The business logic interacts with `EventDao` without knowing whether SQLite, MySQL, or an in-memory mock is used underneath.

### Q6. How is Encapsulation maintained across your domain models?
**Answer:**  
In `SecurityEvent`:
- Fields like `timestamp`, `username`, `ipAddress`, `eventType`, and `description` are marked `private final`, ensuring immutability once created.
- Fields that change during lifecycle (`threatLevel`, `riskScore`, and database `id`) are `private` with explicit setter validation.
- External classes cannot directly corrupt an event's internal state.

---

## 3. Inheritance, Domain Models & Polymorphism

### Q7. Explain the class hierarchy in the `com.security.model` package.
**Answer:**  
- **Base Class**: `SecurityEvent` contains common event properties: `id`, `timestamp`, `username`, `ipAddress`, `eventType`, `description`, `threatLevel`, and `riskScore`.
- **Subclass 1**: `SensorSecurityEvent extends SecurityEvent` adds hardware attributes: `sensorLocation` (e.g., "Front Entrance") and `hardwareProtocol` (e.g., "Zigbee", "Z-Wave").
- **Subclass 2**: `AuthSecurityEvent extends SecurityEvent` adds gateway login attributes: `attemptCount` (int) and `isKnownSubnet` (boolean).
- Both subclasses call `super(timestamp, username, ipAddress, eventType, description)` in their constructors to initialize base state.

### Q8. What is runtime polymorphism, and how did you verify it in your code?
**Answer:**  
Runtime polymorphism (dynamic method dispatch) allows a method call to resolve at runtime based on the actual object type rather than the reference type.  
In our project, `SecurityEvent` defines `getFormattedSummary()`. Both `SensorSecurityEvent` and `AuthSecurityEvent` override this method.  
When iterating through a `List<SecurityEvent>`:
```java
for (SecurityEvent e : eventsList) {
    System.out.println(e.getFormattedSummary());
}
```
Java dynamically executes `SensorSecurityEvent.getFormattedSummary()` for sensor objects and `AuthSecurityEvent.getFormattedSummary()` for authentication objects, outputting specialized metadata without needing `if-else` or `instanceof` checks.

### Q9. What does `ThreatLevel` represent, and how are levels defined?
**Answer:**  
`ThreatLevel` is a strongly typed Java enum:
```java
public enum ThreatLevel { LOW, MEDIUM, HIGH, CRITICAL }
```
It classifies incidents based on the calculated risk score:
- **`LOW`** (0–25): Normal authorized telemetry.
- **`MEDIUM`** (26–50): Minor anomaly requiring observation.
- **`HIGH`** (51–75): Elevated threat pattern (e.g., repeated login failures).
- **`CRITICAL`** (76–100): High-confidence intrusion or brute-force attack requiring immediate mitigation.

---

## 4. Multithreading & Concurrency Mechanics

### Q10. How does multithreading work in your project?
**Answer:**  
The background monitoring service is handled by `SensorMonitorThread`, which implements `java.lang.Runnable`. It executes in a separate worker thread so that sensor polling does not freeze the Java Swing Event Dispatch Thread (EDT).

### Q11. Why did you use `volatile` for the `running` flag in `SensorMonitorThread`?
**Answer:**  
```java
private volatile boolean running = true;
```
In Java, different threads may cache variables locally in CPU registers. The `volatile` keyword guarantees **visibility**: changes made to `running` by the main thread (when calling `stop()`) are immediately visible to the background worker thread without caching delays.

### Q12. Explain how `wait()` and `notifyAll()` coordinate the event queue.
**Answer:**  
`SensorMonitorThread` uses an internal `Queue<SecurityEvent>` (`LinkedList`):
- When a new event arrives, `enqueueEvent()` enters a `synchronized` block, adds the event via `eventQueue.offer(event)`, and calls `notifyAll()` to awaken the sleeping worker thread.
- In `dequeueEvent()`, if the queue is empty, the thread calls `wait(5000)`, sleeping until either an event is enqueued (`notifyAll()`) or the 5-second timeout expires (which generates an autonomous sensor heartbeat).
- This prevents CPU-intensive busy-waiting (spin locks).

---

## 5. Threat Detection & Anomaly Heuristics

### Q13. How does `ThreatDetector` calculate the risk score?
**Answer:**  
The risk score starts at 0 and adds heuristic penalties:
1. **Event Type Penalty**:
   - `SUSPICIOUS_ACCESS`: +60
   - `UNKNOWN_USER`: +45
   - `LOGIN_FAILED`: +20 base, plus repeated occurrences from the same IP add +10 per streak (up to +50)
   - Other events: +5
2. **Temporal Window Penalty**:
   - If the event timestamp is before 06:00 or at/after 23:00 (night time), add +15.
3. **Subnet Anomaly Penalty**:
   - If the source IP starts with untrusted subnet `10.99.`, add +15.
4. **Capping**: Score is clamped to a maximum of 100 via `Math.min(score, 100)`.

### Q14. What makes your AI/threat detection "explainable"?
**Answer:**  
Unlike opaque black-box machine learning models, `ThreatDetector` provides transparent reasoning through its `explain(SecurityEvent)` method and UI breakdown area. It shows exact mathematical component penalties (+60 for event, +15 for off-hours, +15 for rogue subnet) and returns actionable mitigation steps (e.g., "Quarantine IP on firewall & verify credentials").

---

## 6. Database Persistence & DAO Pattern

### Q15. What is the Data Access Object (DAO) pattern, and why did you use it?
**Answer:**  
The DAO pattern separates low-level data persistence operations from high-level business logic.  
- `EventDao` interface defines abstract persistence operations (`saveEvent`, `allEvents`, etc.).
- `DatabaseManager` implements this interface using SQLite JDBC.  
If we ever decide to migrate from SQLite to PostgreSQL or MySQL, we only need to write a new class implementing `EventDao` without modifying any UI or detection logic.

### Q16. How does `DatabaseManager` prevent SQL injection?
**Answer:**  
It uses parameterized `java.sql.PreparedStatement` with placeholder `?` marks instead of raw string concatenation:
```java
String sql = "INSERT INTO security_events(timestamp, username, ip_address, event_type, description, threat_level, risk_score) VALUES(?, ?, ?, ?, ?, ?, ?)";
PreparedStatement pstmt = conn.prepareStatement(sql);
pstmt.setString(1, e.getTimestamp().toString());
...
pstmt.executeUpdate();
```
User inputs are treated strictly as literal values rather than executable SQL code.

### Q17. What happens if the SQLite JDBC driver is not on the classpath?
**Answer:**  
`DatabaseManager` implements a defensive fallback:
- Its static initializer catches `ClassNotFoundException` when loading `org.sqlite.JDBC` and sets `driverLoaded = false`.
- All methods check `if (!driverLoaded) return;`.
- The application logs an informative notice and continues running safely using in-memory collections without crashing.

---

## 7. Exception Handling Architecture

### Q18. What custom exceptions did you create and why?
**Answer:**  
1. **`DatabaseException`** (`com.security.exception.DatabaseException`):
   - A checked exception extending `java.lang.Exception`.
   - Wraps low-level `SQLException` or `ClassNotFoundException`, decoupling callers from database-specific drivers.
2. **`SecurityAlertException`** (`com.security.exception.SecurityAlertException`):
   - A domain-specific checked exception thrown when a critical risk threshold is breached.
   - Encapsulates contextual payload: the integer `riskScore`, accessible via `getRiskScore()`.

### Q19. What is exception chaining, and where is it used?
**Answer:**  
Exception chaining occurs when an exception is caught and re-thrown wrapped inside a higher-level custom exception while preserving the original cause:
```java
public DatabaseException(String message, Throwable cause) {
    super(message, cause);
}
```
In `DatabaseManager`, when `SQLException` occurs, it throws `new DatabaseException("Failed to insert security event", ex)`, preserving the root cause stack trace for debugging.

---

## 8. Collections, Generics & Java Features

### Q20. How is `Comparable<SecurityEvent>` implemented and why?
**Answer:**  
`SecurityEvent` implements `Comparable<SecurityEvent>`:
```java
@Override
public int compareTo(SecurityEvent other) {
    return Integer.compare(other.riskScore, this.riskScore); // Descending order
}
```
When `Collections.sort(eventsList)` is called, events are automatically sorted with the highest risk score first, allowing security operators to focus on critical incidents immediately.

### Q21. Explain the use of `Map.merge()` in `ThreatDetector`.
**Answer:**  
`ThreatDetector` uses:
```java
int n = failures.merge(e.getIpAddress(), 1, Integer::sum);
```
`merge()` is a modern Java Collections method. If `e.getIpAddress()` does not exist in the map, it associates it with `1`. If it already exists, it applies `Integer::sum` to increment the existing counter by `1`. This avoids verbose `containsKey` and `get/put` logic.

### Q22. Which Java version features are utilized?
**Answer:**  
- **Enhanced `switch` expressions** with arrow syntax (`->`) in `ThreatDetector.explain()` and `LogSimulator`.
- **`java.time.LocalDateTime`** for modern, thread-safe temporal handling without the legacy issues of `java.util.Date`.
- **Lambdas and Method References** (`Integer::sum`, `e -> navigateTo(...)`).
- **Java Generics** across `List<SecurityEvent>`, `Queue<SecurityEvent>`, and `Map<String, Integer>`.

---

## 9. Member 4 Specific Contributions & Integration

### Q23. What was Member 4's primary role and contribution?
**Answer:**  
Member 4 was responsible for:
1. **Domain Model Verification**: Inspecting and verifying OOP inheritance, polymorphism, and threat level assignments in `SecurityEvent`, `SensorSecurityEvent`, and `AuthSecurityEvent`.
2. **Comprehensive Project Documentation**: Authoring the full project `README.md` (prerequisites, setup, architecture, run instructions).
3. **Academic Project Report**: Authoring `PROJECT_REPORT.md` (objectives, architecture, OOP concepts, implementation breakdown, and test verification).
4. **Viva Voce Defense Guide**: Authoring `VIVA_QA.md` with in-depth questions and answers.
5. **End-to-End Integration & Testing**: Compiling the entire project from scratch, verifying runtime execution, ensuring thread safety, and validating that no prior work was broken.

### Q24. How did you verify integration across all components?
**Answer:**  
I developed an integration test suite that programmatically validated:
- Base class and subclass inheritance relationships (`instanceof`).
- Polymorphic dynamic dispatch of `getFormattedSummary()`.
- Accurate threat scoring and classification by `ThreatDetector`.
- Natural ordering using `Comparable<SecurityEvent>`.
- Graceful database driver fallback handling in `DatabaseManager`.
- Synchronized enqueue/dequeue in `SensorMonitorThread`.
- Custom exception instantiation and chaining.  
All 7 integration test scenarios passed with 100% success.

---

## 10. Rapid-Fire & Tricky Technical Questions

### Q25. Can you instantiate the `ThreatEvaluator` interface directly?
**Answer:**  
No. Interfaces in Java cannot be instantiated directly using `new ThreatEvaluator()`. They must be implemented by a concrete class (such as `ThreatDetector`) or instantiated via an anonymous class or lambda.

### Q26. Why is `id` in `SecurityEvent` an `Integer` object instead of primitive `int`?
**Answer:**  
An unpersisted event newly instantiated in memory does not yet have an assigned database auto-increment ID; its `id` is `null`. A primitive `int` cannot be `null` (it defaults to `0`, which could conflict with an actual database row ID of 0). Using wrapper `Integer` allows representing an unassigned ID cleanly.

### Q27. What happens if two threads call `SensorMonitorThread.enqueueEvent()` simultaneously?
**Answer:**  
Because `enqueueEvent()` is declared `synchronized`, Java acquires the intrinsic lock (monitor) on the `SensorMonitorThread` instance. Only one thread can enter the method at a time; the second thread is blocked until the first exits, guaranteeing thread-safe modifications to `eventQueue`.

### Q28. Why does `SensorSecurityEvent` call `super(...)` as its very first statement?
**Answer:**  
In Java, a subclass constructor must call a constructor of its superclass (either implicitly or explicitly with `super(...)`) before initializing its own fields. This ensures the base class state is properly constructed before subclass-specific initialization occurs.

### Q29. What is the difference between `Statement` and `PreparedStatement`?
**Answer:**  
- `Statement` compiles SQL queries each time they are executed and requires manual string concatenation, making it vulnerable to SQL injection.
- `PreparedStatement` precompiles the SQL query template on the database server, caches execution plans, improves performance for repeated queries, and automatically escapes parameters, completely neutralizing SQL injection attacks.

### Q30. If you have 500 events, how does the Swing UI filter them by severity?
**Answer:**  
In `refreshThreatTable()`, the selected filter string (e.g., `CRITICAL`, `HIGH`, `ALL`) is checked against `e.getThreatLevel().name()`. Only matching events are added as rows to the `DefaultTableModel`, leaving the master `eventsList` intact.
