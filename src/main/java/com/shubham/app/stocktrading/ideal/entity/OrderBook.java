package com.shubham.app.stocktrading.ideal.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderBook {

    private final int stockId;
    private final TradePricingStrategy pricingStrategy;

    // Ascending by price: the first key is always the lowest (best) ask.
    private final NavigableMap<Double, Deque<Order>> asks = new TreeMap<>();
    // Descending by price: the first key is always the highest (best) bid.
    private final NavigableMap<Double, Deque<Order>> bids = new TreeMap<>(Collections.reverseOrder());

    private final AtomicInteger nextTradeId = new AtomicInteger(0);

    public OrderBook(int stockId, TradePricingStrategy pricingStrategy) {
        this.stockId = stockId;
        this.pricingStrategy = pricingStrategy;
    }

    // Matching and resting an order must happen as one atomic step, the same lesson
    // as
    // ParkingLot.claimVacantSpot and MeetingRoomBoard.book: otherwise two incoming
    // orders on
    // different threads could both match against the same resting order's remaining
    // quantity.
    public synchronized List<Trade> submit(Order incoming) {
        NavigableMap<Double, Deque<Order>> oppositeSide = incoming.getSide() == Side.BUY ? asks : bids;
        NavigableMap<Double, Deque<Order>> ownSide = incoming.getSide() == Side.BUY ? bids : asks;

        List<Trade> trades = new ArrayList<>();

        while (!incoming.isFullyFilled() && !oppositeSide.isEmpty()) {
            double bestOppositePrice = oppositeSide.firstKey();
            if (!crosses(incoming, bestOppositePrice)) {
                break;
            }

            Deque<Order> restingOrders = oppositeSide.get(bestOppositePrice);
            while (!incoming.isFullyFilled() && !restingOrders.isEmpty()) {
                Order resting = restingOrders.peekFirst();
                int tradeUnits = Math.min(incoming.getRemainingQuantity(), resting.getRemainingQuantity());
                double tradePrice = pricingStrategy.priceOf(incoming, resting);

                incoming.fill(tradeUnits);
                resting.fill(tradeUnits);
                trades.add(buildTrade(incoming, resting, tradePrice, tradeUnits));

                if (resting.isFullyFilled()) {
                    restingOrders.removeFirst();
                }
            }

            if (restingOrders.isEmpty()) {
                oppositeSide.remove(bestOppositePrice);
            }
        }

        if (!incoming.isFullyFilled()) {
            rest(ownSide, incoming);
        }

        return trades;
    }

    private boolean crosses(Order incoming, double oppositePrice) {
        if (incoming.getSide() == Side.BUY) {
            return oppositePrice <= incoming.getPrice();
        }
        return oppositePrice >= incoming.getPrice();
    }

    private void rest(NavigableMap<Double, Deque<Order>> side, Order order) {
        Deque<Order> queue = side.get(order.getPrice());
        if (queue == null) {
            queue = new LinkedList<>();
            side.put(order.getPrice(), queue);
        }
        queue.addLast(order);
    }

    private Trade buildTrade(Order incoming, Order resting, double price, int units) {
        int buyOrderId;
        int sellOrderId;
        int buyerId;
        int sellerId;

        if (incoming.getSide() == Side.BUY) {
            buyOrderId = incoming.getId();
            sellOrderId = resting.getId();
            buyerId = incoming.getUserId();
            sellerId = resting.getUserId();
        } else {
            buyOrderId = resting.getId();
            sellOrderId = incoming.getId();
            buyerId = resting.getUserId();
            sellerId = incoming.getUserId();
        }

        return new Trade(nextTradeId.getAndIncrement(), stockId, price, units, buyOrderId, sellOrderId, buyerId,
                sellerId);
    }
}
