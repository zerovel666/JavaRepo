package com.aziz.demo;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class AnalyticsController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button analyticsButton;

    @FXML
    private Button countryButton;

    @FXML
    private MenuItem exitAction;

    @FXML
    private Button materialButton;

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
    void initialize() {
        countryButton.setOnAction(event -> loaderPage("/com/aziz/demo/CountryMain.fxml"));
        suppliersButton.setOnAction(event->loaderPage("/com/aziz/demo/SupplierMain.fxml"));
        staffButton.setOnAction(event->loaderPage("/com/aziz/demo/StaffMain.fxml"));
        registerButton.setOnAction(event->loaderPage("/com/aziz/demo/RegisterMain.fxml"));
        materialButton.setOnAction(event->loaderPage("/com/aziz/demo/Material.fxml"));
        analyticsButton.setDisable(true);
    }
    private void loaderPage(String path) {
        try {
            Stage stage = (Stage) countryButton.getScene().getWindow();
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
