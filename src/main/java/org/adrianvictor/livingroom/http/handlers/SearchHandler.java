package org.adrianvictor.livingroom.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import org.adrianvictor.livingroom.data.Database;
import org.adrianvictor.livingroom.data.catalog.Item;
import org.adrianvictor.livingroom.http.Handler;
import org.adrianvictor.livingroom.http.HttpResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchHandler implements Handler {

    @Override
    public HttpResponse result(String baseAddress, String path, HttpExchange exchange) {
        String[] parts = path.split("/");

        if (parts.length < 1) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Invalid usage. Usage: ./query");
            return HttpResponse.json(400, map);
        }

        List<Item> games = Database.getInstance().search(parts[1]);
        JSONArray json = new JSONArray();
        for (Item g : games) {
            json.add(new JSONObject(g.getPropertiesString()));
        }
        Map<String, Object> extraFields = new HashMap<>();
        extraFields.put("result", json.toJSONString());
        extraFields.put("success", "true");
        return HttpResponse.json(200, extraFields);
    }
}
