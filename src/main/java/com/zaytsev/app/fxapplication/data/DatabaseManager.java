package com.zaytsev.app.fxapplication.data;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/sharapov";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "admin";
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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

    public boolean registerUser(String username, String password) {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        String query = "INSERT INTO users (name, password) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean checkUser(String username, String password) {
        String query = "SELECT password FROM users WHERE name = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String storedHashedPassword = rs.getString("password");
                return BCrypt.checkpw(password, storedHashedPassword);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public int getUserId(String username) {
        String query = "SELECT id FROM users WHERE name = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean saveUserStatistics(Integer users_id, String user_code, String generated_code,
                                      Float lead_time, Integer count_words, Float match_percentage,
                                      LocalDateTime date_of_completion) {
        String query = "INSERT INTO users_statistics (users_id, user_code, generated_code, lead_time, count_words, match_percentage, date_of_completion) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, users_id);
            stmt.setString(2, user_code);
            stmt.setString(3, generated_code);
            stmt.setFloat(4, lead_time);
            stmt.setInt(5, count_words);
            stmt.setFloat(6, match_percentage);
            // Измените следующую строку для соответствия с типом столбца date_of_completion
            stmt.setDate(7, java.sql.Date.valueOf(date_of_completion.toLocalDate()));
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}