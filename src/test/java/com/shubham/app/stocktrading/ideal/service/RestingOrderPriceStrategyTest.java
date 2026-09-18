package com.shubham.app.stocktrading.ideal.service;

import org.junit.jupiter.api.Test;

import com.shubham.app.stocktrading.ideal.entity.Order;
import com.shubham.app.stocktrading.ideal.entity.Side;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RestingOrderPriceStrategyTest {

    @Test
    void tradeAlwaysExecutesAtTheRestingOrdersPrice() {
        RestingOrderPriceStrategy strategy = new RestingOrderPriceStrategy();
        Order incoming = new Order(0, 1, 1, Side.BUY, 105.0, 10);
        Order resting = new Order(1, 2, 1, Side.SELL, 100.0, 10);

        assertEquals(100.0, strategy.priceOf(incoming, resting));
    }
}
