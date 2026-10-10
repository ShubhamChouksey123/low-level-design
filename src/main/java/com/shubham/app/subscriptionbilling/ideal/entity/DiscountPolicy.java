package com.shubham.app.subscriptionbilling.ideal.entity;

import java.math.BigDecimal;
import java.util.List;

/**
 * The Strategy for "how would you handle discounts?" - applied once, to a
 * month's subtotal, after every product's prorated cost has already been
 * computed and summed.
 */
public interface DiscountPolicy {

    BigDecimal discountFor(BigDecimal subtotal, List<BillLineItem> lineItems);
}
