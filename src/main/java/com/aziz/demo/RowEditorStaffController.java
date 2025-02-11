package com.aziz.demo;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RowEditorStaffController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button cancelButton;

    @FXML
    private TextField dateField;

    @FXML
    private TextField fullNameField;

    @FXML
    private Button saveButton;

    @FXML
    private StaffMainController staffMainController;

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

    public void setStaffMainController(StaffMainController staffMainController) {
        this.staffMainController = staffMainController;
    }

    private void insert() {
        String query = "INSERT INTO staff (full_name, date_employement) VALUES (?, ?)";
        try (Connection dbConnection = DbConnection.connect_db();
             PreparedStatement prepared = dbConnection.prepareStatement(query)) {

            String full_name = fullNameField.getText().trim();
            String dateString = dateField.getText().trim();

            if (full_name.isEmpty()) {
                alertInfo("Поле 'full_name' не может быть пустым!");
                return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  // Формат даты, который используете в базе
            java.sql.Date date_employement = null;
            try {
                date_employement = new java.sql.Date(sdf.parse(dateString).getTime());
            } catch (Exception e) {
                alertError("Неверный формат даты!");
                return;
            }

            prepared.setString(1, full_name);
            prepared.setDate(2, date_employement);

            int rowsInserted = prepared.executeUpdate();
            id = null;

            if (rowsInserted > 0) {
                alertSuccess("Данные успешно добавлены!");
                staffMainController.loadTableDataFromDB();
                closeWindow();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            alertError("Ошибка базы данных.");
        }
    }


    private void update() {
        String query = "UPDATE staff SET full_name = ?, date_employement = ? WHERE id = ?";

        try (Connection dbConnection = DbConnection.connect_db();
             PreparedStatement prepared = dbConnection.prepareStatement(query)) {

            String full_name = fullNameField.getText().trim();
            String dateString = dateField.getText().trim();

            if (full_name.isEmpty()) {
                alertInfo("Поле 'full_name' не может быть пустым!");
                return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  // Формат даты
            java.sql.Date date_employement = null;
            try {
                date_employement = new java.sql.Date(sdf.parse(dateString).getTime());
            } catch (Exception e) {
                alertError("Неверный формат даты!");
                return;
            }

            prepared.setString(1, full_name);
            prepared.setDate(2, date_employement);
            prepared.setInt(3, Integer.parseInt(id));

            int rowsUpdated = prepared.executeUpdate();
            id = null;

            if (rowsUpdated > 0) {
                alertSuccess("Данные успешно обновлены!");
                staffMainController.loadTableDataFromDB();
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
        Stage stage = (Stage) fullNameField.getScene().getWindow();
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

    public void setStaffData(Staff staffData) {
        if (staffData != null) {
            this.id = staffData.getID();
            fullNameField.setText(staffData.getFull_name());
            dateField.setText(String.valueOf(staffData.getDateEmploymentFormatted()));
        }
    }
}
