package com.aziz.demo;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

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
import javafx.stage.Stage;

public class RegisterMainController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TableView<Register> RegisterTable;

    @FXML
    private Button countryButton;

    @FXML
    private Button createButton;

    @FXML
    private Button deleteRowButton;

    @FXML
    private MenuItem exitAction;

    @FXML
    private Button materialButton;

    @FXML
    private MenuButton menuButton;

    @FXML
    private TableColumn<Register, Long> registerArticle;

    @FXML
    private Button registerButton;

    @FXML
    private TableColumn<Register, String> registerID;

    @FXML
    private TableColumn<Register, Integer> registerMaterial;

    @FXML
    private TableColumn<Register, String> registerName;

    @FXML
    private TableColumn<Register, Integer> registerSales;

    @FXML
    private TableColumn<Register, Integer> registerSupplier;

    @FXML
    private SplitMenuButton sortButton;

    @FXML
    private Button staffButton;

    @FXML
    private Button suppliersButton;

    @FXML
    private Button updateRowButton;

    private Register selectedRow;

    @FXML
    void initialize() {
        setupTableColumns();
        loadTableDataFromDB();
    }

    private void setupTableColumns() {
        deleteRowButton.setDisable(true);
        updateRowButton.setDisable(true);
        registerID.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getID()));
        registerName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        registerArticle.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().getArticle()).asObject());
        registerMaterial.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getMaterial()).asObject());
        registerSales.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getSales()).asObject());
        registerSupplier.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getSupplier()).asObject());
        RegisterTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedRow = newSelection;
            deleteRowButton.setDisable(newSelection == null);
            updateRowButton.setDisable(newSelection == null);
        });
        deleteRowButton.setOnAction(event -> deleteData());
        registerButton.setOnAction(event -> loadTableDataFromDB());
        updateRowButton.setOnAction(event -> loaderPage("/com/aziz/demo/RowEditorRegister.fxml", false, true));
        createButton.setOnAction(actionEvent -> loaderPage("/com/aziz/demo/RowEditorRegister.fxml",false,false));
    }

    private void deleteData() {
        String query = "DELETE FROM register WHERE id = ?";
        try (Connection dbConnection = DbConnection.connect_db();
        PreparedStatement prepared = dbConnection.prepareStatement(query)) {
            String id = selectedRow.getID();
            prepared.setLong(1,Long.parseLong(id));
            prepared.execute();
            loadTableDataFromDB();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void loadTableDataFromDB() {
        ObservableList<Register> data = FXCollections.observableArrayList();

        String query = "SELECT id, name, article, material, sales, supplier FROM register";

        try (Connection dbConnection = DbConnection.connect_db();
             PreparedStatement prepared = dbConnection.prepareStatement(query);
             ResultSet resultSet = prepared.executeQuery()) {

            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String name = resultSet.getString("name");
                long article = resultSet.getLong("article");
                int material = resultSet.getInt("material");
                int sales = resultSet.getInt("sales");
                int supplier = resultSet.getInt("supplier");

                data.add(new Register(id, name, article, material, sales, supplier));
            }

            RegisterTable.setItems(data);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void loaderPage(String path, Boolean close, Boolean isEditRegister) {
        try {
            if (close) {
                Stage stage = (Stage) sortButton.getScene().getWindow();
                stage.close();
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Parent root = loader.load();

           if (isEditRegister){
               RowEditorRegisterController controller = loader.getController();
               if (controller != null && selectedRow != null) {
                   controller.setRegisterData(selectedRow);
               }
           }

            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            alertError("Ошибка загрузки страницы");
        }
    }


    private void alertSuccess(String message){
        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setTitle("Успех");
        successAlert.setHeaderText(null);
        successAlert.setContentText(message);
        successAlert.showAndWait();
    }

    private void alertError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
