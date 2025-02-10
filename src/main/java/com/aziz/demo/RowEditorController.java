package com.aziz.demo;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

public class RowEditorController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button cancelButton;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button saveButton;

    @FXML
    private TextField usernameField;

    private Integer id;

    @FXML
    void initialize() {
        saveButton.setOnAction(event -> {
            saveUser();
        });
    }

    private void saveUser() {
        String querySelect = "SELECT * FROM users WHERE \"ID\" = ?";
        String queryUpdate = "UPDATE users SET username = ?, first_name = ?, last_name = ?, password = ? WHERE \"ID\" = ?";

        try (Connection dbConnection = DbConnection.connect_db();
             PreparedStatement selectStatement = dbConnection.prepareStatement(querySelect)) {

            selectStatement.setInt(1, id);
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                try (PreparedStatement updateStatement = dbConnection.prepareStatement(queryUpdate)) {

                    String username = usernameField.getText().trim();
                    String firstName = firstNameField.getText().trim();
                    String lastName = lastNameField.getText().trim();
                    String password = passwordField.getText().trim();

                    if (username.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || password.isEmpty()) {
                        alertInfo("Все поля должны быть заполнены!");
                        return;
                    }
                    password = BCrypt.hashpw(password, BCrypt.gensalt());
                    updateStatement.setString(1, username);
                    updateStatement.setString(2, firstName);
                    updateStatement.setString(3, lastName);
                    updateStatement.setString(4, password);
                    updateStatement.setInt(5, id);

                    int rowsAffected = updateStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        alertInfo("Данные пользователя успешно обновлены, обновите страничку!");
                        Stage stage = (Stage) saveButton.getScene().getWindow();
                        stage.close();
                    } else {
                        alertInfo("Не удалось обновить данные пользователя.");
                    }
                }
            } else {
                alertInfo("Пользователь с таким ID не найден.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void alertInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Информация");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loaderPage(String path) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(path));
            Stage stage = (Stage) saveButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setUserData(User user) {
        if (user != null) {
            id = Integer.valueOf(user.getID());
            firstNameField.setText(user.getFirstName());
            lastNameField.setText(user.getLastName());
            usernameField.setText(user.getUsername());
            passwordField.setText(user.getPassword());
        }
    }

}
