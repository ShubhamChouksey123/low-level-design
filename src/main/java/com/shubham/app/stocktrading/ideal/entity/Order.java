package com.shubham.app.stocktrading.ideal.entity;

import com.shubham.app.stocktrading.ideal.exception.InvalidOrderException;

import java.time.Instant;

public class Order {

    private final int id;
    private final int userId;
    private final int stockId;
    private final Side side;
    private final double price;
    private final Instant createdAt;
    private int remainingQuantity;

    public Order(int id, int userId, int stockId, Side side, double price, int quantity) {
        if (price <= 0) {
            throw new InvalidOrderException("price must be positive: " + price);
        }
        if (quantity <= 0) {
            throw new InvalidOrderException("quantity must be positive: " + quantity);
        }

        this.id = id;
        this.userId = userId;
        this.stockId = stockId;
        this.side = side;
        this.price = price;
        this.remainingQuantity = quantity;
        this.createdAt = Instant.now();
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getStockId() {
        return stockId;
    }

    public Side getSide() {
        return side;
    }

    public double getPrice() {
        return price;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public int getRemainingQuantity() {
        return remainingQuantity;
    }

    // Only OrderBook calls this, and only while it holds its own synchronized lock
    // during
    // matching, so a partial fill can never race with another fill of the same
    // order.
    public void fill(int units) {
        if (units <= 0 || units > remainingQuantity) {
            throw new InvalidOrderException(
                    "cannot fill " + units + " units; only " + remainingQuantity + " remaining on order " + id);
        }
        remainingQuantity -= units;
    }

    public boolean isFullyFilled() {
        return remainingQuantity == 0;
    }

    @Override
    public String toString() {
        return "Order{id=" + id + ", userId=" + userId + ", stockId=" + stockId + ", side=" + side + ", price=" + price
                + ", remainingQuantity=" + remainingQuantity + '}';
    }
}
