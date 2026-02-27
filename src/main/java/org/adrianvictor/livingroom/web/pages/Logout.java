package org.adrianvictor.livingroom.web.pages;

import com.sun.net.httpserver.HttpExchange;
import freemarker.template.Configuration;
import org.adrianvictor.livingroom.auth.SessionManager;
import org.adrianvictor.livingroom.utils.net.Cookie;
import org.adrianvictor.livingroom.web.Page;

import java.util.Map;

public class Logout implements Page {

    @Override
    public String result(Configuration cfg, String baseAddress, String path, Map<String, Object> data, HttpExchange exchange) {
        String sessionId = Cookie.getSessionIdFromCookies(exchange);
        SessionManager sm = SessionManager.getInstance();
        sm.destroySession(sessionId);
        data.remove("username");
        return new Login().result(cfg, baseAddress, path, data, exchange);
    }
}
