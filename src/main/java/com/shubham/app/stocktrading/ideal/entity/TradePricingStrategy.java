package com.shubham.app.stocktrading.ideal.entity;

public interface TradePricingStrategy {

    double priceOf(Order incoming, Order resting);
}
