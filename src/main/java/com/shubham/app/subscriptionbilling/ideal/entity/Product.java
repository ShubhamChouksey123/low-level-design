package com.shubham.app.subscriptionbilling.ideal.entity;

import com.shubham.app.subscriptionbilling.ideal.exception.InvalidProductException;

import java.math.BigDecimal;

public class Product {

    private final int id;
    private final String name;
    private final BigDecimal monthlyPrice;

    public Product(int id, String name, BigDecimal monthlyPrice) {
        if (name == null || name.isBlank()) {
            throw new InvalidProductException("name must not be blank");
        }
        if (monthlyPrice == null || monthlyPrice.signum() <= 0) {
            throw new InvalidProductException("monthlyPrice must be positive: " + monthlyPrice);
        }
        this.id = id;
        this.name = name;
        this.monthlyPrice = monthlyPrice;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getMonthlyPrice() {
        return monthlyPrice;
    }
}
