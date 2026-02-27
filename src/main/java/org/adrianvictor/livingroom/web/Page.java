package org.adrianvictor.livingroom.web;

import com.sun.net.httpserver.HttpExchange;
import freemarker.template.Configuration;

import java.util.Map;

public interface Page {
    String result(Configuration cfg, String baseAddress, String path, Map<String, Object> data, HttpExchange exchange);
}