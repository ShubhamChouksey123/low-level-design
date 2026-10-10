package com.shubham.app.subscriptionbilling.ideal2.entity;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;

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
