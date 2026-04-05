package org.adrianvictor.livingroom.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import org.adrianvictor.livingroom.Main;
import org.adrianvictor.livingroom.auth.Role;
import org.adrianvictor.livingroom.auth.Session;
import org.adrianvictor.livingroom.auth.User;
import org.adrianvictor.livingroom.http.Handler;
import org.adrianvictor.livingroom.http.HttpResponse;
import org.adrianvictor.livingroom.http.utils.AuthenticationHelper;
import org.adrianvictor.livingroom.services.UserService;

public class UserHandler implements Handler {
    @Override
    public HttpResponse result(String baseAddress, String path, HttpExchange exchange) {
        String[] parts = path.split("/");
        Session session = AuthenticationHelper.getAuthenticatedSession(exchange);
        String username = session.getUsername();

        if (parts.length < 2) {
            return HttpResponse.json(400, "Usage: '.../user/<promote|demote>/<username>'");
        }
        if (UserService.getInstance().getUser(username).role() != Role.ADMIN) {
            return HttpResponse.json(401, "You are not an administrator.");
        }

        User user = UserService.getInstance().getUser(parts[1]);
        if (user == null) {
            return HttpResponse.json(401, "This user does not exist.");
        }

        try {
            switch (parts[0]) {
                case "promote":
                    user.setRole(Role.ADMIN);
                    break;
                case "demote":
                    user.setRole(Role.USER);
                    break;
                default:
                    return HttpResponse.json(400, "Usage: '.../user/<promote|demote>/<username>'");
            }

            Main.getConfig().addUser(user.getName(), user.toConfig());
            Main.getConfig().saveConfig();

            return HttpResponse.json(200, "Success.");
        } catch (Exception e) {
            return HttpResponse.json(500, "");
        }
    }
}
