package com.shubham.app.subscriptionbilling.ideal.entity;

import java.math.BigDecimal;

public class BillLineItem {

    private final String productName;
    private final int billableDays;
    private final BigDecimal amount;

    public BillLineItem(String productName, int billableDays, BigDecimal amount) {
        this.productName = productName;
        this.billableDays = billableDays;
        this.amount = amount;
    }

    public String getProductName() {
        return productName;
    }

    public int getBillableDays() {
        return billableDays;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
