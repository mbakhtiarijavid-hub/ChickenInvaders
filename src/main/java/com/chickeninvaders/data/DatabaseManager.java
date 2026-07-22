package com.chickeninvaders.data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:game.db";
    private Connection connection;

    public DatabaseManager() {
        connect();
        createTables();
    }

    private void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException ignored) {
        }
        try {
            connection = DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            System.err.println("Failed to connect to database: " + e.getMessage());
        }
    }

    private void createTables() {
        String usersTable =
                "CREATE TABLE IF NOT EXISTS users (" +
                "username TEXT PRIMARY KEY, " +
                "password TEXT NOT NULL, " +
                "high_score INTEGER NOT NULL DEFAULT 0, " +
                "last_level INTEGER NOT NULL DEFAULT 1, " +
                "music_on INTEGER NOT NULL DEFAULT 1, " +
                "shot_on INTEGER NOT NULL DEFAULT 1, " +
                "crash_on INTEGER NOT NULL DEFAULT 1, " +
                "end_on INTEGER NOT NULL DEFAULT 1, " +
                "selected_plane TEXT NOT NULL DEFAULT 'Default')";

        String historyTable =
                "CREATE TABLE IF NOT EXISTS game_history (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL, " +
                "score INTEGER NOT NULL, " +
                "last_level INTEGER NOT NULL, " +
                "played_at INTEGER NOT NULL, " +
                "music_on INTEGER NOT NULL, " +
                "shot_on INTEGER NOT NULL, " +
                "crash_on INTEGER NOT NULL, " +
                "end_on INTEGER NOT NULL, " +
                "FOREIGN KEY (username) REFERENCES users(username))";

        try (Statement st = connection.createStatement()) {
            st.execute(usersTable);
            st.execute(historyTable);
        } catch (SQLException e) {
            System.err.println("Failed to create tables: " + e.getMessage());
        }
    }

    private User mapUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getString("username"),
                rs.getString("password"),
                rs.getInt("high_score"),
                rs.getInt("last_level"),
                rs.getInt("music_on") == 1,
                rs.getInt("shot_on") == 1,
                rs.getInt("crash_on") == 1,
                rs.getInt("end_on") == 1,
                rs.getString("selected_plane"));
    }

    public synchronized boolean usernameExists(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("usernameExists failed: " + e.getMessage());
            return false;
        }
    }

    public synchronized boolean registerUser(String username, String password) {
        if (username == null || username.trim().isEmpty() || usernameExists(username)) return false;
        String sql = "INSERT INTO users (username, password, high_score, last_level, music_on, shot_on, crash_on, end_on, selected_plane) " +
                "VALUES (?, ?, 0, 1, 1, 1, 1, 1, 'Default')";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("registerUser failed: " + e.getMessage());
            return false;
        }
    }

    public synchronized User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("login failed: " + e.getMessage());
        }
        return null;
    }

    public synchronized void updateUser(User u) {
        String sql = "UPDATE users SET high_score = ?, last_level = ?, music_on = ?, shot_on = ?, crash_on = ?, end_on = ?, selected_plane = ? " +
                "WHERE username = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, u.getHighScore());
            ps.setInt(2, u.getLastLevel());
            ps.setInt(3, u.isMusicOn() ? 1 : 0);
            ps.setInt(4, u.isShotSoundOn() ? 1 : 0);
            ps.setInt(5, u.isCrashSoundOn() ? 1 : 0);
            ps.setInt(6, u.isEndSoundOn() ? 1 : 0);
            ps.setString(7, u.getSelectedPlane());
            ps.setString(8, u.getUsername());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("updateUser failed: " + e.getMessage());
        }
    }

    public synchronized void recordGame(String username, int score, int lastLevel,
                                         boolean music, boolean shot, boolean crash, boolean end) {
        String insertHistory = "INSERT INTO game_history (username, score, last_level, played_at, music_on, shot_on, crash_on, end_on) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(insertHistory)) {
            ps.setString(1, username);
            ps.setInt(2, score);
            ps.setInt(3, lastLevel);
            ps.setLong(4, System.currentTimeMillis());
            ps.setInt(5, music ? 1 : 0);
            ps.setInt(6, shot ? 1 : 0);
            ps.setInt(7, crash ? 1 : 0);
            ps.setInt(8, end ? 1 : 0);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("recordGame insert failed: " + e.getMessage());
        }

        String updateLevel = "UPDATE users SET last_level = ? WHERE username = ?";
        try (PreparedStatement ps = connection.prepareStatement(updateLevel)) {
            ps.setInt(1, lastLevel);
            ps.setString(2, username);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("recordGame level update failed: " + e.getMessage());
        }

        String updateBestScore = "UPDATE users SET high_score = ? WHERE username = ? AND ? > high_score";
        try (PreparedStatement ps = connection.prepareStatement(updateBestScore)) {
            ps.setInt(1, score);
            ps.setString(2, username);
            ps.setInt(3, score);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("recordGame high-score update failed: " + e.getMessage());
        }
    }

    public synchronized List<String[]> getHighScores() {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT username, score, last_level, played_at FROM (" +
                "SELECT username, score, last_level, played_at, " +
                "ROW_NUMBER() OVER (PARTITION BY username ORDER BY score DESC, played_at DESC) AS rn " +
                "FROM game_history) WHERE rn = 1 ORDER BY score DESC";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String username = rs.getString("username");
                int score = rs.getInt("score");
                int level = rs.getInt("last_level");
                long ts = rs.getLong("played_at");
                String date = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date(ts));
                list.add(new String[]{username, String.valueOf(score), String.valueOf(level), date});
            }
        } catch (SQLException e) {
            System.err.println("getHighScores failed: " + e.getMessage());
        }
        return list;
    }
}
