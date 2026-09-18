package com.shubham.app.stocktrading.practice.services;

import com.shubham.app.stocktrading.practice.entity.Order;
import com.shubham.app.stocktrading.practice.entity.OrderBook;
import com.shubham.app.stocktrading.practice.entity.PricingStrategy;

public class MatchingEngine {

    public OrderBook orderBook;

    public MatchingEngine(PricingStrategy pricingStrategy) {
        this.orderBook = new OrderBook(pricingStrategy);
    }

    public void addOrder(Order incomingOrder) {
        orderBook.addOrder(incomingOrder);
    }
}
