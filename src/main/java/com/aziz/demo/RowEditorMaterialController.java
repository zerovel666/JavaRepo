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

public class RowEditorMaterialController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button cancelButton;

    @FXML
    private TextField materialField;

    @FXML
    private Button saveButton;

    private MaterialMainController materialMainController;

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

    public void setMaterialMainController(MaterialMainController materialMainController) {
        this.materialMainController = materialMainController;
    }

    private void insert() {
        String query = "INSERT INTO material (name) VALUES (?)";
        try (Connection dbConnection = DbConnection.connect_db();
             PreparedStatement prepared = dbConnection.prepareStatement(query)) {

            String material = materialField.getText().trim();

            if (material.isEmpty()) {
                alertInfo("Поле 'name' не может быть пустым!");
                return;
            }

            prepared.setString(1, material);

            int rowsInserted = prepared.executeUpdate();
            id = null;

            if (rowsInserted > 0) {
                alertSuccess("Данные успешно добавлены!");
                materialMainController.loadTableDataFromDB();
                String finalQuery = String.format(
                        "INSERT INTO material (name) VALUES ('%s')",
                        material
                );

                DbConnection.logs(finalQuery);

                closeWindow();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            alertError("Ошибка базы данных.");
        }
    }


    private void update() {
        String query = "UPDATE material SET name = ? WHERE id = ?";

        try (Connection dbConnection = DbConnection.connect_db();
             PreparedStatement prepared = dbConnection.prepareStatement(query)) {

            String material = materialField.getText().trim();

            if (material.isEmpty()) {
                alertInfo("Поле 'material' не может быть пустым!");
                return;
            }

            prepared.setString(1, material);
            prepared.setInt(2, Integer.parseInt(id));

            int rowsUpdated = prepared.executeUpdate();
            id = null;

            if (rowsUpdated > 0) {
                alertSuccess("Данные успешно обновлены!");
                materialMainController.loadTableDataFromDB();
                String finalQuery = String.format(
                        "UPDATE material SET name = '%s' WHERE id = %s",
                        material, id
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
        Stage stage = (Stage) materialField.getScene().getWindow();
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

    public void setMaterialData(Material materialData) {
        if (materialData != null) {
            this.id = materialData.getID();
            materialField.setText(materialData.getName());
        }
    }

}
