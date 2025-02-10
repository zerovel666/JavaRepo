package com.aziz.demo;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CountryCrudController {
    @FXML
    private TextField countryInput;

    public void store(String requestCountry) throws SQLException {
        String query = "INSERT INTO country (country) VALUES (?)";
        Connection dbConnection = DbConnection.connect_db();
        try {
            assert dbConnection != null;
            PreparedStatement prepared = dbConnection.prepareStatement(query);
            prepared.setString(1,requestCountry);
            prepared.execute();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void destroy(String requestCountry) throws SQLException {
        String query = "DELETE FROM country WHERE (country) = (?)";
        Connection dbConnection = DbConnection.connect_db();
        try{
            assert  dbConnection !=null;
            PreparedStatement prepared = dbConnection.prepareStatement(query);
            prepared.setString(1,requestCountry);
            prepared.execute();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    public void addCountry() {
        String requestCountry = countryInput.getText();
        try {
            store(requestCountry);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
