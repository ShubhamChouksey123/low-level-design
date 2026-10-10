package com.shubham.app.subscriptionbilling.ideal.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * Generates a user's monthly and annual bills. Each product's monthly price is
 * prorated by the number of billable days used, divided by the actual number of
 * days in that calendar month ({@link YearMonth#lengthOfMonth()}) - not a
 * hardcoded constant. {@link TrialPolicy} decides how many of a product's used
 * days are billable before proration runs; {@link DiscountPolicy} then
 * discounts the resulting subtotal once, for the whole month.
 */
public class BillingService {

    private static final int CURRENCY_SCALE = 2;

    private final ProductCatalog productCatalog;
    private final UsageRepository usageRepository;
    private final TrialPolicy trialPolicy;
    private final DiscountPolicy discountPolicy;

    public BillingService(ProductCatalog productCatalog, UsageRepository usageRepository, TrialPolicy trialPolicy,
            DiscountPolicy discountPolicy) {
        this.productCatalog = productCatalog;
        this.usageRepository = usageRepository;
        this.trialPolicy = trialPolicy;
        this.discountPolicy = discountPolicy;
    }

    public Bill generateMonthlyBill(int userId, YearMonth period) {
        List<UsageRecord> usageRecords = usageRepository.getUsage(userId, period);
        List<BillLineItem> lineItems = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (UsageRecord usageRecord : usageRecords) {
            Product product = productCatalog.getProduct(usageRecord.getProductId());
            int billableDays = trialPolicy.billableDays(product, usageRecord.getDaysUsed());
            BigDecimal amount = product.getMonthlyPrice().multiply(BigDecimal.valueOf(billableDays))
                    .divide(BigDecimal.valueOf(period.lengthOfMonth()), CURRENCY_SCALE, RoundingMode.HALF_UP);
            lineItems.add(new BillLineItem(product.getName(), billableDays, amount));
            subtotal = subtotal.add(amount);
        }

        BigDecimal discount = discountPolicy.discountFor(subtotal, lineItems);
        BigDecimal total = subtotal.subtract(discount);

        return new Bill(period, lineItems, subtotal, discount, total);
    }

    public List<Bill> monthlyBills(int userId, int year) {
        List<Bill> bills = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            bills.add(generateMonthlyBill(userId, YearMonth.of(year, month)));
        }
        return bills;
    }

    public BigDecimal annualCost(int userId, int year) {
        BigDecimal total = BigDecimal.ZERO;
        for (Bill bill : monthlyBills(userId, year)) {
            total = total.add(bill.getTotal());
        }
        return total;
    }
}
