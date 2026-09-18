package com.shubham.app.stocktrading.practice;

import com.shubham.app.stocktrading.practice.entity.Order;
import com.shubham.app.stocktrading.practice.entity.OrderType;
import com.shubham.app.stocktrading.practice.services.MatchingEngine;
import com.shubham.app.stocktrading.practice.services.RestingOrderPriceStrategy;

public class Main {

    public static void main(String[] args) {

        MatchingEngine engine = new MatchingEngine(new RestingOrderPriceStrategy());

        engine.addOrder(new Order(1, 1, 1, 101.1, 10, OrderType.SELL));
        engine.addOrder(new Order(1, 2, 1, 102.0, 8, OrderType.SELL));

        engine.addOrder(new Order(1, 3, 1, 103.0, 28, OrderType.BUY));
        engine.addOrder(new Order(1, 4, 1, 103.3, 28, OrderType.BUY));

        engine.addOrder(new Order(1, 1, 1, 102.1, 45, OrderType.SELL));
        engine.addOrder(new Order(1, 2, 1, 102.4, 80, OrderType.SELL));
    }
}
