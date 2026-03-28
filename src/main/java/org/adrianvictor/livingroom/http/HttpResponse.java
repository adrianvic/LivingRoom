package org.adrianvictor.livingroom.http;

import org.json.simple.JSONObject;

import java.nio.charset.StandardCharsets;
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

    public static HttpResponse text(int status, String text) {
        return new HttpResponse(
                status,
                text.getBytes(),
                Map.of()
        );
    }

    public static HttpResponse json(int status, String text) {
        return new HttpResponse(
                status,
                text.getBytes(StandardCharsets.UTF_8),
                Map.of("Content-Type", "application/json")
        );
    }

    public static HttpResponse json(int status, Map<String, Object> extraFields) {
        JSONObject json = new JSONObject();
        json.putAll(extraFields);

        return new HttpResponse(
                status,
                json.toJSONString().getBytes(StandardCharsets.UTF_8),
                Map.of("Content-Type", "application/json")
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