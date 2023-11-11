package com.zaytsev.app.fxapplication.data;
import java.sql.*;
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



    public boolean registerUser(String username, String password) {
        if(username.trim().isBlank() || password.trim().isBlank())
            return false;
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

    public int getCodeId(String code) {
        String query = "SELECT id FROM code WHERE code = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public List<String> getComplexityLevels() {
        List<String> complexities = new ArrayList<>();
        String query = "SELECT complexity FROM complexity;";
        try (Connection conn = this.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                complexities.add(rs.getString("complexity"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return complexities;
    }

    public String getCodeById(int id) {
        String query = "SELECT code FROM code WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("code");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<UserDto> getUsersByGeneratedCode(String code) {
        List<UserDto> users = new ArrayList<>();
        String query = "SELECT u.name, us.user_code, us.generated_code " +
                "FROM users_statistics us " +
                "JOIN users u ON us.users_id = u.id " +
                "WHERE us.generated_code = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                UserDto userDto = new UserDto();
                userDto.name = rs.getString("name");
                userDto.userCode = rs.getString("user_code");
                userDto.generatedCode = rs.getString("generated_code");
                users.add(userDto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public String getRandomCode(String language, String complexity) {
        String randomCode = "";
        String query = "SELECT c.code FROM code AS c " +
                "JOIN language AS l ON c.language_id = l.id " +
                "JOIN complexity AS comp ON c.complexity_id = comp.id " +
                "WHERE l.name = ? AND comp.complexity = ? " +
                "ORDER BY RANDOM() LIMIT 1;";

        try (Connection conn = this.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, language);
            pstmt.setString(2, complexity);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    randomCode = rs.getString("code");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return randomCode;
    }

    public String getComplexityNameByCodeId(int codeId) {
        String complexityName = "";
        String query = "SELECT comp.complexity FROM complexity AS comp " +
                "JOIN code AS c ON comp.id = c.complexity_id " +
                "WHERE c.id = ?;";

        try (Connection conn = this.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, codeId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    complexityName = rs.getString("complexity");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return complexityName;
    }

    public List<UserStatisticsDto> getUserStatistics(int userId) {
        List<UserStatisticsDto> statistics = new ArrayList<>();
        String query = "SELECT us.user_code, us.generated_code, us.date_of_completion, us.lead_time, us.count_words, us.match_percentage, comp.complexity " +
                "FROM users_statistics AS us " +
                "JOIN code AS c ON us.generated_code = c.code " +
                "JOIN complexity AS comp ON c.complexity_id = comp.id " +
                "WHERE us.users_id = ?;";

        try (Connection conn = this.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String userCode = rs.getString("user_code");
                    String generatedCode = rs.getString("generated_code");
                    LocalDateTime dateTesting = rs.getDate("date_of_completion").toLocalDate().atStartOfDay(); // Преобразование java.sql.Date в LocalDateTime
                    Float leadTime = rs.getFloat("lead_time");
                    Integer countWords = rs.getInt("count_words");
                    Float matchPercentage = rs.getFloat("match_percentage");
                    String codeComplexity = rs.getString("complexity"); // Получение сложности кода

                    statistics.add(new UserStatisticsDto(userCode, generatedCode, dateTesting, leadTime, countWords, matchPercentage, codeComplexity));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return statistics;
    }

    public int addLanguage(String languageName) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement("INSERT INTO language (name) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, languageName);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating language failed, no rows affected.");
            }
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating language failed, no ID obtained.");
                }
            }
        }
    }

    public void addCode(int languageId, int complexityId, String codeText) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement("INSERT INTO code (language_id, complexity_id, code) VALUES (?, ?, ?)")) {
            stmt.setInt(1, languageId);
            stmt.setInt(2, complexityId);
            stmt.setString(3, codeText);
            stmt.executeUpdate();
        }
    }

    public int getLanguageIdByName(String name) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement("SELECT id FROM language WHERE name = ?")) {
            stmt.setString(1, name);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                } else {
                    throw new SQLException("Language not found");
                }
            }
        }
    }

    public String getUserRoleById(int userId) throws SQLException {
        String query = "SELECT role FROM users WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("role");
                } else {
                    throw new SQLException("User not found");
                }
            }
        }
    }

}