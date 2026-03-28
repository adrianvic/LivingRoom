package org.adrianvictor.livingroom.http.utils;

import com.sun.net.httpserver.HttpExchange;
import org.adrianvictor.livingroom.auth.Session;
import org.adrianvictor.livingroom.auth.SessionManager;
import org.adrianvictor.livingroom.utils.net.Cookie;

import java.io.IOException;

public class AuthenticationHelper {
    public static Session getAuthenticatedSession(HttpExchange exchange) {
        String id = Cookie.getSessionIdFromCookies(exchange);
        if (id != null) {
            Session session = SessionManager.getInstance().getSession(id);
            if (session != null) {
                return session;
            }
        }

        String bearerToken = getBearerToken(exchange);
        if (bearerToken != null) {
            Session session = SessionManager.getInstance().getSession(bearerToken);
            if (session != null) {
                return session;
            }
        }
        return null;
    }

    private static String getBearerToken(HttpExchange exchange) {
        String auth = exchange.getRequestHeaders().getFirst("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            return auth.substring(7);
        }
        return null;
    }

    public static void sendUnauthorized(HttpExchange exchange, String message) throws IOException {
        byte[] response = (message == null) ? "Not authorized.".getBytes() : message.getBytes();
        exchange.sendResponseHeaders(401, response.length);
        exchange.getResponseBody().write(response);
        exchange.getResponseBody().close();
    }
}
