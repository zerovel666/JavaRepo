package com.aziz.demo;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.sun.net.httpserver.Authenticator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class AdminMainController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button createButton;

    @FXML
    private MenuItem exitAction;

    @FXML
    private MenuButton menuButton;

    @FXML
    private MenuItem renderOperationAction;

    @FXML
    private MenuItem renderUserAction;

    @FXML
    private Button countryButton;


    @FXML
    private Button materialButton;

    @FXML
    private Button registerButton;

    @FXML
    private SplitMenuButton sortButton;

    @FXML
    private Button staffButton;

    @FXML
    private Button suppliersButton;

    @FXML
    private TableColumn<User, String> firstNameColumn;

    @FXML
    private TableColumn<User, String> idColumn;

    @FXML
    private TableColumn<User, String> lastNameColumn;

    @FXML
    private TableColumn<User, String> rolesColumn;

    @FXML
    private TableColumn<User, String> usernameColumn;

    @FXML
    private TableView<User> usersTable;

    @FXML
    private Button usersButton;

    @FXML
    private Button deleteRowButton;

    @FXML
    private Button updateRowButton;

    @FXML
    private Button logsButton;

    @FXML
    void initialize() {
        deleteRowButton.setDisable(true);
        updateRowButton.setDisable(true);
        exitAction.setOnAction(event -> loaderPage("/com/aziz/demo/SignUp.fxml",true,true));

        usersButton.setOnAction(event -> {
            idColumn.setCellValueFactory(new PropertyValueFactory<>("ID"));
            usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
            firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
            lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
            rolesColumn.setCellValueFactory(new PropertyValueFactory<>("roles"));

            loadUsers();
        });

        usersTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<User>() {
            @Override
            public void changed(ObservableValue<? extends User> observable, User oldValue, User newValue) {
                if (newValue != null) {
                    deleteRowButton.setDisable(false);
                    updateRowButton.setDisable(false);
                }
            }
        });

        updateRowButton.setOnAction(event -> {
            loaderPage("/com/aziz/demo/RowEditorUsers.fxml",false,true);
        });
        deleteRowButton.setOnAction(event -> {
            deleteUser();
        });
        createButton.setOnAction(event -> {
            loaderPage("/com/aziz/demo/Register.fxml",false,false);
        });

        logsButton.setOnAction(event -> loaderPage("/com/aziz/demo/LogsMain.fxml",true,false));
    }

    private void deleteUser() {
        String query = "DELETE FROM users WHERE \"ID\" = ?";
        try (Connection dbConnection = DbConnection.connect_db();
        PreparedStatement prepared = dbConnection.prepareStatement(query);
        ){
            User selectedUser = usersTable.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                prepared.setLong(1, Long.parseLong(selectedUser.getID()));
                prepared.execute();
                usersTable.getItems().remove(selectedUser);
                alertSuccess("Пользователь успешно удален");
                loadUsers();
                DbConnection.logs(query);

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void alertSuccess(String message){
        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setTitle("Успех");
        successAlert.setHeaderText(null);
        successAlert.setContentText(message);
        successAlert.showAndWait();
    }
    public void loadUsers() {
        String query = "SELECT * FROM users";

        try (Connection dbConnection = DbConnection.connect_db();
             PreparedStatement statement = dbConnection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            ObservableList<User> userList = FXCollections.observableArrayList();

            while (resultSet.next()) {
                String id = resultSet.getString("ID");
                String password = resultSet.getString("password");
                String username = resultSet.getString("username");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String roles = resultSet.getString("roles");

                userList.add(new User(id, password,username, firstName, lastName, roles));
            }

            usersTable.setItems(userList);
            DbConnection.logs(query);

        } catch (SQLException e) {
            e.printStackTrace();
            alertError("Ошибка загрузки данных пользователей");
        }
    }

    private void alertError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loaderPage(String path, Boolean close, Boolean isEditUser) {
        try {
            if (close) {
                Stage stage = (Stage) sortButton.getScene().getWindow();
                stage.close();
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Parent root = loader.load();

            if (isEditUser) {
                User selectedUser = usersTable.getSelectionModel().getSelectedItem();
                if (selectedUser != null) {
                    RowEditorController controller = loader.getController();
                    controller.setUserData(selectedUser);
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

}
