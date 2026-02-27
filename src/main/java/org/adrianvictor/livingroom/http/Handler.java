package org.adrianvictor.livingroom.http;

import com.sun.net.httpserver.HttpExchange;

public interface Handler {
    HttpResponse result(String baseAddress, String path, HttpExchange exchange);
}
