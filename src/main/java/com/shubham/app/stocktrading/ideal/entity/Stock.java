package com.shubham.app.stocktrading.ideal.entity;

public class Stock {

    private final int id;

    public Stock(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Stock{id=" + id + '}';
    }
}
