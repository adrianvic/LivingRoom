package org.adrianvictor.livingroom.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.adrianvictor.livingroom.Logger;
import org.adrianvictor.livingroom.Main;
import org.adrianvictor.livingroom.auth.Session;
import org.adrianvictor.livingroom.http.handlers.*;
import org.adrianvictor.livingroom.http.utils.AuthenticationHelper;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Map;

public class Server {
    static Server instance;
    HttpServer httpServer;

    private Server() {
        try {
            httpServer = HttpServer.create(new InetSocketAddress(Main.getConfig().getPort()), 0);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Server getInstance() {
        if (instance == null) {
            instance = new Server();
        }

        return instance;
    }

    public void start() {
      httpServer.start();
    }

    public void registerHandler(Handler handler, String path) {
        httpServer.createContext(path, new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) {
                try {
                    String fullPath = exchange.getRequestURI().getPath();

                    String afterBase = "";
                    if (fullPath.length() > path.length()) {
                        afterBase = fullPath.substring(path.length() + 1);
                    }
                    String decoded = java.net.URLDecoder.decode(afterBase, java.nio.charset.StandardCharsets.UTF_8);

                    int thirdSlash = nthIndexOf(fullPath, '/', 3);
                    String baseAddress;
                    if (thirdSlash == -1) {
                        baseAddress = fullPath;
                    } else {
                        baseAddress = fullPath.substring(0, thirdSlash);
                    }

                    exchange.getResponseHeaders().add("X-Content-Type-Options", "nosniff");
                    exchange.getResponseHeaders().add("X-Frame-Option", "DENY");
                    exchange.getResponseHeaders().add("Referrer-Policy", "no-referrer");
                    exchange.getResponseHeaders().add("Content-Security-Policy", "");
                    exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                    exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                    exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

                    if (!(handler instanceof WebHandler) && !(handler instanceof LoginHandler) && !(handler instanceof StaticWebHandler) && !(handler instanceof WebRedirectHandler)) {
                        Session session = AuthenticationHelper.getAuthenticatedSession(exchange);
                        if (session == null) {
                            try {
                                AuthenticationHelper.sendUnauthorized(exchange, null);
                                return;
                            } catch (IOException ignored) {}
                        }
                    }

                    switch (handler) {
                        case FileHandler fileHandler -> fileHandler.handleStream(exchange, decoded);
                        case ImageHandler imageHandler -> imageHandler.handleStream(exchange, decoded);
                        case StaticWebHandler staticWebHandler -> staticWebHandler.handleStream(exchange, decoded);
                        default -> {
                            HttpResponse result = handler.result(baseAddress, decoded, exchange);

                            for (Map.Entry<String, String> header : result.headers().entrySet()) {
                                exchange.getResponseHeaders().set(header.getKey(), header.getValue());
                            }

                            exchange.sendResponseHeaders(result.status(), result.body().length);

                            OutputStream os = exchange.getResponseBody();
                            os.write(result.body());
                            os.close();
                        }
                    }
                } catch (IOException ignored) {
                } catch (Exception e) {
                    Logger.error("Handler error: " + e.getMessage());
                    e.printStackTrace();
                    try {
                        exchange.sendResponseHeaders(500, 0);
                        exchange.close();
                    } catch (Exception ignored) {
                    }
                } finally {
                    exchange.close();
                }
            }
        });
    }

    private static int nthIndexOf(String str, char c, int n) {
        int pos = -1;
        for (int i = 0; i < n; i++) {
            pos = str.indexOf(c, pos + 1);
            if (pos == -1) break;
        }
        return pos;
    }
}