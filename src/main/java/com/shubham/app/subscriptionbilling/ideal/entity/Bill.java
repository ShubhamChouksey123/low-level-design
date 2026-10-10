package com.shubham.app.subscriptionbilling.ideal.entity;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;

/**
 * One user's itemized invoice for one calendar month - a per-product breakdown
 * plus the subtotal, the discount applied, and the final total. The practice
 * code's {@code getMonthlyCost} returned a bare {@code double} with no
 * breakdown at all; this is what it was missing.
 */
public class Bill {

    private final YearMonth period;
    private final List<BillLineItem> lineItems;
    private final BigDecimal subtotal;
    private final BigDecimal discount;
    private final BigDecimal total;

    public Bill(YearMonth period, List<BillLineItem> lineItems, BigDecimal subtotal, BigDecimal discount,
            BigDecimal total) {
        this.period = period;
        this.lineItems = Collections.unmodifiableList(lineItems);
        this.subtotal = subtotal;
        this.discount = discount;
        this.total = total;
    }

    public YearMonth getPeriod() {
        return period;
    }

    public List<BillLineItem> getLineItems() {
        return lineItems;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public BigDecimal getTotal() {
        return total;
    }
}
