package org.adrianvictor.livingroom.web.pages;

import com.sun.net.httpserver.HttpExchange;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.adrianvictor.livingroom.Logger;
import org.adrianvictor.livingroom.data.Database;
import org.adrianvictor.livingroom.data.catalog.Item;
import org.adrianvictor.livingroom.data.catalog.Property;
import org.adrianvictor.livingroom.utils.image.ColorExtractor;
import org.adrianvictor.livingroom.web.Page;
import org.adrianvictor.livingroom.web.QuickResponses;

import java.io.File;
import java.io.StringWriter;
import java.util.Map;

public class Game implements Page {
    @Override
    public String result(Configuration cfg, String baseAddress, String path, Map<String, Object> data, HttpExchange exchange) {
        try {
            Template template = cfg.getTemplate("game.ftl");

            String[] args = path.split("/");

            int id;
            try {
                id = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                return QuickResponses.notFound();
                // send404(exchange, "The provided game ID %s is not a valid number.".formatted(args[0]));
            }

            Item game = Database.getInstance().getGame(id);

            Map<String, String> gameMap = game.getPropertiesString();

            try {
                String accentHEX = ColorExtractor.getDominantColor(game);
                data.put("accentColor", accentHEX);
            } catch (Exception e) {
                data.put("accentColor", "#FFFFFF");
            }

            data.put("game", gameMap);
            data.put("versions", game.getVersions());

            StringWriter writer = new StringWriter();
            template.process(data, writer);

            return writer.toString();
        } catch (Exception e) {
            Logger.error(e.getMessage());
            return "<h1>Error</h1>";
        }
    }
}
