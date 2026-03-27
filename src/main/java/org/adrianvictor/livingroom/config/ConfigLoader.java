package org.adrianvictor.livingroom.config;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

public class ConfigLoader {

    private static AppConfig config;

    @SuppressWarnings("unchecked")
    public static AppConfig load(String path) throws Exception {
        if (config != null) return config;

        File file = new File(path);
        AppConfig appConfig;

        if (!file.exists()) {
            appConfig = createDefaultConfig(path);

            saveConfig(appConfig, path);
        } else {
            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(new FileReader(path));

            appConfig = new AppConfig(path);
            appConfig.setLibrary((String) obj.get("library"));

            // port may be a long in org.json.simple
            Object portObj = obj.get("port");
            if (portObj instanceof Long) {
                appConfig.setPort(((Long) portObj).intValue());
            } else if (portObj instanceof String) {
                appConfig.setPort(Integer.parseInt((String) portObj));
            } else {
                throw new IllegalStateException("Invalid port value in config");
            }

            // users
            JSONObject usersObj = (JSONObject) obj.get("users");
            Map<String, AppConfig.UserConfig> users = new HashMap<>();
            for (Object key : usersObj.keySet()) {
                String username = (String) key;
                JSONObject userJson = (JSONObject) usersObj.get(username);

                AppConfig.UserConfig user = new AppConfig.UserConfig();
                user.setRole((String) userJson.get("role"));
                user.setPassword((String) userJson.get("password"));

                users.put(username, user);
            }
            appConfig.setUsers(users);
        }

        validate(appConfig);

        config = appConfig;
        return config;
    }

    private static AppConfig createDefaultConfig(String path) {
        AppConfig defaultConfig = new AppConfig(path);
        defaultConfig.setLibrary("/srv/games");
        defaultConfig.setPort(8080);

        Map<String, AppConfig.UserConfig> users = new HashMap<>();
        AppConfig.UserConfig admin = new AppConfig.UserConfig();
        admin.setRole("admin");
        admin.setPassword("admin");
        users.put("admin", admin);

        defaultConfig.setUsers(users);
        return defaultConfig;
    }

    @SuppressWarnings("unchecked")
    private static void saveConfig(AppConfig appConfig, String path) throws Exception {
        JSONObject obj = new JSONObject();
        obj.put("library", appConfig.getLibrary());
        obj.put("port", appConfig.getPort());

        JSONObject usersObj = new JSONObject();
        for (Map.Entry<String, AppConfig.UserConfig> entry : appConfig.getUsers().entrySet()) {
            JSONObject userJson = new JSONObject();
            userJson.put("role", entry.getValue().getRole());
            userJson.put("password", entry.getValue().getPassword());
            usersObj.put(entry.getKey(), userJson);
        }
        obj.put("users", usersObj);

        try (FileWriter writer = new FileWriter(path)) {
            writer.write(obj.toJSONString());
        }
    }

    private static void validate(AppConfig appConfig) {
        if (appConfig.getLibrary() == null || appConfig.getLibrary().isEmpty())
            throw new IllegalStateException("Library path must be set");
        if (appConfig.getPort() <= 0)
            throw new IllegalStateException("Port must be > 0");
        if (appConfig.getUsers() == null || appConfig.getUsers().isEmpty())
            throw new IllegalStateException("At least one user must be defined");
    }

    public static AppConfig get() {
        if (config == null) throw new IllegalStateException("Config not loaded yet");
        return config;
    }
}