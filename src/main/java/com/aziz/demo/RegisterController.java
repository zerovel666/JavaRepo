package com.aziz.demo;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

public class RegisterController {

    @FXML
    private TextField firstNameField, lastNameField, passwordField, userNameField;

    @FXML
    private Button registerButton, backButton;

    @FXML
    private CheckBox checkboxAdmin, checkboxOperator;

    @FXML
    private AnchorPane rootPane;

    private static final String ADMIN_CODE = "SECRET123";

    private PasswordField codeField;

    private String roles = "User";

    @FXML
    void initialize() {
        backButton.setOnAction(event -> loaderPage("/com/aziz/demo/SignUp.fxml"));

        checkboxAdmin.setOnAction(event -> {
            if (checkboxAdmin.isSelected()) {
                showAdminCodeField();
                checkboxOperator.setSelected(false);
            }
            updateRoles();
        });

        checkboxOperator.setOnAction(event -> {
            if (checkboxOperator.isSelected()) {
                removeCodeField();
                checkboxAdmin.setDisable(false);
                checkboxAdmin.setSelected(false);
            }
            updateRoles();
        });

        registerButton.setOnAction(event -> handleRegistration());
    }

    private void updateRoles() {
        if (checkboxAdmin.isSelected()) {
            roles = "Admin";
        } else if (checkboxOperator.isSelected()) {
            roles = "Operator";
        } else {
            roles = "User";
        }
    }

    private void showAdminCodeField() {
        if (codeField == null) {
            codeField = new PasswordField();
            codeField.setPromptText("Введите код");
            codeField.setLayoutX(230.0);
            codeField.setLayoutY(300.0);
            rootPane.getChildren().add(codeField);
            codeField.setOnAction(e -> validateAdminCode());
        }
    }

    private void validateAdminCode() {
        if (ADMIN_CODE.equals(codeField.getText())) {
            checkboxAdmin.setSelected(true);
            removeCodeField();
        } else {
            alertError("Неверный код.");
            checkboxAdmin.setSelected(false);
        }
    }

    private void removeCodeField() {
        if (codeField != null) {
            rootPane.getChildren().remove(codeField);
            codeField = null;
        }
    }

    private void handleRegistration() {
        String username = userNameField.getText().trim();
        String password = passwordField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();

        if (isValidInput(username, password, firstName, lastName)) {
            registerUser(username, password, firstName, lastName);
            loadAppropriatePage();
        } else {
            alertError("Заполните всю форму");
        }
    }

    private boolean isValidInput(String username, String password, String firstName, String lastName) {
        return !username.isEmpty() && !password.isEmpty() && !firstName.isEmpty() && !lastName.isEmpty() &&
                (checkboxAdmin.isSelected() || checkboxOperator.isSelected());
    }

    private void loadAppropriatePage() {
        if (Stage.getWindows().size() > 1) {
            Stage currentStage = (Stage) registerButton.getScene().getWindow();
            currentStage.close();
        } else {
            String path = checkboxAdmin.isSelected() ? "/com/aziz/demo/AdminMain.fxml" : "/com/aziz/demo/OperatorMain.fxml";
            loaderPage(path);
        }
    }


    private void registerUser(String username, String password, String firstName, String lastName) {
        String query = "INSERT INTO users (username, password, first_name, last_name, roles) VALUES (?, ?, ?, ?, ?)";
        password = BCrypt.hashpw(password, BCrypt.gensalt());
        try (Connection dbConnection = DbConnection.connect_db();
             PreparedStatement prepared = dbConnection.prepareStatement(query)) {

            prepared.setString(1, username);
            prepared.setString(2, password);
            prepared.setString(3, firstName);
            prepared.setString(4, lastName);
            prepared.setString(5, roles);
            prepared.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
            Parent root = FXMLLoader.load(getClass().getResource(path));
            Stage stage = (Stage) registerButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
