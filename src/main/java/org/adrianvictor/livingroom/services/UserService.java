package org.adrianvictor.livingroom.services;

import org.adrianvictor.livingroom.Main;
import org.adrianvictor.livingroom.auth.Role;
import org.adrianvictor.livingroom.auth.User;
import org.adrianvictor.livingroom.config.AppConfig;

import java.util.Map;

public class UserService {
    private static UserService instance;
    private static Map<String, AppConfig.UserConfig> users;

    private UserService() {
        users = Main.getConfig().getUsers();
    }

    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    public User getUser(String user) {
        AppConfig.UserConfig raw = users.get(user);

        if (user == null) {
            return null;
        }

        Role role;

        try {
            role = Role.valueOf(raw.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            role = Role.USER;
        }

        return new User(
                user,
                raw.getHash(),
                raw.getSalt(),
                role
        );
    }
}
