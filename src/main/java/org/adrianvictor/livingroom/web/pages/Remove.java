package org.adrianvictor.livingroom.web.pages;

import com.sun.net.httpserver.HttpExchange;
import freemarker.template.Configuration;
import org.adrianvictor.livingroom.Main;
import org.adrianvictor.livingroom.data.Database;
import org.adrianvictor.livingroom.data.Indexer;
import org.adrianvictor.livingroom.web.Page;
import org.adrianvictor.livingroom.web.QuickResponses;

import java.io.File;
import java.util.Map;

public class Remove implements Page {

    @Override
    public String result(Configuration cfg, String baseAddress, String path, Map<String, Object> data, HttpExchange exchange) {
        String arg = path.split("/")[0];
        if (arg == null) {
            return QuickResponses.notFound();
        }
        int id;
        try {
            id = Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            return QuickResponses.notFound();
        }
        Database.getInstance().remove(String.valueOf(id));
        return "Success";
    }
}
