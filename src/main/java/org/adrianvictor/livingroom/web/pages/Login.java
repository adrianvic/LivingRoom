package org.adrianvictor.livingroom.web.pages;

import com.sun.net.httpserver.HttpExchange;
import freemarker.template.Configuration;
import org.adrianvictor.livingroom.Logger;
import org.adrianvictor.livingroom.web.Page;

import java.io.StringWriter;
import java.util.Map;

public class Login implements Page {
    @Override
    public String result(Configuration cfg, String baseAddress, String path, Map<String, Object> data, HttpExchange exchange) {
        try {
            freemarker.template.Template template = cfg.getTemplate("login.ftl");

            StringWriter writer = new StringWriter();
            template.process(data, writer);

            return writer.toString();
        } catch (Exception e) {
            Logger.error(e.getMessage());
            return "<h1>Error</h1>";
        }
    }
}
