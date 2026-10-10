package com.shubham.app.subscriptionbilling.ideal.service;

import com.shubham.app.subscriptionbilling.ideal.entity.BillLineItem;
import com.shubham.app.subscriptionbilling.ideal.entity.DiscountPolicy;
import com.shubham.app.subscriptionbilling.ideal.exception.InvalidBillingConfigException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * A flat percentage off the whole month's subtotal, once at least
 * {@code minProductCount} distinct products were billed that month - "a bundle
 * discount for buying multiple products," one reasonable reading of the
 * open-ended "how would you handle discounts?" follow-up.
 */
public class BundleDiscountPolicy implements DiscountPolicy {

    private final BigDecimal percentOff;
    private final int minProductCount;

    public BundleDiscountPolicy(BigDecimal percentOff, int minProductCount) {
        if (percentOff == null || percentOff.signum() < 0 || percentOff.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new InvalidBillingConfigException("percentOff must be between 0 and 100: " + percentOff);
        }
        if (minProductCount < 1) {
            throw new InvalidBillingConfigException("minProductCount must be at least 1: " + minProductCount);
        }
        this.percentOff = percentOff;
        this.minProductCount = minProductCount;
    }

    @Override
    public BigDecimal discountFor(BigDecimal subtotal, List<BillLineItem> lineItems) {
        if (lineItems.size() < minProductCount) {
            return BigDecimal.ZERO;
        }
        return subtotal.multiply(percentOff).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }
}
