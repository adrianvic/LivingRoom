package org.adrianvictor.livingroom.auth;

import org.adrianvictor.livingroom.utils.PasswordUtils;

public record User(String username, String hashedPassword, String salt, Role role) {
    public boolean auth(String password) {
        return PasswordUtils.verifyPassword(password, salt, hashedPassword);
    }
}
