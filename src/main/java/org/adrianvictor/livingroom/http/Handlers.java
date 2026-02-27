package org.adrianvictor.livingroom.http;

import org.adrianvictor.livingroom.http.handlers.*;

import java.util.HashMap;

public class Handlers {
    private static HashMap<Handler, String> map = new HashMap<>();

    static {
        map.put(new CatalogHandler(), "/catalog");
        map.put(new ImageHandler(), "/pic");
        map.put(new FileHandler(), "/download");
        map.put(new WebHandler(), "/web");
        map.put(new StaticWebHandler(), "/static");
    }

    public static HashMap<Handler, String> getAll() {
        return map;
    }
}
