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

public class StaffMainController {

    public Button analyticsButton;
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TableView<Staff> staffTable;

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
    private TableColumn<Staff, String> staffID;

    @FXML
    private SplitMenuButton sortButton;

    @FXML
    private Button staffButton;

    @FXML
    private TableColumn<Staff, String> staffDateEmployement;

    @FXML
    private TableColumn<Staff, String> staffFullName;

    @FXML
    private Button suppliersButton;

    @FXML
    private Button updateRowButton;

    private Staff selectedRow;

    @FXML
    void initialize() {
        setupTableColumns();
        loadTableDataFromDB();
        deleteRowButton.setDisable(true);
        updateRowButton.setDisable(true);
        staffTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedRow = newSelection;
            deleteRowButton.setDisable(newSelection == null);
            updateRowButton.setDisable(newSelection == null);
        });
        deleteRowButton.setOnAction(event -> deleteData());
        registerButton.setOnAction(event -> loadTableDataFromDB());
        updateRowButton.setOnAction(event -> loaderPage("/com/aziz/demo/RowEditorStaff.fxml", false, true,false));
        createButton.setOnAction(actionEvent -> loaderPage("/com/aziz/demo/RowEditorStaff.fxml",false,false,true));
        countryButton.setOnAction(event -> loaderPage("/com/aziz/demo/CountryMain.fxml",true,false,false));
        suppliersButton.setOnAction(event -> loaderPage("/com/aziz/demo/SupplierMain.fxml",true,false,false));
        staffButton.setDisable(true);
        registerButton.setOnAction(event->loaderPage("/com/aziz/demo/RegisterMain.fxml",true,false,false));
        materialButton.setOnAction(event -> loaderPage("/com/aziz/demo/MaterialMain.fxml",true,false,false));
        analyticsButton.setOnAction(event -> loaderPage("/com/aziz/demo/ChartAnalytics.fxml",true,false,false));

    }

    private void setupTableColumns() {
        staffID.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getID()));
        staffFullName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFull_name()));

        staffDateEmployement.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDateEmploymentFormatted())
        );
    }

    private void deleteData() {
        String query = "DELETE FROM Staff WHERE id = ?";
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
        ObservableList<Staff> data = FXCollections.observableArrayList();

        String query = "SELECT id, full_name, date_employement FROM staff";

        try (Connection dbConnection = DbConnection.connect_db();
             PreparedStatement prepared = dbConnection.prepareStatement(query);
             ResultSet resultSet = prepared.executeQuery()) {

            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String full_name = resultSet.getString("full_name");
                Date date_employement = resultSet.getDate("date_employement");

                data.add(new Staff(id, full_name,date_employement));
            }

            staffTable.setItems(data);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void loaderPage(String path, Boolean close, Boolean isEditStaff,Boolean isCreate) {
        try {
            if (close) {
                Stage stage = (Stage) sortButton.getScene().getWindow();
                stage.close();
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Parent root = loader.load();

            if (isEditStaff) {
                RowEditorStaffController controller = loader.getController();
                if (controller != null && selectedRow != null) {
                    controller.setStaffData(selectedRow);
                    controller.setStaffMainController(this); // Передача текущего контроллера
                }
            } else if(isCreate) {
                RowEditorStaffController controller = loader.getController();
                controller.setStaffMainController(this);
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
