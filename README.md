# Smart Home Security Threat Monitor

[![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Platform](https://img.shields.io/badge/Platform-macOS%20%7C%20Linux%20%7C%20Windows-0078D4?style=for-the-badge)](https://github.com/shubrodeepbanerjee/Smart-Home-Security-Threat-Monitor)
[![Architecture](https://img.shields.io/badge/Architecture-Pure%20Java%20%2F%20Swing%20%2F%20JDBC-00B4D8?style=for-the-badge)](https://github.com/shubrodeepbanerjee/Smart-Home-Security-Threat-Monitor)
[![Status](https://img.shields.io/badge/Build-Passing-brightgreen?style=for-the-badge)](https://github.com/shubrodeepbanerjee/Smart-Home-Security-Threat-Monitor)

An explainable, offline, privacy-first cybersecurity desktop monitoring application built entirely in Java. The system monitors simulated smart-home sensor telemetry, entry guards, and gateway authentication logs in real time. It calculates dynamic heuristic risk scores (0–100), classifies incidents into four threat levels (`LOW`, `MEDIUM`, `HIGH`, `CRITICAL`), provides transparent rule-based incident explanations, logs events via a persistent Data Access Object (DAO) layer, and delivers a modern desktop dashboard using Java Swing.

---

## Table of Contents
1. [Key Features](#key-features)
2. [Architecture & OOP Design](#architecture--oop-design)
3. [Project Structure](#project-structure)
4. [Prerequisites](#prerequisites)
5. [Dependencies](#dependencies)
6. [Installation & Build Instructions](#installation--build-instructions)
7. [Running the Application](#running-the-application)
8. [Heuristic Threat Scoring Rules](#heuristic-threat-scoring-rules)
9. [Database Persistence & Fallback](#database-persistence--fallback)
10. [Team Contributions](#team-contributions)
11. [Verification & Testing](#verification--testing)
12. [License](#license)

---

## Key Features

- **Object-Oriented Domain Hierarchy**: Extensible base event model (`SecurityEvent`) specialized for hardware telemetry (`SensorSecurityEvent`) and authentication traffic (`AuthSecurityEvent`).
- **Explainable Anomaly Detection Engine**: Rule-based scoring engine (`ThreatDetector`) providing 0–100 risk scoring, threshold classification, and plain-English threat rationale.
- **Multithreaded Sensor Monitoring**: Background polling worker thread (`SensorMonitorThread`) running safely as a daemon thread with synchronized inter-thread queue coordination (`wait()` / `notifyAll()`).
- **Robust Persistence Layer (DAO Pattern)**: SQLite-backed audit storage (`DatabaseManager`) with automatic schema initialization and safe fallback when external drivers are absent.
- **Interactive Java Swing UI**: High-contrast dark cybersecurity console with navigation tabs, live sensor grid, incident table, KPI metric cards, explainable AI inspector, and CSV export.
- **100% Offline & Private**: Zero external cloud telemetry or tracking. Operates entirely locally.

---

## Architecture & OOP Design

The project is structured according to clean Object-Oriented Programming (OOP) principles:

```
[ Sensor / Auth Telemetry ]
            │
            ▼
   [ LogSimulator / UI ]
            │ (Enqueues events)
            ▼
 [ SensorMonitorThread ]  ◄── Multithreading & Synchronization (wait/notifyAll)
            │ (Polls & processes)
            ▼
    [ ThreatDetector ]    ◄── Polymorphism & Abstraction (ThreatEvaluator interface)
            │ (Evaluates 0-100 score & ThreatLevel)
            ▼
    [ DatabaseManager ]   ◄── Persistence & Abstraction (EventDao interface)
            │ (Writes to security.db via PreparedStatement)
            ▼
[ SmartHomeSecurityApp ]  ◄── Java Swing Desktop UI & Presentation
```

### Core OOP Highlights
- **Encapsulation**: Private fields, immutable identifiers/timestamps, and controlled accessor/mutator methods across all domain entities.
- **Inheritance**: `SensorSecurityEvent` and `AuthSecurityEvent` inherit common telemetry attributes from `SecurityEvent`.
- **Polymorphism**:
  - *Dynamic Method Overriding*: `getFormattedSummary()` produces specialized summaries for base, sensor, and auth events at runtime.
  - *Interface Polymorphism*: Implementation of `ThreatEvaluator` by `ThreatDetector`, `EventDao` by `DatabaseManager`, and `Runnable` by `SensorMonitorThread`.
  - *Generic Comparable*: `SecurityEvent` implements `Comparable<SecurityEvent>` to enable polymorphic descending risk sorting.
- **Abstraction**: Interfaces decouple consumers from persistence (`EventDao`) and evaluation mechanisms (`ThreatEvaluator`).
- **Exception Handling**: Custom exception hierarchy featuring `DatabaseException` (checked, exception chaining) and `SecurityAlertException` (stateful exception carrying risk scores).

---

## Project Structure

```
Smart-Home-Security-Threat-Monitor/
├── README.md                      # Complete installation, architecture, and run guide
├── PROJECT_REPORT.md              # Detailed academic project report
├── VIVA_QA.md                     # Comprehensive viva voce preparation Q&A guide
└── latest pure java project/       # Pure Java source project root
    └── src/
        └── main/
            └── java/
                └── com/
                    └── security/
                        ├── Main.java                        # Application entry point
                        ├── model/                           # Domain entities & enums
                        │   ├── ThreatLevel.java             # Enum: LOW, MEDIUM, HIGH, CRITICAL
                        │   ├── SecurityEvent.java           # Base security event model
                        │   ├── SensorSecurityEvent.java     # Hardware/sensor event subclass
                        │   └── AuthSecurityEvent.java       # Gateway authentication event subclass
                        ├── detector/                        # Cybersecurity detection engine
                        │   ├── ThreatEvaluator.java         # Detection contract interface
                        │   └── ThreatDetector.java          # Heuristic scoring implementation
                        ├── database/                        # Persistence layer
                        │   ├── EventDao.java                # Data Access Object interface
                        │   └── DatabaseManager.java         # SQLite JDBC manager & fallback
                        ├── service/                         # Simulation & background threads
                        │   ├── LogSimulator.java            # Event telemetry generator
                        │   └── SensorMonitorThread.java     # Synchronized daemon worker thread
                        ├── exception/                       # Custom exception classes
                        │   ├── DatabaseException.java       # Custom persistence exception
                        │   └── SecurityAlertException.java  # Custom threshold breach exception
                        └── ui/                              # User Interface
                            └── SmartHomeSecurityApp.java    # Pure Java Swing desktop interface
```

---

## Prerequisites

- **Java Development Kit (JDK)**: JDK 17 (LTS) or higher (JDK 17, 21, 25, 27 supported).
- **Operating System**: macOS, Linux, or Windows (cross-platform compatible).
- **Git**: For version control and cloning.

Check your installed Java version:
```bash
java -version
javac -version
```

---

## Dependencies

- **Core Application**: **Zero external library dependencies required**. The entire core application, data models, threat engine, simulation, threading, exceptions, and user interface run on standard Java SE (`java.desktop`, `java.sql`, `java.time`, `java.util`).
- **SQLite Persistence (Optional)**: If SQLite persistence to disk (`security.db`) is desired, include `sqlite-jdbc` (version 3.40.0.0 or later) on the classpath. If the driver is not present, the application's built-in fallback operates gracefully in memory without crashing.

---

## Installation & Build Instructions

### 1. Clone the Repository
```bash
git clone https://github.com/shubrodeepbanerjee/Smart-Home-Security-Threat-Monitor.git
cd Smart-Home-Security-Threat-Monitor
```

### 2. Compile via Command Line

Navigate to the Java project directory and compile all source files into a `bin/` directory:

```bash
cd "latest pure java project"
mkdir -p bin
javac -d bin $(find src/main/java -name "*.java")
```

On Windows Command Prompt:
```cmd
cd "latest pure java project"
mkdir bin
for /R src\main\java %f in (*.java) do javac -d bin -cp src\main\java "%f"
```

---

## Running the Application

### 1. Run via Command Line
From within the `latest pure java project` directory (after compiling to `bin/`):

```bash
java -cp bin com.security.Main
```

Or run the Swing application class directly:
```bash
java -cp bin com.security.ui.SmartHomeSecurityApp
```

### 2. Run with SQLite JDBC Driver (Optional)
If you have downloaded the SQLite JDBC driver jar (e.g. `sqlite-jdbc-3.45.1.0.jar`):

```bash
java -cp "bin:sqlite-jdbc-3.45.1.0.jar" com.security.Main
```
*(On Windows, use a semicolon `;` as classpath separator instead of `:`)*.

### 3. Open and Run in IDEs

- **IntelliJ IDEA**:
  1. Open IntelliJ and select **Open**.
  2. Select the `latest pure java project` folder.
  3. Ensure Project SDK is set to Java 17+ under **File -> Project Structure -> Project**.
  4. Right-click on `com.security.Main` and select **Run 'Main.main()'**.

- **VS Code**:
  1. Open the repository folder in VS Code.
  2. Ensure the *Extension Pack for Java* is installed.
  3. Navigate to `latest pure java project/src/main/java/com/security/Main.java`.
  4. Click the **Run** button above the `main` method.

---

## Heuristic Threat Scoring Rules

The `ThreatDetector` calculates an explainable risk score (0–100) based on clear heuristic rules:

| Factor | Condition | Penalty |
|---|---|---|
| **Event Classification** | `SUSPICIOUS_ACCESS` | +60 pts |
| | `UNKNOWN_USER` | +45 pts |
| | `LOGIN_FAILED` | +20 pts (+10 pts per streak on same IP, max +50) |
| | Routine Event (`LOGIN_SUCCESS`, etc.) | +5 pts |
| **Temporal Check** | Off-hours access (before 06:00 or after 23:00) | +15 pts |
| **Subnet Anomaly** | Untrusted external subnet (IP starts with `10.99.`) | +15 pts |

### Threat Level Thresholds
- **CRITICAL** (Score: 76–100): High risk of active intrusion. Immediate IP quarantine recommended.
- **HIGH** (Score: 51–75): Elevated anomaly pattern. Sensor inspection and rate limiting recommended.
- **MEDIUM** (Score: 26–50): Noticeable deviation from baseline. Logging and user verification advised.
- **LOW** (Score: 0–25): Routine authorized smart home telemetry. No intervention required.

---

## Database Persistence & Fallback

The persistence module implements the **Data Access Object (DAO)** pattern:
- **Interface**: `EventDao` defines `initDatabase()`, `saveEvent()`, `allEvents()`, and `countByThreatLevel()`.
- **Implementation**: `DatabaseManager` connects to `jdbc:sqlite:security.db` using parameterized `PreparedStatement` queries to prevent SQL injection.
- **Graceful Fallback**: If SQLite JDBC driver is not on the classpath, the static initializer catches `ClassNotFoundException`, logs an informative notice, and allows the application to continue running using in-memory state without crashing.

---

## Team Contributions

This project was developed collaboratively by a team of 4 members:

| Member | Primary Responsibilities | Pushed Artifacts & Modules |
|---|---|---|
| **Member 1** (`shubrodeepbanerjee`) | Database persistence, schema design, custom exceptions | `DatabaseManager.java`, `EventDao.java`, `DatabaseException.java`, SQLite schema |
| **Member 2** (`hasnain01-ui`) | Threat detection engine & multithreaded monitoring service | `ThreatEvaluator.java`, `ThreatDetector.java`, `SensorMonitorThread.java` |
| **Member 3** (`saurabhmauryasamrat`) | Domain modeling, event simulation & desktop Swing UI | `SecurityEvent.java`, `SensorSecurityEvent.java`, `AuthSecurityEvent.java`, `ThreatLevel.java`, `LogSimulator.java`, `SmartHomeSecurityApp.java`, `Main.java` |
| **Member 4** (`vikasyadav34289-sys`) | Project report, viva QA guide, documentation, end-to-end integration & verification | `README.md`, `PROJECT_REPORT.md`, `VIVA_QA.md`, verification test suite, final integration validation |

---

## Verification & Testing

The project has been compiled and verified with zero compilation warnings or errors under JDK 27.
The test plan validates:
1. **Inheritance & Hierarchy**: `SensorSecurityEvent` and `AuthSecurityEvent` extend `SecurityEvent`.
2. **Polymorphic Dispatch**: `getFormattedSummary()` dynamically invokes the proper subtype representation.
3. **Sorting Contract**: `Comparable<SecurityEvent>` correctly orders events in descending order of risk score.
4. **Threat Engine Scoring**: Accurate evaluation of base, subnet, and off-hour penalties.
5. **Thread Safety**: Inter-thread communication via synchronized queue in `SensorMonitorThread`.
6. **Exception Robustness**: Validation of `DatabaseException` and `SecurityAlertException`.

---

## License

This project is developed for educational and academic demonstration purposes as part of the Object-Oriented Programming (OOP) and Cybersecurity curriculum. All rights reserved.