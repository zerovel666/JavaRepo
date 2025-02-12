package com.aziz.demo;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class AnalyticsController {

    @FXML
    public LineChart lineChart;

    @FXML
    public Button updateCharButton;

    @FXML
    public MenuButton changeFilterMenu;

    @FXML
    public MenuItem filterItemByCreatedAt;

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
    private NumberAxis yAxis;

    @FXML
    private CategoryAxis xAxis;

    private Runnable lastFilterMethod;

    @FXML
    void initialize() {
        countryButton.setOnAction(event -> loaderPage("/com/aziz/demo/CountryMain.fxml"));
        suppliersButton.setOnAction(event->loaderPage("/com/aziz/demo/SupplierMain.fxml"));
        staffButton.setOnAction(event->loaderPage("/com/aziz/demo/StaffMain.fxml"));
        registerButton.setOnAction(event->loaderPage("/com/aziz/demo/RegisterMain.fxml"));
        materialButton.setOnAction(event->loaderPage("/com/aziz/demo/Material.fxml"));
        analyticsButton.setDisable(true);
        updateCharButton.setOnAction(event -> {
            if (lastFilterMethod != null){
                lastFilterMethod.run();
            }
        });
        filterItemByCreatedAt.setOnAction(event -> getFilterByCreatedAt());

    }

    private void getFilterByCreatedAt() {
        lastFilterMethod = this::getFilterByCreatedAt;
        Map<LocalDate, Integer> dataMap = new LinkedHashMap<>(); // Используем LinkedHashMap для сохранения порядка

        String query = "SELECT DATE(time) AS date, COUNT(*) AS count FROM register GROUP BY date ORDER BY date";

        try (Connection dbConnection = DbConnection.connect_db();
             PreparedStatement prepared = dbConnection.prepareStatement(query);
             ResultSet resultSet = prepared.executeQuery()) {

            while (resultSet.next()) {
                LocalDate date = resultSet.getDate("date").toLocalDate();
                int count = resultSet.getInt("count");
                dataMap.put(date, count);
            }

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Analytics register table");

            ObservableList<String> categories = FXCollections.observableArrayList(); // Категории для оси X

            for (Map.Entry<LocalDate, Integer> entry : dataMap.entrySet()) {
                String dateStr = entry.getKey().toString();
                series.getData().add(new XYChart.Data<>(dateStr, entry.getValue()));
                categories.add(dateStr);
            }

            // Очищаем график перед добавлением новых данных
            lineChart.getData().clear();
            lineChart.layout();
            lineChart.getData().add(series);

            // Настройка оси Y
            int maxValue = dataMap.values().stream().max(Integer::compare).orElse(50);
            yAxis.setTickUnit(1);
            yAxis.setMinorTickCount(0);
            yAxis.setForceZeroInRange(true);
            yAxis.setAutoRanging(false);
            yAxis.setLowerBound(0);
            yAxis.setUpperBound(maxValue + 5);

            // Настройка оси X (категории дат)
            CategoryAxis xAxis = (CategoryAxis) lineChart.getXAxis();
            xAxis.setCategories(categories);
            xAxis.setTickLabelRotation(45);
            xAxis.setTickLabelGap(10);

        } catch (SQLException e) {
            e.printStackTrace();
        }
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


