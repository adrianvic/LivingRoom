package org.adrianvictor.livingroom;

import org.adrianvictor.livingroom.config.AppConfig;
import org.adrianvictor.livingroom.config.ConfigLoader;
import org.adrianvictor.livingroom.http.Handler;
import org.adrianvictor.livingroom.http.Handlers;
import org.adrianvictor.livingroom.http.Server;
import org.adrianvictor.livingroom.data.Indexer;

import java.io.File;
import java.util.Map;

public class Main {
    private static String directoryPath;
    private static final Indexer indexer = Indexer.getInstance();
    private static AppConfig config;
    private static String configPath;

    public static void main(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if ("--config".equals(args[i]) && i + 1 < args.length) {
                configPath = args[i + 1];
            }
        }

        try {
            config = ConfigLoader.load("config.json");
            config.hashPlaintextPasswords();
        } catch (Exception e) {
            Logger.error("Error loading config: " + e.getMessage());
            System.exit(1);
        }

        directoryPath = config.getLibrary();
        Server server = Server.getInstance();
        indexer.scanAsync(new File(directoryPath));
        indexer.collectGarbage();

        for (Map.Entry<Handler, String> e : Handlers.getAll().entrySet()) {
            server.registerHandler(e.getKey(), e.getValue());
        }

        server.start();
    }

    public static AppConfig getConfig() {
        return config;
    }
}