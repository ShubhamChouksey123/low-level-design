package com.shubham.app.stocktrading.practice.services;

import com.shubham.app.stocktrading.practice.entity.Order;
import com.shubham.app.stocktrading.practice.entity.PricingStrategy;

public class RestingOrderPriceStrategy implements PricingStrategy {
    @Override
    public double getTradePrice(Order incomingOrder, Order restingOrder) {
        return restingOrder.getPrice();
    }
}
