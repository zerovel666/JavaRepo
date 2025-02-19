package com.aziz.demo;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logs {
    private final SimpleStringProperty id;
    private final SimpleStringProperty method;
    private final SimpleStringProperty action;
    private final SimpleIntegerProperty entryId;
    private final Date date;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public Logs(String id, String method, String action, Integer entryId, Date date)
    {
        this.id = new SimpleStringProperty(id);
        this.method = new SimpleStringProperty(method);
        this.action = new SimpleStringProperty(action);
        this.entryId = new SimpleIntegerProperty(entryId);
        this.date = date;
    }

    public String getId() {
        return id.get();
    }

    public SimpleStringProperty idProperty() {
        return id;
    }

    public String getMethod() {
        return method.get();
    }

    public SimpleStringProperty methodProperty() {
        return method;
    }

    public String getAction() {
        return action.get();
    }

    public SimpleStringProperty actionProperty() {
        return action;
    }

    public int getEntryId() {
        return entryId.get();
    }

    public SimpleIntegerProperty entryIdProperty() {
        return entryId;
    }

    public Date getDate() {
        return date;
    }
    public String getDateFormated() {
        return DATE_FORMAT.format(date);
    }
}
