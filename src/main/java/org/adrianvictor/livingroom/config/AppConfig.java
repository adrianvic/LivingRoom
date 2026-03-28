package org.adrianvictor.livingroom.config;

import org.adrianvictor.livingroom.Logger;
import org.adrianvictor.livingroom.utils.PasswordUtils;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AppConfig {
    private String library;
    private int port;
    private Map<String, UserConfig> users;
    private final String path;

    public AppConfig(String path) {
        this.path = path;
        this.users = new HashMap<>();
    }

    public String getLibrary() { return library; }
    public void setLibrary(String library) { this.library = library; }

    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }

    public Map<String, UserConfig> getUsers() { return users; }
    public void setUsers(Map<String, UserConfig> users) {
        this.users = (users != null) ? users : new HashMap<>();
    }
    public void addUser(String name, UserConfig config) { this.users.put(name, config); }

    public void hashPlaintextPasswords() {
        if (users == null) return;
        for (Map.Entry<String, UserConfig> entry : users.entrySet()) {
            Logger.info("Hashing user password for %s.".formatted(entry.getKey()));
            UserConfig user = entry.getValue();
            if (user.getHash() == null && user.getPassword() != null) {
                String salt = PasswordUtils.generateSalt();
                String hash = PasswordUtils.hashPassword(user.getPassword(), salt);

                user.setSalt(salt);
                user.setHash(hash);
                user.setPassword(null);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void saveConfig() throws IOException {
        JSONObject json = new JSONObject();
        json.put("library", getLibrary());
        json.put("port", getPort());

        JSONObject usersJson = new JSONObject();
        if (users != null) {
            for (Map.Entry<String, UserConfig> entry : users.entrySet()) {
                String username = entry.getKey();
                UserConfig user = entry.getValue();

                JSONObject userJson = new JSONObject();
                userJson.put("role", user.getRole());
                userJson.put("salt", user.getSalt());
                userJson.put("hash", user.getHash());
                usersJson.put(username, userJson);
            }
        }
        json.put("users", usersJson);

        try (FileWriter writer = new FileWriter(path)) {
            writer.write(json.toJSONString());
        }
    }

    public static class UserConfig {
        private String role;
        private String password;
        private String salt;
        private String hash;

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }

        public String getSalt() { return salt; }
        public void setSalt(String salt) { this.salt = salt; }

        public String getHash() { return hash; }
        public void setHash(String hash) { this.hash = hash; }
    }
}