package org.adrianvictor.livingroom.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import org.adrianvictor.livingroom.http.Handler;
import org.adrianvictor.livingroom.http.HttpResponse;

public class WebRedirectHandler implements Handler {

    @Override
    public HttpResponse result(String baseAddress, String path, HttpExchange exchange) {
        return HttpResponse.redirect("/web");
    }
}
