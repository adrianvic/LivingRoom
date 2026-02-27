package org.adrianvictor.livingroom.utils.net;

import com.sun.net.httpserver.HttpExchange;

public class Cookie {
    private static final String SESSION_COOKIE_NAME = "SESSIONID";

    public static String getSessionIdFromCookies(HttpExchange exchange) {
        String cookieHeader = exchange.getRequestHeaders().getFirst("Cookie");
        if (cookieHeader == null) {
            return null;
        }

        String[] cookies = cookieHeader.split(";");
        for (String cookie : cookies) {
            cookie = cookie.trim();
            if (cookie.startsWith(SESSION_COOKIE_NAME + "=")) {
                return cookie.substring((SESSION_COOKIE_NAME + "=").length());
            }
        }
        return null;
    }

    public static void setSessionCookie(HttpExchange exchange, String sessionId) {
        String cookie = SESSION_COOKIE_NAME + "=" + sessionId + "; Path=/; HttpOnly; SameSite=Strict";
        exchange.getResponseHeaders().add("Set-Cookie", cookie);
    }

    public static void clearSessionCookie(HttpExchange exchange) {
        String cookie = SESSION_COOKIE_NAME + "=; Path=/; Max-Age=0; HttpOnly";
        exchange.getResponseHeaders().add("Set-Cookie", cookie);
    }
}