package org.adrianvictor.livingroom.http;

import org.adrianvictor.livingroom.http.handlers.*;
import org.adrianvictor.livingroom.http.handlers.UserHandler;

import java.util.HashMap;

public class Handlers {
    private static final HashMap<Handler, String> map = new HashMap<>();

    static {
        map.put(new CatalogHandler(), "/catalog");
        map.put(new ImageHandler(), "/pic");
        map.put(new FileHandler(), "/download");
        map.put(new WebHandler(), "/web");
        map.put(new StaticWebHandler(), "/static");
        map.put(new WebRedirectHandler(), "/");
        map.put(new LoginHandler(), "/login");
        map.put(new SearchHandler(), "/search");
        map.put(new UserHandler(), "/user");
    }

    public static HashMap<Handler, String> getAll() {
        return map;
    }
}
