package com.shubham.app.subscriptionbilling.ideal2.service;

import com.shubham.app.subscriptionbilling.ideal2.entity.BillLineItem;
import com.shubham.app.subscriptionbilling.ideal2.entity.DiscountPolicy;

import java.math.BigDecimal;
import java.util.List;

public class NoDiscountPolicy implements DiscountPolicy {

    @Override
    public BigDecimal discountFor(BigDecimal subtotal, List<BillLineItem> lineItems) {
        return BigDecimal.ZERO;
    }
}
