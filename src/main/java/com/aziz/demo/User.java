package com.aziz.demo;

import javafx.beans.property.SimpleStringProperty;

public class User {
    private final SimpleStringProperty ID;
    private final SimpleStringProperty username;
    private final SimpleStringProperty password;
    private final SimpleStringProperty firstName;
    private final SimpleStringProperty lastName;
    private final SimpleStringProperty roles;

    public User(String ID, String password,String username, String firstName, String lastName, String roles){
        this.ID = new SimpleStringProperty(ID);
        this.password = new SimpleStringProperty(password);
        this.username = new SimpleStringProperty(username);
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.roles = new SimpleStringProperty(roles);
    }

    public String getID() {
        return ID.get();
    }

    public SimpleStringProperty IDProperty() {
        return ID;
    }

    public String getUsername() {
        return username.get();
    }

    public SimpleStringProperty usernameProperty() {
        return username;
    }

    public String getFirstName() {
        return firstName.get();
    }

    public SimpleStringProperty firstNameProperty() {
        return firstName;
    }

    public String getLastName() {
        return lastName.get();
    }

    public SimpleStringProperty lastNameProperty() {
        return lastName;
    }

    public String getRoles() {
        return roles.get();
    }

    public SimpleStringProperty rolesProperty() {
        return roles;
    }

    public String getPassword() {
        return password.get();
    }

    public SimpleStringProperty passwordProperty() {
        return password;
    }
}
