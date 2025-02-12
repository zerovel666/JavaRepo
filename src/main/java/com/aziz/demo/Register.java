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
    private final SimpleIntegerProperty count_have;
    private final SimpleIntegerProperty sold;

    public Register(String ID, String name, long article, int supplier, int material, int sales, int count_have, int sold) {
        this.ID = new SimpleStringProperty(ID);
        this.name = new SimpleStringProperty(name);
        this.article = new SimpleLongProperty(article);
        this.supplier = new SimpleIntegerProperty(supplier);
        this.material = new SimpleIntegerProperty(material);
        this.sales = new SimpleIntegerProperty(sales);
        this.count_have = new SimpleIntegerProperty(count_have);
        this.sold = new SimpleIntegerProperty(sold);
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

    public int getcount_have() {
        return count_have.get();
    }

    public SimpleIntegerProperty count_haveProperty() {
        return count_have;
    }

    public int getSold() {
        return sold.get();
    }

    public SimpleIntegerProperty soldProperty() {
        return sold;
    }
}
