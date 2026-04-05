package org.adrianvictor.livingroom.auth;

import org.adrianvictor.livingroom.config.AppConfig;
import org.adrianvictor.livingroom.utils.PasswordUtils;

public class User {
    private final String username;
    private final String hashedPassword;
    private final String salt;
    private Role role;

    public User(String username, String hashedPassword, String salt, Role role) {
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.salt = salt;
        this.role = role;
    }

    public boolean auth(String password) {
        return PasswordUtils.verifyPassword(password, salt, hashedPassword);
    }

    public AppConfig.UserConfig toConfig() {
        AppConfig.UserConfig config = new AppConfig.UserConfig();
        config.setRole(role.toString().toLowerCase());
        config.setHash(this.hashedPassword);
        config.setSalt(this.salt);
        return config;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getName() {
        return username;
    }

    public Role role() { return role; }
}
