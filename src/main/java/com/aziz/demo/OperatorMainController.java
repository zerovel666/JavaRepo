package com.aziz.demo;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.*;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class OperatorMainController {

    @FXML
    public Button analyticsButton;

    @FXML
    public Button mainButton;

    @FXML
    public TextField searchField;

    @FXML
    public Button searchButton;

    @FXML
    public TableColumn<Register,String> registerID;

    @FXML
    public TableColumn<Register,String> registerName;

    @FXML
    public TableColumn<Register,String> registerArticle;

    @FXML
    public TableColumn<Register,String> registerMaterial;

    @FXML
    public TableColumn<Register,String> registerSupplier;

    @FXML
    public TableColumn<Register,String> registerSales;

    @FXML
    public TableColumn<Register,String> registerCountHave;

    @FXML
    public TableColumn<Register,String> registerSold;

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
    private Button staffButton;

    @FXML
    private Button suppliersButton;

    @FXML
    private Button updateRowButton;

    @FXML
    private TableView<Register> operatorTable;

    @FXML
    private Button materialButton;

    @FXML
    void initialize() {
        registerButton.setOnAction(event->loaderPage("/com/aziz/demo/RegisterMain.fxml"));
        countryButton.setOnAction(event -> loaderPage("/com/aziz/demo/CountryMain.fxml"));
        suppliersButton.setOnAction(event -> loaderPage("/com/aziz/demo/SupplierMain.fxml"));
        staffButton.setOnAction(event->loaderPage("/com/aziz/demo/StaffMain.fxml"));
        registerButton.setOnAction(event->loaderPage("/com/aziz/demo/RegisterMain.fxml"));
        materialButton.setOnAction(event->loaderPage("/com/aziz/demo/MaterialMain.fxml"));
        analyticsButton.setOnAction(event -> loaderPage("/com/aziz/demo/ChartAnalytics.fxml"));
        searchButton.setOnAction(event -> search());
        registerID.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getID()));
        registerName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        registerArticle.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().getArticle()).asObject().asString());
        registerSupplier.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getSupplier()).asObject().asString());
        registerMaterial.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getMaterial()).asObject().asString());
        registerSales.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getSales()).asObject().asString());
        registerCountHave.setCellValueFactory(cellData -> cellData.getValue().count_haveProperty().asObject().asString());
        registerSold.setCellValueFactory(cellData -> cellData.getValue().soldProperty().asObject().asString());
        setupSearchAutocomplete();
        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                return;
            }
            String formattedText = newValue.substring(0, 1).toUpperCase() + newValue.substring(1);

            if (!newValue.equals(formattedText)) {
                searchField.setText(formattedText);
                searchField.positionCaret(formattedText.length());
            }
        });

    }

    private void search() {
        String query = "SELECT * FROM register WHERE name = ?";

        try (Connection dbConnection = DbConnection.connect_db();
             PreparedStatement prepared = dbConnection.prepareStatement(query)) {

            String name = searchField.getText().trim();
            prepared.setString(1, name);

            ObservableList<Register> data = FXCollections.observableArrayList();

            try (ResultSet resultSet = prepared.executeQuery()) {
                while (resultSet.next()) {
                    Register register = new Register(
                            String.valueOf(resultSet.getLong("id")),
                            resultSet.getString("name"),
                            resultSet.getLong("article"),
                            Integer.parseInt(resultSet.getString("supplier")),
                            Integer.parseInt(resultSet.getString("material")),
                            Integer.parseInt(resultSet.getString("sales")),
                            resultSet.getInt("count_have"),
                            resultSet.getInt("sold")
                    );
                    data.add(register);
                }
                String finalQuery = query.replace("?", "'%s'").formatted(name);
                DbConnection.logs(finalQuery);

            }
            operatorTable.setItems(data);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void setupSearchAutocomplete() {
        ContextMenu suggestions = new ContextMenu();

        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                suggestions.hide();
                return;
            }

            ObservableList<MenuItem> items = FXCollections.observableArrayList();
            String query = "SELECT DISTINCT name FROM register WHERE name LIKE ? LIMIT 5";

            try (Connection dbConnection = DbConnection.connect_db();
                 PreparedStatement prepared = dbConnection.prepareStatement(query)) {

                prepared.setString(1, "%" + newValue + "%");
                try (ResultSet resultSet = prepared.executeQuery()) {
                    while (resultSet.next()) {
                        String suggestion = resultSet.getString("name");
                        MenuItem item = new MenuItem(suggestion);
                        item.setOnAction(event -> {
                            searchField.setText(suggestion);
                            searchField.positionCaret(suggestion.length());
                            suggestions.hide();
                        });
                        items.add(item);
                        String finalQuery = query.replace("?", "'%s'").formatted(suggestion);
                        DbConnection.logs(finalQuery);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            suggestions.getItems().setAll(items);
            if (!items.isEmpty()) {
                suggestions.show(searchField, searchField.localToScreen(0, searchField.getHeight()).getX(),
                        searchField.localToScreen(0, searchField.getHeight()).getY());
            } else {
                suggestions.hide();
            }
        });
    }



    private void loaderPage(String path) {
        try {
            Stage stage = (Stage) searchField.getScene().getWindow();
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
