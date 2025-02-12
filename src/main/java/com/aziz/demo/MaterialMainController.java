package com.aziz.demo;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class MaterialMainController {

    public Button analyticsButton;
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button materialButton;

    @FXML
    private TableColumn<Material, String> materialID;

    @FXML
    private Button createButton;

    @FXML
    private Button deleteRowButton;

    @FXML
    private MenuItem exitAction;

    @FXML
    private Button countryButton;

    @FXML
    private TableColumn<Material, String> materialMaterial;

    @FXML
    private TableView<Material> materialTable;

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

    public Material selectedRow;

    @FXML
    void initialize() {
        setupTableColumns();
        loadTableDataFromDB();
        deleteRowButton.setDisable(true);
        updateRowButton.setDisable(true);
        materialTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedRow = newSelection;
            deleteRowButton.setDisable(newSelection == null);
            updateRowButton.setDisable(newSelection == null);
        });
        deleteRowButton.setOnAction(event -> deleteData());
        registerButton.setOnAction(event -> loadTableDataFromDB());
        updateRowButton.setOnAction(event -> loaderPage("/com/aziz/demo/RowEditorMaterial.fxml", false, true,false));
        createButton.setOnAction(actionEvent -> loaderPage("/com/aziz/demo/RowEditorMaterial.fxml",false,false,true));
        countryButton.setOnAction(event -> loaderPage("/com/aziz/demo/CountryMain.fxml",true,false,false));
        suppliersButton.setOnAction(event -> loaderPage("/com/aziz/demo/SupplierMain.fxml",true,false,false));
        staffButton.setOnAction(event->loaderPage("/com/aziz/demo/StaffMain.fxml",true,false,false));
        registerButton.setOnAction(event->loaderPage("/com/aziz/demo/RegisterMain.fxml",true,false,false));
        materialButton.setDisable(true);
        analyticsButton.setOnAction(event -> loaderPage("/com/aziz/demo/ChartAnalytics.fxml",true,false,false));

    }

    private void setupTableColumns() {
        materialID.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getID()));
        materialMaterial.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
    }

    private void deleteData() {
        String query = "DELETE FROM material WHERE id = ?";
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
        ObservableList<Material> data = FXCollections.observableArrayList();

        String query = "SELECT id, name FROM material";

        try (Connection dbConnection = DbConnection.connect_db();
             PreparedStatement prepared = dbConnection.prepareStatement(query);
             ResultSet resultSet = prepared. executeQuery()) {

            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String name = resultSet.getString("name");

                data.add(new Material(id, name));
            }

            materialTable.setItems(data);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void loaderPage(String path, Boolean close, Boolean isEditMaterial,Boolean isCreate) {
        try {
            if (close) {
                Stage stage = (Stage) sortButton.getScene().getWindow();
                stage.close();
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Parent root = loader.load();

            if (isEditMaterial) {
                RowEditorMaterialController controller = loader.getController();
                if (controller != null && selectedRow != null) {
                    controller.setMaterialData(selectedRow);
                    controller.setMaterialMainController(this);
                }
            } else if (isCreate) {
                RowEditorMaterialController controller = loader.getController();
                controller.setMaterialMainController(this);
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