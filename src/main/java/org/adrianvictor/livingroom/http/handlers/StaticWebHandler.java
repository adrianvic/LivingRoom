package org.adrianvictor.livingroom.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import org.adrianvictor.livingroom.http.Handler;
import org.adrianvictor.livingroom.http.HttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StaticWebHandler implements Handler {

    private static final String STATIC_ROOT = "static";

    public void handleStream(HttpExchange exchange, String path) throws IOException {
        if (path.contains("..")) {
            exchange.sendResponseHeaders(403, -1);
            return;
        }

        String resourcePath = "static/" + path.replace("\\", "/");
        InputStream resourceStream = getClass().getClassLoader().getResourceAsStream(resourcePath);

        if (resourceStream == null) {
            exchange.sendResponseHeaders(404, -1);
            return;
        }

        String contentType;
        if (path.endsWith(".css")) contentType = "text/css";
        else if (path.endsWith(".js")) contentType = "application/javascript";
        else if (path.endsWith(".png")) contentType = "image/png";
        else if (path.endsWith(".jpg") || path.endsWith(".jpeg")) contentType = "image/jpeg";
        else contentType = "application/octet-stream";

        exchange.getResponseHeaders().add("Content-Type", contentType);
        exchange.sendResponseHeaders(200, 0);

        try (resourceStream; OutputStream os = exchange.getResponseBody()) {
            resourceStream.transferTo(os);
        }
    }

    @Override
    public HttpResponse result(String baseAddress, String path, HttpExchange exchange) {
        return HttpResponse.ok(new byte[0], "");
    }

    public String contentType() {
        return "application/octet-stream";
    }
}