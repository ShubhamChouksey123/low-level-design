package com.shubham.app.subscriptionbilling.practice.entity;

public class ProductUsage {

    private int id;
    private int productId;
    private int usedInDays;
    private int userId;
    private int month;

    public ProductUsage(int id, int productId, int usedInDays, int userId, int month) {
        this.id = id;
        this.productId = productId;
        this.usedInDays = usedInDays;
        this.userId = userId;
        this.month = month;
    }

    public int getUserId() {
        return userId;
    }

    public int getMonth() {
        return month;
    }

    public int getUsedInDays() {
        return usedInDays;
    }

    public int getProductId() {
        return productId;
    }
}
