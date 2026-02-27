package org.adrianvictor.livingroom.web.pages;

import com.sun.net.httpserver.HttpExchange;
import freemarker.template.Configuration;
import org.adrianvictor.livingroom.Main;
import org.adrianvictor.livingroom.data.Indexer;
import org.adrianvictor.livingroom.web.Page;

import java.io.File;
import java.util.Map;

public class Scan implements Page {

    @Override
    public String result(Configuration cfg, String baseAddress, String path, Map<String, Object> data, HttpExchange exchange) {
        Indexer.getInstance().scanAsync(new File(Main.getConfig().getLibrary()));
        return "";
    }
}
