package org.adrianvictor.livingroom.auth;

import java.util.UUID;

public class Session {
    private final String sessionId;
    private final String username;
    private final long createdAt;
    private long lastAccessedAt;
    private static final long SESSION_TIMEOUT = 86400000; // 24 hours in milliseconds

    public Session(String username) {
        this.sessionId = UUID.randomUUID().toString();
        this.username = username;
        this.createdAt = System.currentTimeMillis();
        this.lastAccessedAt = createdAt;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getUsername() {
        return username;
    }

    public void updateLastAccessed() {
        this.lastAccessedAt = System.currentTimeMillis();
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - lastAccessedAt > SESSION_TIMEOUT;
    }
}