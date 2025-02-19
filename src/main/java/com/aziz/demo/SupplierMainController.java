package com.aziz.demo;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
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

public class SupplierMainController {

    @FXML
    public Button staffButton;

    @FXML
    public Button analyticsButton;

    @FXML
    public Button mainButton;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TableView<Supplier> supplierTable;

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
    private Button registerButton;

    @FXML
    private SplitMenuButton sortButton;

    @FXML
    private Button supplierButton;

    @FXML
    private TableColumn<Supplier,String> supplierCountry;

    @FXML
    private TableColumn<Supplier,String> supplierID;

    @FXML
    private Button suppliersButton;

    @FXML
    private TableColumn<Supplier,String> supplierSupplier;

    @FXML
    private Button updateRowButton;

    private Supplier selectedRow;

    @FXML
    void initialize() {
        setupTableColumns();
        loadTableDataFromDB();
        deleteRowButton.setDisable(true);
        updateRowButton.setDisable(true);
        supplierTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedRow = newSelection;
            deleteRowButton.setDisable(newSelection == null);
            updateRowButton.setDisable(newSelection == null);
        });
        deleteRowButton.setOnAction(event -> deleteData());
        registerButton.setOnAction(event -> loadTableDataFromDB());
        updateRowButton.setOnAction(event -> loaderPage("/com/aziz/demo/RowEditorSupplier.fxml", false, true,false));
        createButton.setOnAction(actionEvent -> loaderPage("/com/aziz/demo/RowEditorSupplier.fxml",false,false,true));

        countryButton.setOnAction(event -> loaderPage("/com/aziz/demo/CountryMain.fxml",true,false,false));
        suppliersButton.setDisable(true);
        staffButton.setOnAction(event->loaderPage("/com/aziz/demo/StaffMain.fxml",true,false,false));
        registerButton.setOnAction(event->loaderPage("/com/aziz/demo/RegisterMain.fxml",true,false,false));
        materialButton.setOnAction(event->loaderPage("/com/aziz/demo/Material.fxml",true,false,false));
        analyticsButton.setOnAction(event -> loaderPage("/com/aziz/demo/ChartAnalytics.fxml",true,false,false));
        mainButton.setOnAction(event -> loaderPage("/com/aziz/demo/OperatorMain.fxml",true,false,false));

    }

    private void setupTableColumns() {
        supplierID.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getID()));
        supplierSupplier.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSupplier()));
        supplierCountry.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCountry())); // Добавьте это
    }

    private void deleteData() {
        String query = "DELETE FROM suppliers WHERE id = ?";
        try (Connection dbConnection = DbConnection.connect_db();
             PreparedStatement prepared = dbConnection.prepareStatement(query)) {
            String id = selectedRow.getID();
            prepared.setLong(1,Long.parseLong(id));
            prepared.execute();
            loadTableDataFromDB();
            DbConnection.logs(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void loadTableDataFromDB() {
        ObservableList<Supplier> data = FXCollections.observableArrayList();

        String query = "SELECT id, supplier, country FROM suppliers";

        try (Connection dbConnection = DbConnection.connect_db();
             PreparedStatement prepared = dbConnection.prepareStatement(query);
             ResultSet resultSet = prepared.executeQuery()) {

            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String supplier = resultSet.getString("supplier");
                String country = resultSet.getString("country");

                data.add(new Supplier(id, supplier, country));
            }
            DbConnection.logs(query);
            supplierTable.setItems(data);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void loaderPage(String path, Boolean close, Boolean isEditSupplier, Boolean isCreate) {
        try {
            if (close) {
                Stage stage = (Stage) sortButton.getScene().getWindow();
                stage.close();
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Parent root = loader.load();

            if (isEditSupplier) {
                RowEditorSupplierController controller = loader.getController();
                if (controller != null && selectedRow != null) {
                    controller.setSupplierData(selectedRow);
                    controller.setSupplierMainController(this); // Передача текущего контроллера
                }
            } else if (isCreate){
                RowEditorSupplierController controller = loader.getController();
                controller.setSupplierMainController(this);
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
