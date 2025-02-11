package com.aziz.demo;

import javafx.beans.property.SimpleStringProperty;

public class Material {
    private final SimpleStringProperty ID;
    private final SimpleStringProperty name;


    public Material(String ID, String name) {
        this.ID = new SimpleStringProperty(ID);
        this.name = new SimpleStringProperty(name);;
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public String getID() {
        return ID.get();
    }

    public SimpleStringProperty IDProperty() {
        return ID;
    }
}
