package org.adrianvictor.livingroom.auth;

import org.adrianvictor.livingroom.Main;
import org.adrianvictor.livingroom.services.UserService;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {
    private static final SessionManager instance = new SessionManager();
    private final Map<String, Session> sessions = new HashMap<>();

    private SessionManager() {}

    public static SessionManager getInstance() {
        return instance;
    }

    public Session createSession(String username) {
        Session session = new Session(username);
        sessions.put(session.getSessionId(), session);
        return session;
    }

    public Session getSession(String sessionId) {
        Session session = sessions.get(sessionId);
        if (session != null && !session.isExpired()) {
            session.updateLastAccessed();
            return session;
        }
        if (session != null) {
            sessions.remove(sessionId);
        }
        return null;
    }

    public void destroySession(String sessionId) {
        sessions.remove(sessionId);
    }
}