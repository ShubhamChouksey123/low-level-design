package com.shubham.app.subscriptionbilling.ideal.entity;

import org.junit.jupiter.api.Test;

import com.shubham.app.subscriptionbilling.ideal.service.BundleDiscountPolicy;
import com.shubham.app.subscriptionbilling.ideal.service.FixedTrialPeriodPolicy;
import com.shubham.app.subscriptionbilling.ideal.service.NoDiscountPolicy;
import com.shubham.app.subscriptionbilling.ideal.service.NoTrialPolicy;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BillingServiceTest {

    private static final int JIRA = 1;
    private static final int CONFLUENCE = 2;
    private static final int BITBUCKET = 3;

    private ProductCatalog catalogWithTheThreeAtlassianProducts() {
        ProductCatalog catalog = new ProductCatalog();
        catalog.register(new Product(JIRA, "JIRA", new BigDecimal("10")));
        catalog.register(new Product(CONFLUENCE, "CONFLUENCE", new BigDecimal("7.5")));
        catalog.register(new Product(BITBUCKET, "BITBUCKET", new BigDecimal("8")));
        return catalog;
    }

    // The exact scenario from the problem statement and the practice code's own
    // Main.java (JIRA
    // 14 days, CONFLUENCE 30 days, BITBUCKET 7 days), run against a 30-day month so
    // the raw
    // proration math matches the practice code's 14.0333... exactly before
    // rounding. The totals
    // differ on purpose: real money is cent-accurate, so each line item is rounded
    // to 2 decimal
    // places before summing (4.67 + 7.50 + 1.87 = 14.04), not left as an unrounded
    // double sum.
    @Test
    void matchesThePracticeScenarioOnceRoundedToTheNearestCent() {
        ProductCatalog catalog = catalogWithTheThreeAtlassianProducts();
        UsageRepository usageRepository = new UsageRepository();
        YearMonth thirtyDayMonth = YearMonth.of(2025, 4);
        usageRepository.recordUsage(1, new UsageRecord(JIRA, thirtyDayMonth, 14));
        usageRepository.recordUsage(1, new UsageRecord(CONFLUENCE, thirtyDayMonth, 30));
        usageRepository.recordUsage(1, new UsageRecord(BITBUCKET, thirtyDayMonth, 7));

        BillingService billingService = new BillingService(catalog, usageRepository, new NoTrialPolicy(),
                new NoDiscountPolicy());
        Bill bill = billingService.generateMonthlyBill(1, thirtyDayMonth);

        assertEquals(new BigDecimal("4.67"), bill.getLineItems().get(0).getAmount());
        assertEquals(new BigDecimal("7.50"), bill.getLineItems().get(1).getAmount());
        assertEquals(new BigDecimal("1.87"), bill.getLineItems().get(2).getAmount());
        assertEquals(new BigDecimal("14.04"), bill.getTotal());
    }

    // The practice code hardcoded Product.days = 30 for every product, so the same
    // 20 days of
    // usage would bill identically in every month. Here, the same 20 days of JIRA
    // usage bills
    // differently in a 28-day Feb vs a 29-day (leap year) Feb, because
    // YearMonth.lengthOfMonth()
    // is the actual divisor, not a stored constant.
    @Test
    void prorationUsesTheActualNumberOfDaysInThatCalendarMonth() {
        ProductCatalog catalog = catalogWithTheThreeAtlassianProducts();
        BigDecimal nonLeapFebCost = monthlyTotalForJiraUsage(catalog, YearMonth.of(2025, 2), 20);
        BigDecimal leapFebCost = monthlyTotalForJiraUsage(catalog, YearMonth.of(2024, 2), 20);

        assertEquals(new BigDecimal("7.14"), nonLeapFebCost);
        assertEquals(new BigDecimal("6.90"), leapFebCost);
    }

    private BigDecimal monthlyTotalForJiraUsage(ProductCatalog catalog, YearMonth period, int daysUsed) {
        UsageRepository usageRepository = new UsageRepository();
        usageRepository.recordUsage(1, new UsageRecord(JIRA, period, daysUsed));
        BillingService billingService = new BillingService(catalog, usageRepository, new NoTrialPolicy(),
                new NoDiscountPolicy());
        return billingService.generateMonthlyBill(1, period).getTotal();
    }

    // The practice code only ever computed one month at a time - there was no
    // method that
    // produced a full year's worth of bills, even though the problem explicitly
    // says "for a
    // given year, calculate monthly billing."
    @Test
    void monthlyBillsReturnsAllTwelveMonthsAndAnnualCostSumsThem() {
        ProductCatalog catalog = catalogWithTheThreeAtlassianProducts();
        UsageRepository usageRepository = new UsageRepository();
        for (int month = 1; month <= 12; month++) {
            usageRepository.recordUsage(1, new UsageRecord(JIRA, YearMonth.of(2025, month), 10));
        }
        BillingService billingService = new BillingService(catalog, usageRepository, new NoTrialPolicy(),
                new NoDiscountPolicy());

        List<Bill> bills = billingService.monthlyBills(1, 2025);
        assertEquals(12, bills.size());

        BigDecimal expectedAnnualCost = BigDecimal.ZERO;
        for (Bill bill : bills) {
            expectedAnnualCost = expectedAnnualCost.add(bill.getTotal());
        }
        assertEquals(expectedAnnualCost, billingService.annualCost(1, 2025));
        assertEquals(new BigDecimal("39.50"), billingService.annualCost(1, 2025));
    }

    // Proves the ④ extensibility claim: swapping in a real TrialPolicy and
    // DiscountPolicy costs
    // zero changes to BillingService - only the two constructor arguments change.
    @Test
    void trialPolicyAndDiscountPolicyPlugInWithNoChangesToBillingService() {
        ProductCatalog catalog = catalogWithTheThreeAtlassianProducts();
        UsageRepository usageRepository = new UsageRepository();
        YearMonth april = YearMonth.of(2025, 4);
        usageRepository.recordUsage(1, new UsageRecord(JIRA, april, 14));
        usageRepository.recordUsage(1, new UsageRecord(CONFLUENCE, april, 30));
        usageRepository.recordUsage(1, new UsageRecord(BITBUCKET, april, 7));

        BillingService billingService = new BillingService(catalog, usageRepository, new FixedTrialPeriodPolicy(5),
                new BundleDiscountPolicy(new BigDecimal("10"), 2));
        Bill bill = billingService.generateMonthlyBill(1, april);

        // JIRA: 14-5=9 billable days, 10*9/30 = 3.00. CONFLUENCE: 30-5=25, 7.5*25/30 =
        // 6.25.
        // BITBUCKET: 7-5=2, 8*2/30 = 0.53. Subtotal 9.78, 10% off (3 products >=
        // minProductCount
        // 2) = 0.98, total 8.80.
        assertEquals(new BigDecimal("3.00"), bill.getLineItems().get(0).getAmount());
        assertEquals(9, bill.getLineItems().get(0).getBillableDays());
        assertEquals(new BigDecimal("9.78"), bill.getSubtotal());
        assertEquals(new BigDecimal("0.98"), bill.getDiscount());
        assertEquals(new BigDecimal("8.80"), bill.getTotal());
    }

    @Test
    void aProductWithNoUsageInAMonthContributesNoLineItem() {
        ProductCatalog catalog = catalogWithTheThreeAtlassianProducts();
        UsageRepository usageRepository = new UsageRepository();
        YearMonth april = YearMonth.of(2025, 4);
        usageRepository.recordUsage(1, new UsageRecord(JIRA, april, 10));

        BillingService billingService = new BillingService(catalog, usageRepository, new NoTrialPolicy(),
                new NoDiscountPolicy());
        Bill bill = billingService.generateMonthlyBill(1, april);

        assertEquals(1, bill.getLineItems().size());
    }
}
