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

import javafx.application.Platform;
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
import javafx.scene.control.*;
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
    public MenuItem filterByBestSelling;

    @FXML
    public DatePicker startDate;

    @FXML
    public DatePicker endDate;

    @FXML
    public Button mainButton;

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
        startDate.setVisible(false);
        endDate.setVisible(false);
        mainButton.setOnAction(event -> loaderPage("/com/aziz/demo/OperatorMain.fxml"));
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
        filterByBestSelling.setOnAction(event -> getFilterByBestSelling());

    }

    private void dateVisible(Boolean set){
        startDate.setVisible(set);
        endDate.setVisible(set);
    }

    private void nullValueDate()
    {
        startDate.setValue(null);
        endDate.setValue(null);
    }

    private void getFilterByCreatedAt() {
        nullValueDate();
        dateVisible(false);
        lastFilterMethod = this::getFilterByCreatedAt;
        Map<LocalDate, Integer> dataMap = new LinkedHashMap<>();

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

            ObservableList<String> categories = FXCollections.observableArrayList();

            for (Map.Entry<LocalDate, Integer> entry : dataMap.entrySet()) {
                String dateStr = entry.getKey().toString();
                series.getData().add(new XYChart.Data<>(dateStr, entry.getValue()));
                categories.add(dateStr);
            }

            lineChart.getData().clear();
            lineChart.layout();
            lineChart.getData().add(series);

            int maxValue = dataMap.values().stream().max(Integer::compare).orElse(50);
            yAxis.setTickUnit(1);
            yAxis.setMinorTickCount(0);
            yAxis.setForceZeroInRange(true);
            yAxis.setAutoRanging(false);
            yAxis.setLowerBound(0);
            yAxis.setUpperBound(maxValue + 5);

            CategoryAxis xAxis = (CategoryAxis) lineChart.getXAxis();
            xAxis.setCategories(categories);
            xAxis.setTickLabelRotation(45);
            xAxis.setTickLabelGap(10);
            DbConnection.logs(query);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void getFilterByBestSelling() {
        dateVisible(true);
        lastFilterMethod = this::getFilterByBestSelling;
        Map<String, Integer> dataMap = new LinkedHashMap<>();

        String query;
        boolean hasDateFilter = startDate.getValue() != null && endDate.getValue() != null;

        if (hasDateFilter) {
            query = "SELECT name, SUM(sold) AS total_sold FROM register " +
                    "WHERE time BETWEEN ? AND ? " +
                    "GROUP BY name ORDER BY total_sold DESC";
        } else {
            query = "SELECT name, SUM(sold) AS total_sold FROM register " +
                    "GROUP BY name ORDER BY total_sold DESC";
        }


        try (Connection dbConnection = DbConnection.connect_db();
             PreparedStatement prepared = dbConnection.prepareStatement(query)) {

            if (hasDateFilter) {
                prepared.setObject(1, startDate.getValue());
                prepared.setObject(2, endDate.getValue());
            }

            try (ResultSet resultSet = prepared.executeQuery()) {
                while (resultSet.next()) {
                    String name = resultSet.getString("name");
                    int sold = resultSet.getInt("total_sold");
                    dataMap.put(name, sold);
                }
            }


            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Top Sold Products");

            ObservableList<String> categories = FXCollections.observableArrayList();

            for (Map.Entry<String, Integer> entry : dataMap.entrySet()) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
                categories.add(entry.getKey());
            }

            lineChart.getData().clear();
            lineChart.layout();
            lineChart.getData().add(series);

            int maxValue = dataMap.values().stream().max(Integer::compare).orElse(50);
            yAxis.setTickUnit(1);
            yAxis.setMinorTickCount(0);
            yAxis.setForceZeroInRange(true);
            yAxis.setAutoRanging(false);
            yAxis.setLowerBound(0);
            yAxis.setUpperBound(maxValue + 5);

            CategoryAxis xAxis = (CategoryAxis) lineChart.getXAxis();
            xAxis.setCategories(categories);
            xAxis.setTickLabelRotation(45);
            xAxis.setTickLabelGap(10);
            DbConnection.logs(query);

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


