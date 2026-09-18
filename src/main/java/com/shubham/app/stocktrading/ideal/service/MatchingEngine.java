package com.shubham.app.stocktrading.ideal.service;

import com.shubham.app.stocktrading.ideal.entity.Order;
import com.shubham.app.stocktrading.ideal.entity.OrderBook;
import com.shubham.app.stocktrading.ideal.entity.Stock;
import com.shubham.app.stocktrading.ideal.entity.Trade;
import com.shubham.app.stocktrading.ideal.entity.TradePricingStrategy;
import com.shubham.app.stocktrading.ideal.exception.NoSuchStockException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MatchingEngine {

    private final Map<Integer, OrderBook> orderBooksByStockId = new ConcurrentHashMap<>();

    public void registerStock(Stock stock, TradePricingStrategy pricingStrategy) {
        orderBooksByStockId.put(stock.getId(), new OrderBook(stock.getId(), pricingStrategy));
    }

    public List<Trade> submitOrder(Order order) {
        OrderBook book = orderBooksByStockId.get(order.getStockId());
        if (book == null) {
            throw new NoSuchStockException("no such stock: " + order.getStockId());
        }
        return book.submit(order);
    }
}
