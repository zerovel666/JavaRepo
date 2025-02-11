package com.aziz.demo;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Staff {
    private final SimpleStringProperty ID;
    private final SimpleStringProperty full_name;
    private final Date date_employment;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public Staff(String ID, String full_name, Date date_employment) {
        this.ID = new SimpleStringProperty(ID);
        this.full_name = new SimpleStringProperty(full_name);
        this.date_employment = date_employment;
    }

    public String getID() {
        return ID.get();
    }

    public SimpleStringProperty IDProperty() {
        return ID;
    }

    public String getFull_name() {
        return full_name.get();
    }

    public SimpleStringProperty full_nameProperty() {
        return full_name;
    }

    public String getDateEmploymentFormatted() {
        return DATE_FORMAT.format(date_employment);
    }

    public Date getDateEmployment() {
        return date_employment;
    }
}
