package org.adrianvictor.livingroom.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import org.adrianvictor.livingroom.http.Handler;
import org.adrianvictor.livingroom.http.HttpResponse;
import org.adrianvictor.livingroom.Logger;
import org.adrianvictor.livingroom.data.Database;
import org.adrianvictor.livingroom.data.catalog.Item;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class FileHandler implements Handler {

    public void handleStream(HttpExchange exchange, String path) {
        String[] parts = path.split("/");

        if (parts.length < 2) {
            send404(exchange, "Usage: .../game_id/DownloadName");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(parts[0]);
        } catch (NumberFormatException e) {
            send404(exchange, "The provided game ID %s is not a valid number.".formatted(parts[0]));
            return;
        }

        String platform = URLDecoder.decode(parts[1], StandardCharsets.UTF_8);
        Item game = Database.getInstance().getGame(id);
        if (game == null) {
            send404(exchange, "Game not found.");
            return;
        }

        List<String> files = game.getVersions();
        if (files.isEmpty() || !files.contains(platform)) {
            send404(exchange, "This game has no such download: %s.".formatted(platform));
            return;
        }

        File gameFile = new File(game.location().getParent() + '/' + game.getVersionFile(platform));

        if (!gameFile.exists()) {
            Logger.info(gameFile.getPath());
            send404(exchange, "Could not find the file you're looking for. Sorry.");
            return;
        }

        Logger.info("Providing game file: " + gameFile.getPath());

        exchange.getResponseHeaders().add("Content-Type", contentType());
        exchange.getResponseHeaders().add(
                "Content-Disposition",
                "attachment; filename=\"" + gameFile.getName() + "\""
        );

        try (FileInputStream fis = new FileInputStream(gameFile);
             OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(200, gameFile.length());

            byte[] buffer = new byte[8192]; // 8 KB chunks
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            send404(exchange);
        }
    }

    private void send404(HttpExchange exchange, String message) {
        byte[] notFound = message.getBytes();
        try {
            exchange.sendResponseHeaders(404, notFound.length);
            exchange.getResponseBody().write(notFound);
            exchange.getResponseBody().close();
        } catch (IOException ignored) {}
    }

    private void send404(HttpExchange exchange) {
        send404(exchange, "Not found.");
    }

    @Override
    public HttpResponse result(String baseAddress, String path, HttpExchange exchange) {
        return HttpResponse.ok(new byte[0], "");
    }

    public String contentType() {
        return "application/octet-stream";
    }
}