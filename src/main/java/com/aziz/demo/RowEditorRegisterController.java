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

public class RowEditorRegisterController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField articleField;

    @FXML
    private Button cancelButton;

    @FXML
    private TextField materialField;

    @FXML
    private TextField nameField;

    @FXML
    private TextField salesField;

    @FXML
    private Button saveButton;

    @FXML
    private TextField supplierField;

    private RegisterMainController registerMainController;

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

    public void setRegisterMainController(RegisterMainController registerMainController) {
        this.registerMainController = registerMainController;
    }

    private void insert() {
        String query = "INSERT INTO register (name, article, supplier, material, sales) VALUES (?, ?, ?, ?, ?)";
        try (Connection dbConnection = DbConnection.connect_db();
             PreparedStatement prepared = dbConnection.prepareStatement(query)) {

            String name = nameField.getText().trim();
            Long article = Long.valueOf(articleField.getText().trim());
            Integer supplier = Integer.valueOf(supplierField.getText().trim());
            Integer material = Integer.valueOf(materialField.getText().trim());
            Integer sales = Integer.valueOf(salesField.getText().trim());

            if (name.isEmpty()) {
                alertInfo("Поле 'name' не может быть пустым!");
                return;
            }

            prepared.setString(1, name);
            prepared.setLong(2, article);
            prepared.setInt(3, supplier);
            prepared.setInt(4, material);
            prepared.setInt(5, sales);

            int rowsInserted = prepared.executeUpdate();

            if (rowsInserted > 0) {
                alertSuccess("Данные успешно добавлены!");
                registerMainController.loadTableDataFromDB();
                closeWindow();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            alertError("Ошибка базы данных.");
        }
    }

    private void update() {
        String query = "UPDATE register SET name = ?, article = ?, supplier = ?, material = ?, sales = ? WHERE id = ?";

        try (Connection dbConnection = DbConnection.connect_db();
             PreparedStatement prepared = dbConnection.prepareStatement(query)) {

            String name = nameField.getText().trim();
            Long article = Long.valueOf(articleField.getText().trim());
            Integer supplier = Integer.valueOf(supplierField.getText().trim());
            Integer material = Integer.valueOf(materialField.getText().trim());
            Integer sales = Integer.valueOf(salesField.getText().trim());

            if (name.isEmpty()) {
                alertInfo("Поле 'name' не может быть пустым!");
                return;
            }

            prepared.setString(1, name);
            prepared.setLong(2, article);
            prepared.setInt(3, supplier);
            prepared.setInt(4, material);
            prepared.setInt(5, sales);
            prepared.setInt(6, Integer.parseInt(id));

            int rowsUpdated = prepared.executeUpdate();

            if (rowsUpdated > 0) {
                alertSuccess("Данные успешно обновлены!");
                registerMainController.loadTableDataFromDB();
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

    public void setRegisterData(Register registerData) {
        if (registerData != null) {
            this.id = registerData.getID();
            nameField.setText(registerData.getName());
            articleField.setText(String.valueOf(registerData.getArticle()));
            materialField.setText(String.valueOf(registerData.getMaterial()));
            salesField.setText(String.valueOf(registerData.getSales()));
            supplierField.setText(String.valueOf(registerData.getSupplier()));
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
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
}
