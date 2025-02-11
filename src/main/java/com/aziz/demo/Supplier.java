package com.aziz.demo;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Supplier {
    private final SimpleStringProperty ID;
    private final SimpleStringProperty supplier;
    private final SimpleStringProperty country;


    public Supplier(String ID, String supplier, String country) {
        this.ID = new SimpleStringProperty(ID);
        this.supplier = new SimpleStringProperty(supplier);
        this.country = new SimpleStringProperty(country);;
    }


    public String getID() {
        return ID.get();
    }

    public SimpleStringProperty IDProperty() {
        return ID;
    }

    public String getSupplier() {
        return supplier.get();
    }

    public SimpleStringProperty supplierProperty() {
        return supplier;
    }

    public String getCountry() {
        return country.get();
    }

    public SimpleStringProperty countryProperty() {
        return country;
    }
}
