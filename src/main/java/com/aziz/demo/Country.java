package com.aziz.demo;

import javafx.beans.property.SimpleStringProperty;

public class Country {
    private final SimpleStringProperty ID;
    private final SimpleStringProperty country;


    public Country(String ID, String country) {
        this.ID = new SimpleStringProperty(ID);
        this.country = new SimpleStringProperty(country);;
    }

    public String getID() {
        return ID.get();
    }

    public SimpleStringProperty IDProperty() {
        return ID;
    }

    public String getCountry() {
        return country.get();
    }

    public SimpleStringProperty countryProperty() {
        return country;
    }
}
