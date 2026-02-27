package org.adrianvictor.livingroom.web.pages;

import com.sun.net.httpserver.HttpExchange;
import freemarker.template.Configuration;
import org.adrianvictor.livingroom.web.Page;

import java.util.Map;

public class NotFound implements Page {
    @Override
    public String result(Configuration cfg, String baseAddress, String path, Map<String, Object> data, HttpExchange exchange) {
        return "Not found";
    }
}
