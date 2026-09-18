package com.shubham.app.stocktrading.ideal;

import com.shubham.app.stocktrading.ideal.entity.Order;
import com.shubham.app.stocktrading.ideal.entity.Side;
import com.shubham.app.stocktrading.ideal.entity.Stock;
import com.shubham.app.stocktrading.ideal.entity.Trade;
import com.shubham.app.stocktrading.ideal.exception.NoSuchStockException;
import com.shubham.app.stocktrading.ideal.service.MatchingEngine;
import com.shubham.app.stocktrading.ideal.service.RestingOrderPriceStrategy;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        MatchingEngine matchingEngine = new MatchingEngine();
        Stock stock = new Stock(1);
        matchingEngine.registerStock(stock, new RestingOrderPriceStrategy());

        // Two resting sell orders at two different price levels — no buyer yet, so both
        // just
        // rest in the book.
        matchingEngine.submitOrder(new Order(0, 2, stock.getId(), Side.SELL, 100.0, 10));
        matchingEngine.submitOrder(new Order(1, 3, stock.getId(), Side.SELL, 101.0, 5));

        // A buy order that crosses both price levels: price-time priority means the
        // cheaper
        // ask (100.0) is consumed first and fully, then the next-best ask (101.0)
        // partially.
        List<Trade> trades = matchingEngine.submitOrder(new Order(2, 1, stock.getId(), Side.BUY, 101.0, 12));
        System.out.println("Trades from the crossing buy order:");
        for (Trade trade : trades) {
            System.out.println("  " + trade);
        }

        // This buy doesn't cross the remaining ask (101.0, 3 units left) — it just
        // rests.
        List<Trade> noTrades = matchingEngine.submitOrder(new Order(3, 4, stock.getId(), Side.BUY, 50.0, 3));
        System.out.println("Trades from the non-crossing buy order: " + noTrades.size());

        try {
            matchingEngine.submitOrder(new Order(4, 1, 999, Side.BUY, 10.0, 1));
        } catch (NoSuchStockException e) {
            System.out.println("Rejected: " + e.getMessage());
        }
    }
}
