package com.aziz.demo;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

public class Register {
    private final SimpleStringProperty ID;
    private final SimpleStringProperty name;
    private final SimpleLongProperty article;
    private final SimpleIntegerProperty supplier;
    private final SimpleIntegerProperty material;
    private final SimpleIntegerProperty sales;

    public Register(String ID, String name, long article, int supplier, int material, int sales) {
        this.ID = new SimpleStringProperty(ID);
        this.name = new SimpleStringProperty(name);
        this.article = new SimpleLongProperty(article);
        this.supplier = new SimpleIntegerProperty(supplier);
        this.material = new SimpleIntegerProperty(material);
        this.sales = new SimpleIntegerProperty(sales);
    }


    public String getID() {
        return ID.get();
    }

    public String getName() {
        return name.get();
    }

    public long getArticle() {
        return article.get();
    }

    public int getSupplier() {
        return supplier.get();
    }

    public int getMaterial() {
        return material.get();
    }

    public int getSales() {
        return sales.get();
    }
}
