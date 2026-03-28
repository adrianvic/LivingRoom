package org.adrianvictor.livingroom.data;

import org.adrianvictor.livingroom.Logger;
import org.adrianvictor.livingroom.Main;
import org.adrianvictor.livingroom.data.catalog.Item;
import org.adrianvictor.livingroom.data.catalog.Property;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database {
    static String url;
    static Database instance;

    private Database() {
        String library = Main.getConfig().getLibrary();
        File dbFile = new File(library, "livingroom.db");
        url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
        Logger.info("Loading DB at %s.".formatted(url));
        setup();
    }

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }

        return instance;
    }

    public void setup() {
        var sql = """
                CREATE TABLE IF NOT EXISTS games (
                    id INTEGER PRIMARY KEY,
                    name TEXT NOT NULL,
                    location TEXT NOT NULL,
                    properties TEXT NOT NULL
                )
                """;

        try (var conn = DriverManager.getConnection(url); var stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            Logger.error(e.getMessage());
        }
    }


    public void add(Item item) {
        String sql = "INSERT INTO games(name, location, properties) VALUES(?,?,?)";

        try (var conn = DriverManager.getConnection(url);
        var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, item.getPropertiesString().get("name"));
            pstmt.setString(2, item.location().toString());
            pstmt.setString(3, item.getPropertiesJSON().toJSONString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            Logger.error(e.getMessage());
        }
    }

    public void remove(String gameID) {
        String sql = "DELETE FROM games WHERE id=?";

        try (var conn = DriverManager.getConnection(url);
        var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, gameID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            Logger.error(e.getMessage());
        }
    }

    public void nuke() {
        String sql = "DROP TABLE IF EXISTS games;";

        try (var conn = DriverManager.getConnection(url);
        var stmt = conn.createStatement()) {
            stmt.execute(sql);
            setup();
        } catch (SQLException e) {
            Logger.error(e.getMessage());
        }
    }

    public boolean has(String name) {
        String sql = "SELECT id FROM games WHERE name=?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            Logger.error(e.getMessage());
            return false;
        }
    }

    public Item getGame(int id) {
        String sql = "SELECT * FROM games WHERE id=?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, String.valueOf(id));
            try (ResultSet rs = stmt.executeQuery()) {
                List<Item> processed = processResultSet(rs);
                if (processed.isEmpty()) {
                    return null;
                }

                return processed.getFirst();
            }
        } catch (SQLException e) {
            Logger.error(e.getMessage());
            return null;
        }
    }

    public List<Item> search(String term) {
        String sql = "SELECT * FROM games WHERE name LIKE ?";
        List<Item> result = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url);
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + term + "%"); // partial match

            try (ResultSet rs = stmt.executeQuery()) {
                result.addAll(processResultSet(rs));
            }

        } catch (SQLException e) {
            Logger.error(e.getMessage());
        }

        return result;
    }

    public List<Item> getAllGames() {
        String sql = "SELECT * FROM games";
        List<Item> result = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            result.addAll(processResultSet(rs));
        } catch (SQLException e) {
            Logger.error(e.getMessage());
        }

        return result;
    }

    private List<Item> processResultSet(ResultSet rs) throws SQLException {
        List<Item> result = new ArrayList<>();
        JSONParser parser = new JSONParser();
        while (rs.next()) {
            long id = rs.getLong("id");
            String name = rs.getString("name");
            String location = rs.getString("location");
            String json = rs.getString("properties");

            JSONObject props;
            try {
                props = (JSONObject) parser.parse(json);
            } catch (ParseException pe) {
                Logger.error("Failed to parse JSON for game id " + id + ": " + pe.getMessage());
                continue;
            }

            props.put("name", name);
            props.put("id", id);

            File file;

            try {
                file = new File(location);
            } catch (Exception e) {
                Logger.error("Cannot find game path for id " + id + ": " + e.getMessage());
                continue;
            }

            HashMap<Property, String> properties = new HashMap<>();

            for (Object propEntry : props.entrySet()) {
                Map.Entry<?, ?> entry = (Map.Entry<?, ?>) propEntry;

                String value = String.valueOf(entry.getValue());

                try {
                    Property prop = Property.valueOf(entry.getKey().toString().toUpperCase());
                    properties.put(prop, value);
                } catch (IllegalArgumentException ignored) {
                    Logger.warning("Unknown property: %s".formatted(entry.getKey()));
                }
            }

            Item item = new Item(file, properties);
            result.add(item);
        }
        return result;
    }
}
