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

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Index implements Page {
    @Override
    public String result(Configuration cfg, String baseAddress, String path, Map<String, Object> data, HttpExchange exchange) {
        try {
            Template template = cfg.getTemplate("games.ftl");

            List<Map<String, Object>> gamesList = new ArrayList<>();

            for (Item item : Database.getInstance().getAllGames()) {
                Map<String, Object> map = new HashMap<>();
                for (Map.Entry<Property, String> e : item.properties().entrySet()) {
                    map.put(e.getKey().name(), e.getValue());
                }
                map.put("versions", item.getVersions());
                String accent = "#FFFFFF";
                try {
                    accent = ColorExtractor.getDominantColor(item);
                } catch (Exception ignored) {}
                map.put("accentColor", accent);
                gamesList.add(map);
            }

            data.put("games", gamesList);

            StringWriter writer = new StringWriter();
            template.process(data, writer);

            return writer.toString();
        } catch (Exception e) {
            Logger.error(e.getMessage());
            return "<h1>Error</h1>";
        }
    }
}
