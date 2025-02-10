package com.aziz.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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

    public PreparedStatement prepareStatement(Connection connection, String query) throws SQLException {
        return connection.prepareStatement(query);
    }
}
