package com.aziz.demo;

import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DbConnection {

    private static final String URL = "jdbc:postgresql://localhost:5432/Java";
    private static final String USER = "postgres";
    private static final String PASSWORD = "1";

    public static Connection connect_db() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(URL,USER,PASSWORD);
            return connection;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void logs(String userQuery) {
        String query = "INSERT INTO logs (method, action, entry_id, date) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";

        try (Connection dbConnection = DbConnection.connect_db();
             PreparedStatement prepared = dbConnection.prepareStatement(query)) {

            String method = userQuery.split(" ")[0];
            String action = userQuery;
            Integer entry_id = extractEntryId(userQuery);

            prepared.setString(1, method);
            prepared.setString(2, action);

            if (entry_id != null) {
                prepared.setInt(3, entry_id);
            } else {
                prepared.setNull(3, Types.INTEGER);
            }

            prepared.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static Integer extractEntryId(String query) {
        Pattern pattern = Pattern.compile("\\b(?:id|entry_id)\\s*=\\s*(\\d+)");
        Matcher matcher = pattern.matcher(query);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }

        return null;
    }


}
