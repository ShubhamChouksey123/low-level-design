package com.shubham.app.stocktrading.ideal.service;

import com.shubham.app.stocktrading.ideal.entity.Order;
import com.shubham.app.stocktrading.ideal.entity.TradePricingStrategy;

public class RestingOrderPriceStrategy implements TradePricingStrategy {

    @Override
    public double priceOf(Order incoming, Order resting) {
        return resting.getPrice();
    }
}
