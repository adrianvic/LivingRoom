package org.adrianvictor.livingroom.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import freemarker.template.Configuration;
import org.adrianvictor.livingroom.auth.User;
import org.adrianvictor.livingroom.http.Handler;
import org.adrianvictor.livingroom.http.HttpResponse;
import org.adrianvictor.livingroom.Logger;
import org.adrianvictor.livingroom.Main;
import org.adrianvictor.livingroom.services.UserService;
import org.adrianvictor.livingroom.utils.net.Cookie;
import org.adrianvictor.livingroom.web.Page;
import org.adrianvictor.livingroom.web.Pages;
import org.adrianvictor.livingroom.auth.Session;
import org.adrianvictor.livingroom.auth.SessionManager;
import org.adrianvictor.livingroom.web.pages.Login;
import org.adrianvictor.livingroom.web.pages.NotFound;

import java.util.HashMap;
import java.util.Map;

public class WebHandler implements Handler {

    private final Configuration cfg;

    public WebHandler() {
        cfg = new Configuration(Configuration.VERSION_2_3_32);
        cfg.setClassForTemplateLoading(
                Main.class,
                "/templates"
        );
    }

    @Override
    public HttpResponse result(String baseAddress, String path, HttpExchange exchange) {
        Map<String, Object> data = new HashMap<>();
        String firstPath = path.split("/")[0];
        String remainingPath = path.substring(firstPath.length());
        if (remainingPath.startsWith("/")) {
            remainingPath = remainingPath.substring(1);
        }

        data.put("webpref", "web");

        String sessionId = Cookie.getSessionIdFromCookies(exchange);
        Session session = SessionManager.getInstance().getSession(sessionId);

        if (firstPath.equals("login")) {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                return handleLoginPage(baseAddress, remainingPath, exchange);
            }

            Page page = Pages.get(firstPath);
            if (page == null) {
                page = new Login();
            }
            return HttpResponse.ok(page.result(cfg, baseAddress, remainingPath, data, exchange).getBytes(), "text/html");
        }

        if (session == null) {
            return HttpResponse.ok(new Login().result(cfg, baseAddress, "", data, exchange).getBytes(), "text/html");
        }

        Page page = Pages.get(firstPath);

        User user = UserService.getInstance().getUser(session.getUsername());

        data.put("username", session.getUsername());
        data.put("userRole", user.role().toString().toLowerCase());

        if (page == null) {
            return HttpResponse.ok(new NotFound().result(cfg, baseAddress, path, data, exchange).getBytes(), "text/html");
        }

        return HttpResponse.ok(page.result(cfg, baseAddress, remainingPath, data, exchange).getBytes(), "text/html");
    }

    private HttpResponse handleLoginPage(String baseAddress, String path, HttpExchange exchange) {
        String method = exchange.getRequestMethod();

        Map<String, Object> data = new HashMap<>();
        data.put("webpref", "web");

        if ("POST".equals(method)) {
            try {
                String body = new String(exchange.getRequestBody().readAllBytes());
                String[] params = body.split("&");
                String username = null;
                String password = null;


                for (String param : params) {
                    String[] kv = param.split("=");
                    if (kv.length == 2) {
                        if ("username".equals(kv[0])) {
                            username = java.net.URLDecoder.decode(kv[1], java.nio.charset.StandardCharsets.UTF_8);
                        } else if ("password".equals(kv[0])) {
                            password = java.net.URLDecoder.decode(kv[1], java.nio.charset.StandardCharsets.UTF_8);
                        }
                    }
                }

                if (UserService.getInstance().getUser(username).auth(password)) {
                    Session session = SessionManager.getInstance().createSession(username);
                    Cookie.setSessionCookie(exchange, session.getSessionId());

                    return HttpResponse.redirect("/web/");
                } else {
                    return HttpResponse.ok(new Login().result(cfg, baseAddress, "", data, exchange).getBytes(), "text/html");
                }
            } catch (Exception e) {
                Logger.error("Login error: " + e.getMessage());
                return HttpResponse.ok(new NotFound().result(cfg, baseAddress, path, data, exchange).getBytes(), "text/html");
            }
        }

        return HttpResponse.ok(new NotFound().result(cfg, baseAddress, path, data, exchange).getBytes(), "text/html");
    }
}