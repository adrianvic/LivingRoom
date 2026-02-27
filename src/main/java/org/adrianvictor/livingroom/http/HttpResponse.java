package org.adrianvictor.livingroom.http;

import java.util.HashMap;
import java.util.Map;

public record HttpResponse(
        int status,
        byte[] body,
        Map<String, String> headers
) {
    public static HttpResponse ok(byte[] body, String contentType, Map<String, String> headers) {
        return new HttpResponse(
                200,
                body,
                Map.of("Content-Type", contentType)
        );
    }

    public static HttpResponse ok(byte[] body, String contentType) {
        return ok(body, contentType, new HashMap<>());
    }

    public static HttpResponse redirect(String location) {
        return new HttpResponse(
                302,
                new byte[0],
                Map.of("Location", location)
        );
    }
}