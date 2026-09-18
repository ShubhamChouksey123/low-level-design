package com.shubham.app.stocktrading.practice.entity;

import java.time.Instant;

public class Trade {

    /** - id - sellerId - buyerId - stockId - price - quantity - createdAt */
    private int id;

    private int sellerId;
    private int buyerId;
    private double price;
    private int quantity;
    private Instant createdAt;

    public Trade(int id, double price, int quantity, Instant createdAt) {
        this.id = id;
        this.price = price;
        this.quantity = quantity;
        this.createdAt = createdAt;
    }

    public void setBuyerId(int buyerId) {
        this.buyerId = buyerId;
    }

    public void setSellerId(int sellerId) {
        this.sellerId = sellerId;
    }

    @Override
    public String toString() {
        return "Trade{" + "id=" + id + ", sellerId=" + sellerId + ", buyerId=" + buyerId + ", price=" + price
                + ", quantity=" + quantity + ", createdAt=" + createdAt + '}';
    }
}
