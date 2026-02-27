package org.adrianvictor.livingroom.data.catalog;

import org.adrianvictor.livingroom.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public record Item(File location, HashMap<Property, String> properties) {
    public HashMap<String, String> getPropertiesString() {
        HashMap<String, String> result = new HashMap<>();
        properties.forEach((key, value) -> {
            result.put(key.name().toLowerCase(), value);
        });
        return result;
    }

    public JSONObject getPropertiesJSON() {
        return new JSONObject(getPropertiesString());
    }

    public List<String> getVersions() {
        List<String> result = new ArrayList<>();

        if (getPropertiesString().get("files") == null) {
            return result;
        }

        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(getPropertiesString().get("files"));

            for (Object o : jsonObject.keySet()) {
                if (o instanceof String s) {
                    result.add(s);
                }
            }
        } catch (ParseException e) {
            Logger.error(e.getMessage());
        }

        return result;
    }

    public String getVersionFile(String version) {
        if (getPropertiesString().get("files") != null) {
            try {
                JSONParser parser = new JSONParser();
                JSONObject jsonObject = (JSONObject) parser.parse(getPropertiesString().get("files"));
                JSONObject versionObject = (JSONObject) jsonObject.get(version);
                return (String) versionObject.get("file");
            } catch (ParseException e) {
                Logger.error(e.getMessage());
            } catch (ClassCastException ignored) {}
        }

        return null;
    }
}