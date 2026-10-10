package com.shubham.app.subscriptionbilling.practice.entity;

public class Product {

    private int id;
    private String name;
    private double basePrice;
    private int days;

    public Product(int id, String name, double basePrice, int days) {
        this.id = id;
        this.name = name;
        this.basePrice = basePrice;
        this.days = days;
    }

    public int getId() {
        return id;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public double getPricePerDay() {
        return basePrice / days;
    }
}
