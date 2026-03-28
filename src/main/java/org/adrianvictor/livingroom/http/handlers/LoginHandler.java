package org.adrianvictor.livingroom.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import org.adrianvictor.livingroom.Logger;
import org.adrianvictor.livingroom.auth.Session;
import org.adrianvictor.livingroom.auth.SessionManager;
import org.adrianvictor.livingroom.http.Handler;
import org.adrianvictor.livingroom.http.HttpResponse;
import org.adrianvictor.livingroom.http.utils.AuthenticationHelper;
import org.adrianvictor.livingroom.services.UserService;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class LoginHandler implements Handler {

    @Override
    public HttpResponse result(String baseAddress, String path, HttpExchange exchange) {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            return HttpResponse.text(400, "Not post.");
        }

        Session session = AuthenticationHelper.getAuthenticatedSession(exchange);

        if (session != null) {
            return HttpResponse.text(400, "You're already logged in.");
        }

        try {
            JSONParser parser = new JSONParser();
            byte[] body = exchange.getRequestBody().readAllBytes();
            String bodyString = new String(body, StandardCharsets.UTF_8);

            JSONObject json = (JSONObject) parser.parse(bodyString);
            String username = (String) json.get("username");
            String password = (String) json.get("password");

            if (password == null || username == null) {
                return HttpResponse.text(400, "You must provide an username and password.");
            }

            try {
                if (UserService.getInstance().getUser(username).auth(password)) {
                    Session s = SessionManager.getInstance().createSession(username);

                    exchange.getResponseHeaders().add("Set-Cookie",
                            "SESSIONID=" + s.getSessionId() + "; Path=/; HttpOnly; SameSite=Strict");

                    String jsonResponse = "{\"success\": true, \"sessionId\": \"" + s.getSessionId() + "\"}";
                    return HttpResponse.ok(jsonResponse.getBytes(StandardCharsets.UTF_8), "application/json");
                } else {
                    return HttpResponse.text(401, "Invalid username or password");
                }
            } catch (Exception e) {
                Logger.error("User lookup error: " + e.getMessage());
                return HttpResponse.text(401, "Invalid username or password");
            }
        } catch (IOException e) {
            Logger.error("Error reading request body: " + e.getMessage());
            return HttpResponse.text(400, "Invalid request");
        } catch (ParseException e) {
            Logger.error("Error parsing JSON: " + e.getMessage());
            return HttpResponse.text(400, "Invalid JSON format");
        }
    }
}