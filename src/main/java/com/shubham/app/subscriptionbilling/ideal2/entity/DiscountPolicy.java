package com.shubham.app.subscriptionbilling.ideal2.entity;

import java.math.BigDecimal;
import java.util.List;

/**
 * The extensibility seam for "how would you handle discounts?" - applied once,
 * to a month's subtotal, after every product's prorated cost has already been
 * computed and summed. In this scaled-down version only
 * {@code NoDiscountPolicy} exists - see ideal/ for a worked example
 * (BundleDiscountPolicy) that this version deliberately skips to stay inside a
 * 40-minute scope.
 */
public interface DiscountPolicy {

    BigDecimal discountFor(BigDecimal subtotal, List<BillLineItem> lineItems);
}
