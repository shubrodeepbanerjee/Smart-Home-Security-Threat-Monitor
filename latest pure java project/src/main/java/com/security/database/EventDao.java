package com.security.database;

import com.security.model.SecurityEvent;
import com.security.exception.DatabaseException;
import java.util.List;

/**
 * Data Access Object (DAO) Interface for security event persistence.
 * Provides abstraction for database operations and event queries.
 */
public interface EventDao {
    void initDatabase() throws DatabaseException;
    void saveEvent(SecurityEvent event) throws DatabaseException;
    List<SecurityEvent> allEvents() throws DatabaseException;
    int countByThreatLevel(String threatLevel) throws DatabaseException;
}
