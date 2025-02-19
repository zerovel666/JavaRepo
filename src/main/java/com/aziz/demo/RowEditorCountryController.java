package com.aziz.demo;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RowEditorCountryController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button cancelButton;

    @FXML
    private TextField countryField;

    @FXML
    private Button saveButton;

    private CountryMainController countryMainController;

    public static String id;

    @FXML
    void initialize() {
        saveButton.setOnAction(event -> saveData());
    }

    private void saveData() {
        if (id != null) {
            update();
        } else {
            insert();
        }
    }

    public void setCountryMainController(CountryMainController countryMainController) {
        this.countryMainController = countryMainController;
    }

    private void insert() {
        String query = "INSERT INTO country (country) VALUES (?)";
        try (Connection dbConnection = DbConnection.connect_db();
             PreparedStatement prepared = dbConnection.prepareStatement(query)) {

            String country = countryField.getText().trim();

            if (country.isEmpty()) {
                alertInfo("Поле 'country' не может быть пустым!");
                return;
            }

            prepared.setString(1, country);

            int rowsInserted = prepared.executeUpdate();
            id = null;

            if (rowsInserted > 0) {
                alertSuccess("Данные успешно добавлены!");
                String finalQuery = String.format(
                        "INSERT INTO country (country) VALUES ('%s')",
                        country
                );

                DbConnection.logs(finalQuery);
                countryMainController.loadTableDataFromDB();
                closeWindow();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            alertError("Ошибка базы данных.");
        }
    }


    private void update() {
        String query = "UPDATE country SET country = ? WHERE id = ?";

        try (Connection dbConnection = DbConnection.connect_db();
             PreparedStatement prepared = dbConnection.prepareStatement(query)) {

            String country = countryField.getText().trim();

            if (country.isEmpty()) {
                alertInfo("Поле 'country' не может быть пустым!");
                return;
            }

            prepared.setString(1, country);
            prepared.setInt(2, Integer.parseInt(id));

            int rowsUpdated = prepared.executeUpdate();
            id = null;

            if (rowsUpdated > 0) {
                alertSuccess("Данные успешно обновлены!");
                countryMainController.loadTableDataFromDB();
                String finalQuery = String.format(
                        "UPDATE country SET country = '%s' WHERE id = %d",
                        country, Integer.parseInt(id)
                );

                DbConnection.logs(finalQuery);

                closeWindow();
            } else {
                alertError("Не удалось обновить данные.");
            }
        } catch (NumberFormatException e) {
            alertError("Некорректный формат числовых данных.");
        } catch (SQLException e) {
            e.printStackTrace();
            alertError("Ошибка базы данных.");
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) countryField.getScene().getWindow();
        stage.close();
    }

    private void alertInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Информация");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void alertError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void alertSuccess(String message) {
        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setTitle("Успех");
        successAlert.setHeaderText(null);
        successAlert.setContentText(message);
        successAlert.showAndWait();
    }

    public void setCountryData(Country countryData) {
        if (countryData != null) {
            this.id = countryData.getID();
            countryField.setText(countryData.getCountry());
        }
    }

}
