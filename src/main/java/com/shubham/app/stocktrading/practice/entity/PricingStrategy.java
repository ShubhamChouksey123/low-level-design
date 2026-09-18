package com.shubham.app.stocktrading.practice.entity;

public interface PricingStrategy {

    public double getTradePrice(Order incomingOrder, Order restingOrder);
}
