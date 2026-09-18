package com.shubham.app.stocktrading.practice.entity;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderBook {

    private NavigableMap<Double, Deque<Order>> asks;
    private NavigableMap<Double, Deque<Order>> bids;
    private AtomicInteger tradeId;
    private PricingStrategy pricingStrategy;

    public OrderBook(PricingStrategy pricingStrategy) {
        this.asks = new TreeMap<>();
        this.bids = new TreeMap<>(Collections.reverseOrder());
        this.tradeId = new AtomicInteger(0);
        this.pricingStrategy = pricingStrategy;
    }

    public synchronized void addOrder(Order incomingOrder) {

        NavigableMap<Double, Deque<Order>> otherSideMap = (incomingOrder.getOrderType() == OrderType.BUY) ? asks : bids;
        NavigableMap<Double, Deque<Order>> sameSideMap = (incomingOrder.getOrderType() == OrderType.BUY) ? bids : asks;

        List<Trade> trades = new ArrayList<>();

        while (!otherSideMap.isEmpty() && !incomingOrder.isFullFilled()) {
            double bestOppositePrice = otherSideMap.firstKey();
            if (!cross(incomingOrder, bestOppositePrice)) {
                break;
            }

            Deque<Order> restingOrders = otherSideMap.get(bestOppositePrice);

            while (!restingOrders.isEmpty() && !incomingOrder.isFullFilled()) {
                Order restingOrder = restingOrders.peekFirst();
                double settlementPrice = pricingStrategy.getTradePrice(incomingOrder, restingOrder);
                int settlementQuantity = Math.min(incomingOrder.getRemainingQuantity(),
                        restingOrder.getRemainingQuantity());

                Trade trade = createTrade(settlementPrice, settlementQuantity, incomingOrder, restingOrder);
                trades.add(trade);

                incomingOrder.fill(settlementQuantity);
                restingOrder.fill(settlementQuantity);

                if (restingOrder.isFullFilled()) {
                    restingOrders.pollFirst();
                }
            }

            if (restingOrders.isEmpty()) {
                otherSideMap.remove(bestOppositePrice);
            }
        }

        if (!incomingOrder.isFullFilled()) {
            restOrder(incomingOrder, sameSideMap);
        }
    }

    private boolean cross(Order incomingOrder, double bestOppositePrice) {
        if (incomingOrder.getOrderType() == OrderType.BUY) {
            return incomingOrder.getPrice() >= bestOppositePrice;
        }
        return incomingOrder.getPrice() <= bestOppositePrice;
    }

    private void restOrder(Order incomingOrder, Map<Double, Deque<Order>> sameSideMap) {
        double price = incomingOrder.getPrice();
        if (!sameSideMap.containsKey(price)) {
            Deque<Order> orders = new ArrayDeque<>();
            orders.add(incomingOrder);
            sameSideMap.put(price, orders);
            return;
        }
        sameSideMap.get(price).offerLast(incomingOrder);
    }

    private Trade createTrade(double settlementPrice, int settlementQuantity, Order incomingOrder, Order restingOrder) {

        Trade trade = new Trade(tradeId.getAndIncrement(), settlementPrice, settlementQuantity, Instant.now());
        if (incomingOrder.getOrderType() == OrderType.BUY) {
            trade.setBuyerId(incomingOrder.getUserId());
            trade.setSellerId(restingOrder.getUserId());
        } else {
            trade.setBuyerId(restingOrder.getUserId());
            trade.setSellerId(incomingOrder.getUserId());
        }
        System.out.println("trade settlement : " + trade);
        return trade;
    }
}
