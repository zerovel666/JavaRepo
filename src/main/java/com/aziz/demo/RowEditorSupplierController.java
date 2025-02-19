package com.aziz.demo;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RowEditorSupplierController {

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

    @FXML
    private TextField supplierField;

    private SupplierMainController supplierMainController;

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

    public void setSupplierMainController(SupplierMainController supplierMainController) {
        this.supplierMainController = supplierMainController;
    }

    private void insert() {
        String query = "INSERT INTO suppliers (supplier, country) VALUES (?, ?)";
        try (Connection dbConnection = DbConnection.connect_db();
             PreparedStatement prepared = dbConnection.prepareStatement(query)) {

            String supplier = supplierField.getText().trim();
            String country = countryField.getText().trim();

            if (supplier.isEmpty()) {
                alertInfo("Поле 'supplier' не может быть пустым!");
                return;
            }

            prepared.setString(1, supplier);
            prepared.setString(2, country);

            int rowsInserted = prepared.executeUpdate();
            id = null;

            if (rowsInserted > 0) {
                alertSuccess("Данные успешно добавлены!");
                supplierMainController.loadTableDataFromDB();

                String finalQuery = query.replaceFirst("\\?", "'" + supplier + "'")
                        .replaceFirst("\\?", "'" + country + "'");

                DbConnection.logs(finalQuery);
                closeWindow();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            alertError("Ошибка базы данных.");
        }
    }



    private void update() {
        String query = "UPDATE suppliers SET supplier = ?, country = ? WHERE id = ?";

        try (Connection dbConnection = DbConnection.connect_db();
             PreparedStatement prepared = dbConnection.prepareStatement(query)) {

            String supplier = supplierField.getText().trim();
            String country = countryField.getText().trim();

            if (supplier.isEmpty()) {
                alertInfo("Поле 'supplier' не может быть пустым!");
                return;
            }

            // Сохраняем ID перед очисткой
            String idCopy = id;
            int idValue = Integer.parseInt(idCopy);

            prepared.setString(1, supplier);
            prepared.setString(2, country);
            prepared.setInt(3, idValue);

            int rowsUpdated = prepared.executeUpdate();
            id = null; // Очищаем после успешного выполнения запроса

            if (rowsUpdated > 0) {
                alertSuccess("Данные успешно обновлены!");
                supplierMainController.loadTableDataFromDB();

                // Подставляем значения в логируемый запрос
                String finalQuery = query.replaceFirst("\\?", "'" + supplier + "'")
                        .replaceFirst("\\?", "'" + country + "'")
                        .replaceFirst("\\?", String.valueOf(idValue));

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
        Stage stage = (Stage) supplierField.getScene().getWindow();
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

    public void setSupplierData(Supplier supplierData) {
        if (supplierData != null) {
            this.id = supplierData.getID();
            supplierField.setText(supplierData.getSupplier());
            countryField.setText(supplierData.getCountry());
        }
    }

}
