package com.shubham.app.subscriptionbilling.ideal.service;

import org.junit.jupiter.api.Test;

import com.shubham.app.subscriptionbilling.ideal.entity.BillLineItem;
import com.shubham.app.subscriptionbilling.ideal.entity.DiscountPolicy;
import com.shubham.app.subscriptionbilling.ideal.exception.InvalidBillingConfigException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BundleDiscountPolicyTest {

    @Test
    void constructorRejectsAnOutOfRangePercent() {
        assertThrows(InvalidBillingConfigException.class, () -> new BundleDiscountPolicy(new BigDecimal("-1"), 2));
        assertThrows(InvalidBillingConfigException.class, () -> new BundleDiscountPolicy(new BigDecimal("101"), 2));
        assertThrows(InvalidBillingConfigException.class, () -> new BundleDiscountPolicy(null, 2));
    }

    @Test
    void constructorRejectsANonPositiveMinProductCount() {
        assertThrows(InvalidBillingConfigException.class, () -> new BundleDiscountPolicy(BigDecimal.TEN, 0));
    }

    @Test
    void noDiscountBelowTheMinimumProductCount() {
        DiscountPolicy policy = new BundleDiscountPolicy(new BigDecimal("10"), 2);
        List<BillLineItem> oneProduct = List.of(new BillLineItem("JIRA", 30, BigDecimal.TEN));

        assertEquals(BigDecimal.ZERO, policy.discountFor(BigDecimal.TEN, oneProduct));
    }

    @Test
    void appliesThePercentOnceTheMinimumProductCountIsMet() {
        DiscountPolicy policy = new BundleDiscountPolicy(new BigDecimal("10"), 2);
        List<BillLineItem> twoProducts = new ArrayList<>();
        twoProducts.add(new BillLineItem("JIRA", 30, new BigDecimal("5.00")));
        twoProducts.add(new BillLineItem("CONFLUENCE", 30, new BigDecimal("5.00")));

        assertEquals(new BigDecimal("1.00"), policy.discountFor(new BigDecimal("10.00"), twoProducts));
    }
}
