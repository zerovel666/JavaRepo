package com.aziz.demo;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LogsMainController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TableColumn<Logs, String> actionColumn;

    @FXML
    private TableColumn<Logs, String> dateColumn;

    @FXML
    private TableColumn<Logs, Integer> entryIdColumn;

    @FXML
    private MenuItem exitAction;

    @FXML
    private TableColumn<Logs, String> idColumn;

    @FXML
    private Button logsButton;

    @FXML
    private MenuButton menuButton;

    @FXML
    private TableColumn<Logs, String> methodColumn;

    @FXML
    private MenuItem renderOperationAction;

    @FXML
    private MenuItem renderUserAction;

    @FXML
    private SplitMenuButton sortButton;

    @FXML
    private Button usersButton;

    @FXML
    private TableView<Logs> logsTable;

    @FXML
    void initialize() {
        setupTableColumns();
        loadTableDataFromDB();
        usersButton.setOnAction(event -> loaderPage("/com/aziz/demo/AdminMain.fxml"));
        renderOperationAction.setOnAction(event -> loaderPage("/com/aziz/demo/OperatorMain.fxml"));

    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId()));
        methodColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMethod()));
        actionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAction()));
        entryIdColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getEntryId()).asObject());
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDateFormated()));
    }

    public void loadTableDataFromDB() {
        ObservableList<Logs> data = FXCollections.observableArrayList();

        String query = "SELECT id, method, action, entry_id, date FROM logs"; // Используй правильную таблицу

        try (Connection dbConnection = DbConnection.connect_db();
             PreparedStatement prepared = dbConnection.prepareStatement(query);
             ResultSet resultSet = prepared.executeQuery()) {

            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String method = resultSet.getString("method");
                String action = resultSet.getString("action");
                int entryId = resultSet.getInt("entry_id");
                Date date = resultSet.getTimestamp("date");

                data.add(new Logs(id, method, action, entryId, date));
            }
            DbConnection.logs(query);

            logsTable.setItems(data);

        } catch (SQLException e) {
            e.printStackTrace();
        }
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
    private void alertError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
