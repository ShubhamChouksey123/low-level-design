package com.shubham.app.stocktrading.ideal.service;

import org.junit.jupiter.api.Test;

import com.shubham.app.stocktrading.ideal.entity.Order;
import com.shubham.app.stocktrading.ideal.entity.Side;
import com.shubham.app.stocktrading.ideal.entity.Stock;
import com.shubham.app.stocktrading.ideal.entity.Trade;
import com.shubham.app.stocktrading.ideal.exception.NoSuchStockException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MatchingEngineTest {

    @Test
    void submittingAnOrderForARegisteredStockRoutesToItsOwnBook() {
        MatchingEngine engine = new MatchingEngine();
        Stock stock = new Stock(1);
        engine.registerStock(stock, new RestingOrderPriceStrategy());
        engine.submitOrder(new Order(0, 2, 1, Side.SELL, 100.0, 10));

        List<Trade> trades = engine.submitOrder(new Order(1, 1, 1, Side.BUY, 100.0, 10));

        assertEquals(1, trades.size());
    }

    @Test
    void submittingAnOrderForAnUnregisteredStockThrows() {
        MatchingEngine engine = new MatchingEngine();

        assertThrows(NoSuchStockException.class, () -> engine.submitOrder(new Order(0, 1, 999, Side.BUY, 10.0, 1)));
    }
}
