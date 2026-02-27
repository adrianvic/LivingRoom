package org.adrianvictor.livingroom.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import org.adrianvictor.livingroom.http.Handler;
import org.adrianvictor.livingroom.http.HttpResponse;
import org.adrianvictor.livingroom.data.Database;
import org.adrianvictor.livingroom.data.catalog.Item;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;

public record CatalogHandler() implements Handler {
    @Override
    public HttpResponse result(String baseAddress, String path, HttpExchange exchange) {
        List<Item> rawCatalog = Database.getInstance().getAllGames();
        JSONArray jsonArray = new JSONArray();
        for (Item item : rawCatalog) {
            jsonArray.add(new JSONObject(item.getPropertiesString()));
        }
        String response = jsonArray.toJSONString();

        return HttpResponse.ok(response.getBytes(), "application/json");
    }
}
