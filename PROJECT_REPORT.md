# Smart Home Security Threat Monitor

## 1. Introduction
The Smart Home Security Threat Monitor is a Java application designed to monitor simulated smart-home security events, evaluate potential threats, and record event information.

## 2. Objectives
- Represent security events using object-oriented Java classes.
- Monitor simulated sensor and authentication events.
- Evaluate events and identify potential threats.
- Store event information using database components.
- Display monitoring information through a user interface.

## 3. Technologies
- Java
- Object-oriented programming (OOP)
- Java threads for background monitoring
- Database access through the project's database module
- Git and GitHub for collaborative development

## 4. Project Structure
The Java source code is located under `latest pure java project/src/main/java/com/security/`.

- `Main.java` — application entry point.
- `model/` — security event classes and threat-level definitions.
- `detector/` — threat detection and evaluation.
- `service/` — event simulation and sensor monitoring.
- `database/` — database access and event persistence.
- `exception/` — custom exception classes.
- `ui/` — application interface.

## 5. Object-Oriented Design
The model package includes `SecurityEvent`, `SensorSecurityEvent`, and `AuthSecurityEvent`, which represent different kinds of security events. The `ThreatLevel` enum represents threat severity levels. The event classes provide a structured way to work with different event types.

## 6. System Workflow
1. The application starts through its main class.
2. Simulated security events are generated or received.
3. Monitoring services process the events.
4. Detection and evaluation components assess potential threats.
5. Database components can store event records.
6. The user interface presents application information.

## 7. Collaboration
The project is maintained collaboratively using Git and GitHub. Team members contribute application modules and documentation to the shared `main` branch.

## 8. Testing
The application should be compiled and run using the project's configured Java environment. Testing should verify event creation, threat evaluation, database operations, exception handling, and user-interface behavior. Record actual test results after running the application.

## 9. Conclusion
The project brings together Java object-oriented design, event monitoring, threat evaluation, database components, and a user interface in a smart-home security monitoring application.

## 10. Future Improvements
- Integrate real smart-home sensors.
- Add configurable alert thresholds.
- Improve event filtering and reporting.
- Expand automated tests and error handling.