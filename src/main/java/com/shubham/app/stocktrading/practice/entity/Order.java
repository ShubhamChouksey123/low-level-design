package com.shubham.app.stocktrading.practice.entity;

import com.shubham.app.stocktrading.practice.Exception.InvalidStockQuantity;

public class Order {

    /** - id - userId - stockId - price - remainingQuantity - orderType */
    private int id;

    private int userId;
    private int stockId;
    private double price;
    private int remainingQuantity;
    private OrderType orderType;

    public Order(int id, int userId, int stockId, double price, int remainingQuantity, OrderType orderType) {
        this.id = id;
        this.userId = userId;
        this.stockId = stockId;
        this.price = price;
        this.remainingQuantity = remainingQuantity;
        this.orderType = orderType;
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

    public double getPrice() {
        return price;
    }

    public int getRemainingQuantity() {
        return remainingQuantity;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void fill(int quantity) {
        if (quantity <= 0) {
            throw new InvalidStockQuantity("invalid stock quantity");
        }
        remainingQuantity -= quantity;
    }

    public boolean isFullFilled() {
        return (remainingQuantity == 0);
    }

    @Override
    public String toString() {
        return "Order{" + "id=" + id + ", userId=" + userId + ", stockId=" + stockId + ", price=" + price
                + ", remainingQuantity=" + remainingQuantity + ", orderType=" + orderType + '}';
    }
}
