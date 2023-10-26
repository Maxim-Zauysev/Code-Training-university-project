package com.zaytsev.app.fxapplication.data;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/sharapov";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "admin";

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public List<String> getLanguages() {
        List<String> languages = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT name FROM language");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                languages.add(resultSet.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return languages;
    }

    public String getRandomCode(String language) {
        String code = "";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT code FROM code WHERE language_id = (SELECT id FROM language WHERE name = ?) ORDER BY RANDOM() LIMIT 1")) {
            statement.setString(1, language);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                code = resultSet.getString("code");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return code;
    }
}