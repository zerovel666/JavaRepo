package com.aziz.demo;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.*;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class OperatorMainController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button countryButton;

    @FXML
    private Button createButton;

    @FXML
    private Button deleteRowButton;

    @FXML
    private MenuItem exitAction;

    @FXML
    private MenuButton menuButton;

    @FXML
    private Button registerButton;

    @FXML
    private SplitMenuButton sortButton;

    @FXML
    private Button staffButton;

    @FXML
    private Button suppliersButton;

    @FXML
    private Button updateRowButton;

    @FXML
    private TableView<Map<String, Object>> operatorTable;

    @FXML
    private Button materialButton;

    @FXML
    void initialize() {
        exitAction.setOnAction(event -> {
            loaderPage("/com/aziz/demo/SignUp.fxml");
        });
        suppliersButton.setOnAction(event -> {
            clearTableColumns();
            createColumn("Suppliers");
        });
        registerButton.setOnAction(event ->{
            clearTableColumns();
            createColumn("Register");
        });
        staffButton.setOnAction(event -> {
            clearTableColumns();
            createColumn("Staff");
        });
        materialButton.setOnAction(event -> {
            clearTableColumns();
            createColumn("Material");
        });
        countryButton.setOnAction(event -> {
            clearTableColumns();
            createColumn("Country");
        });
    }

    private void createColumn(String model) {
        List<String> columnNames = getDynamicColumnNames(model);

        for (String columnName : columnNames) {
            TableColumn<Map<String, Object>, String> column = new TableColumn<>(columnName);
            column.setCellValueFactory(param -> new SimpleStringProperty(
                    param.getValue().getOrDefault(columnName, "").toString()));
            operatorTable.getColumns().add(column);
        }

        loadTableData(model);
    }

    private void loadTableData(String model) {
        operatorTable.getItems().clear();
        String tableName = getTableNameFromModel(model);

        String query = "SELECT * FROM " + tableName;

        try (Connection dbConnection = DbConnection.connect_db();
             PreparedStatement prepared = dbConnection.prepareStatement(query);
             ResultSet resultSet = prepared.executeQuery()) {

            List<Map<String, Object>> rows = new ArrayList<>();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (resultSet.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), resultSet.getObject(i));
                }
                rows.add(row);
            }

            operatorTable.getItems().addAll(rows);

        } catch (SQLException e) {
            e.printStackTrace();
            alertError("Ошибка загрузки данных из базы данных");
        }
    }


    private void clearTableColumns() {
        operatorTable.getColumns().clear();
    }

    private List<String> getDynamicColumnNames(String model) {
        String tableName = getTableNameFromModel(model);
        List<String> columnNames = new ArrayList<>();

        String query = "SELECT * FROM " + tableName + " LIMIT 1";

        try (Connection dbConnection = DbConnection.connect_db();
             PreparedStatement prepared = dbConnection.prepareStatement(query);
             ResultSet resultSet = prepared.executeQuery()) {  // Удалили передачу query

            ResultSetMetaData metaData = resultSet.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                columnNames.add(metaData.getColumnName(i));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            alertError("Ошибка подключения к базе данных");
        }

        return columnNames;
    }

    private String getTableNameFromModel(String model) {
        return switch (model) {
            case "Register" -> "register";
            case "Material" -> "material";
            case "Staff" -> "staff";
            case "Suppliers" -> "suppliers";
            case "Country" -> "country";
            default -> throw new IllegalArgumentException("Неизвестная модель: " + model);
        };
    }

    private void alertError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loaderPage(String path) {
        try {
            Stage stage = (Stage) sortButton.getScene().getWindow();
            stage.close();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Parent root = loader.load();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            alertError("Ошибка загрузки страницы");
        }
    }

}
