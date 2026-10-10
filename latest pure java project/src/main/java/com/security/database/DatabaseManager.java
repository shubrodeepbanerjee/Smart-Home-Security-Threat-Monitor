package com.security.database;

import com.security.exception.DatabaseException;
import com.security.model.SecurityEvent;
import com.security.model.ThreatLevel;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DatabaseManager implements EventDao using SQLite JDBC driver.
 * Manages database connection, table initialization, and CRUD operations
 * for security audit records using parameterized PreparedStatements.
 */
public class DatabaseManager implements EventDao {
    private static final String URL = "jdbc:sqlite:security.db";
    private static final DatabaseManager INSTANCE = new DatabaseManager();
    private static boolean driverLoaded = false;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
            driverLoaded = true;
        } catch (ClassNotFoundException e) {
            System.err.println("Notice: SQLite JDBC driver not loaded in classpath. Operating with fallback.");
        }
    }

    public static DatabaseManager getInstance() {
        return INSTANCE;
    }

    // Static convenience helpers for UI components
    public static void initialize() {
        try {
            INSTANCE.initDatabase();
        } catch (DatabaseException e) {
            System.err.println("Database initialization warning: " + e.getMessage());
        }
    }

    public static void save(SecurityEvent e) {
        try {
            INSTANCE.saveEvent(e);
        } catch (DatabaseException ex) {
            System.err.println("Database save warning: " + ex.getMessage());
        }
    }

    public static List<SecurityEvent> all() {
        try {
            return INSTANCE.allEvents();
        } catch (DatabaseException ex) {
            System.err.println("Database query warning: " + ex.getMessage());
            return new ArrayList<>();
        }
    }

    // EventDao Interface Implementation
    @Override
    public void initDatabase() throws DatabaseException {
        if (!driverLoaded) {
            try {
                Class.forName("org.sqlite.JDBC");
                driverLoaded = true;
            } catch (ClassNotFoundException e) {
                throw new DatabaseException("SQLite JDBC driver is missing from classpath.", e);
            }
        }

        String sql = "CREATE TABLE IF NOT EXISTS security_events (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "timestamp TEXT NOT NULL, " +
                "username TEXT NOT NULL, " +
                "ip_address TEXT NOT NULL, " +
                "event_type TEXT NOT NULL, " +
                "description TEXT NOT NULL, " +
                "threat_level TEXT NOT NULL, " +
                "risk_score INTEGER NOT NULL)";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException ex) {
            throw new DatabaseException("Failed to create security_events schema", ex);
        }
    }

    @Override
    public void saveEvent(SecurityEvent e) throws DatabaseException {
        if (!driverLoaded) return;
        String sql = "INSERT INTO security_events(timestamp, username, ip_address, event_type, description, threat_level, risk_score) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, e.getTimestamp().toString());
            pstmt.setString(2, e.getUsername());
            pstmt.setString(3, e.getIpAddress());
            pstmt.setString(4, e.getEventType());
            pstmt.setString(5, e.getDescription());
            pstmt.setString(6, e.getThreatLevel().name());
            pstmt.setInt(7, e.getRiskScore());
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            throw new DatabaseException("Failed to insert security event into SQLite", ex);
        }
    }

    @Override
    public List<SecurityEvent> allEvents() throws DatabaseException {
        List<SecurityEvent> list = new ArrayList<>();
        if (!driverLoaded) return list;

        String sql = "SELECT id, timestamp, username, ip_address, event_type, description, threat_level, risk_score " +
                "FROM security_events ORDER BY id DESC";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                SecurityEvent event = new SecurityEvent(
                        LocalDateTime.parse(rs.getString("timestamp")),
                        rs.getString("username"),
                        rs.getString("ip_address"),
                        rs.getString("event_type"),
                        rs.getString("description")
                );
                event.setId(rs.getInt("id"));
                event.setThreatLevel(ThreatLevel.valueOf(rs.getString("threat_level")));
                event.setRiskScore(rs.getInt("risk_score"));
                list.add(event);
            }
        } catch (SQLException ex) {
            throw new DatabaseException("Failed to query security events", ex);
        }
        return list;
    }

    @Override
    public int countByThreatLevel(String threatLevel) throws DatabaseException {
        if (!driverLoaded) return 0;
        String sql = "SELECT COUNT(*) AS total FROM security_events WHERE threat_level = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, threatLevel);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (SQLException ex) {
            throw new DatabaseException("Error counting events by threat level", ex);
        }
        return 0;
    }
}
