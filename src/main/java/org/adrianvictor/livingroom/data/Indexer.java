package org.adrianvictor.livingroom.data;

import org.adrianvictor.livingroom.Logger;
import org.adrianvictor.livingroom.data.catalog.Item;
import org.adrianvictor.livingroom.data.catalog.Property;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.xml.crypto.Data;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Indexer {
    private static Indexer instance;
    private Indexer() {};

    public static synchronized Indexer getInstance() {
        if (instance == null) {
            instance = new Indexer();
        }
        return instance;
    }

    public void scan(File directory) {;
        File[] files = directory.listFiles();

        for (File f : files) {
            if (f.isDirectory()) {
                File[] dir = f.listFiles();
                for (File file : dir) {
                    if (file.isFile() && file.toPath().toString().endsWith(".json")) {
                        Logger.info("Found json: %s".formatted(file.toPath().getFileName()));
                        try (FileReader fr = new FileReader(file)) {
                            Object obj = new JSONParser().parse(fr);

                            if (obj instanceof JSONObject) {
                                processObject((JSONObject) obj, file);
                            } else if (obj instanceof JSONArray) {
                                for (Object element : (JSONArray) obj) {
                                    if (element instanceof JSONObject) {
                                        processObject((JSONObject) element, file);
                                    }
                                }
                            }
                        } catch (FileNotFoundException e) {
                            Logger.error("File not found: %s".formatted(file.getPath()) + e);
                        } catch (IOException e) {
                            Logger.error("IO Error reading file: %s".formatted(file.getPath()) + e);
                        } catch (ParseException e) {
                            Logger.error("JSON Parsing error in file: %s".formatted(file.getPath()) + e);
                        }
                    }
                }
            }
        }
    }

    private void processObject(JSONObject jobj, File file) {
        HashMap<Property, String> properties = new HashMap<>();
        Logger.info("Adding %s to catalog.".formatted(file.getPath()));

        for (Object propEntry : jobj.entrySet()) {
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) propEntry;

            String value = String.valueOf(entry.getValue());

            try {
                Property prop = Property.valueOf(entry.getKey().toString().toUpperCase());
                properties.put(prop, value);
            } catch (IllegalArgumentException ignored) {
                Logger.warning("Unknown property: %s".formatted(entry.getKey()));
            }
        }

        if (!Database.getInstance().has(properties.get(Property.NAME))) {
            Database.getInstance().add(new Item(file, properties));
        }
    }

    public void collectGarbage() {
        List<Item> games = Database.getInstance().getAllGames();
        for (Item game : games) {
            if (!game.location().exists()) {
                Logger.warning("Game %s was caught by garbage collection because it's file does not exist anymore at %s."
                        .formatted(game.properties().get(Property.NAME), game.location().getPath())
                );
                Database.getInstance().remove(game.properties().get(Property.ID));
            }
        }
    }

    public void scanAsync(File directory) {
        Thread asyncThread = new Thread(() -> {
            scan(directory);
        });

        asyncThread.start();
    }
}
