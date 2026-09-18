package com.shubham.app.stocktrading.practice.entity;

public class Stock {

    private int id;

    public Stock(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Stock{" + "id=" + id + '}';
    }
}
