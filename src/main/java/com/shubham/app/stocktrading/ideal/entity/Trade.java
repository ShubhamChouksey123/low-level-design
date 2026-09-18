package com.shubham.app.stocktrading.ideal.entity;

import java.time.Instant;

public class Trade {

    private final int id;
    private final int stockId;
    private final double price;
    private final int units;
    private final Instant createdAt;
    private final int buyOrderId;
    private final int sellOrderId;
    private final int buyerId;
    private final int sellerId;

    public Trade(int id, int stockId, double price, int units, int buyOrderId, int sellOrderId, int buyerId,
            int sellerId) {
        this.id = id;
        this.stockId = stockId;
        this.price = price;
        this.units = units;
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.createdAt = Instant.now();
    }

    public int getId() {
        return id;
    }

    public int getStockId() {
        return stockId;
    }

    public double getPrice() {
        return price;
    }

    public int getUnits() {
        return units;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public int getBuyOrderId() {
        return buyOrderId;
    }

    public int getSellOrderId() {
        return sellOrderId;
    }

    public int getBuyerId() {
        return buyerId;
    }

    public int getSellerId() {
        return sellerId;
    }

    @Override
    public String toString() {
        return "Trade{id=" + id + ", stockId=" + stockId + ", price=" + price + ", units=" + units + ", buyOrderId="
                + buyOrderId + ", sellOrderId=" + sellOrderId + ", buyerId=" + buyerId + ", sellerId=" + sellerId + '}';
    }
}
