package com.aziz.demo;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

public class LoginController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button authButton;

    @FXML
    private TextField loginField;

    @FXML
    private TextField passwordField;

    @FXML
    private Button registerButton;

    @FXML
    void initialize() {
        authButton.setOnAction(event -> handleLogin());
        registerButton.setOnAction(event -> loaderPage("/com/aziz/demo/Register.fxml"));
    }

    private void handleLogin() {
        String login = loginField.getText().trim();
        String password = passwordField.getText().trim();

        if (login.isEmpty() || password.isEmpty()) {
            alertError("Заполните всю форму");
            return;
        }
        String logined = loginUser(login, password);
        if (logined.equals("Admin")) {
            loaderPage("/com/aziz/demo/AdminMain.fxml");
        }else if(logined.equals("Operator")){
            loaderPage("/com/aziz/demo/OperatorMain.fxml");
        }
    }
    private String loginUser(String login, String password) {
        String query = "SELECT password, roles FROM users WHERE username = ?";

        try (Connection dbConnection = DbConnection.connect_db();
             PreparedStatement prepared = dbConnection.prepareStatement(query)) {

            prepared.setString(1, login);

            try (ResultSet resultSet = prepared.executeQuery()) {
                if (resultSet.next()) {
                    String storedHash = resultSet.getString("password");
                    String role = resultSet.getString("roles");
                    String finalQuery = query.replace("?", "'%s'").formatted(login);
                    DbConnection.logs(finalQuery);

                    if (BCrypt.checkpw(password, storedHash)) {
                        return role;
                    } else {
                        alertError("Неверный пароль");
                    }
                } else {
                    alertError("Неверный логин");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            alertError("Ошибка базы данных");
        }
        return "Error";
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
            Stage stage = (Stage) registerButton.getScene().getWindow();
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
