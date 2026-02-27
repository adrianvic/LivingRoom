package org.adrianvictor.livingroom.web;

import org.adrianvictor.livingroom.web.pages.*;

import java.util.HashMap;

public class Pages {
    private static HashMap<String, Page> map = new HashMap<>();

    static {
        Index index = new Index();
        map.put("", index);
        map.put("game", new Game());
        map.put("logout", new Logout());
        map.put("scan", new Scan());
    }

    public static HashMap<String, Page> getAll() {
        return map;
    }
    public static Page get(String path) { return map.get(path); }
}
