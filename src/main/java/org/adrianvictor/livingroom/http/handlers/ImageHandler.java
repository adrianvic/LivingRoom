package org.adrianvictor.livingroom.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import org.adrianvictor.livingroom.http.Handler;
import org.adrianvictor.livingroom.http.HttpResponse;
import org.adrianvictor.livingroom.data.Database;
import org.adrianvictor.livingroom.data.catalog.Item;
import org.adrianvictor.livingroom.data.catalog.Property;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ImageHandler implements Handler {
    public void handleStream(HttpExchange exchange, String path) throws IOException {
        int id = Integer.parseInt(path);
        Item game = Database.getInstance().getGame(id);

        if (game.properties().get(Property.ARTWORK) == null || game.properties().get(Property.ARTWORK).isEmpty()) {
            byte[] notFound = "Not found".getBytes();
            exchange.sendResponseHeaders(404, notFound.length);
            exchange.getResponseBody().write(notFound);
            exchange.getResponseBody().close();
            return;
        }

        File image = new File(game.location().getParent() + '/' + game.properties().get(Property.ARTWORK));

        exchange.getResponseHeaders().add("Content-Type", "image/png");
        exchange.sendResponseHeaders(200, image.length());

        try (FileInputStream fis = new FileInputStream(image)) {
            byte[] buffer = new byte[8192]; // 8 KB chunks
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                exchange.getResponseBody().write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            exchange.getResponseBody().close();
        }
    }

    @Override
    public HttpResponse result(String baseAddress, String path, HttpExchange exchange) {
        return HttpResponse.ok(new byte[0], "");
    }

    public String contentType() {
        return "image/png";
    }
}