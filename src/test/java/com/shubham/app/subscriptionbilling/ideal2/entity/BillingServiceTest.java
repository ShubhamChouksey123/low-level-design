package com.shubham.app.subscriptionbilling.ideal2.entity;

import org.junit.jupiter.api.Test;

import com.shubham.app.subscriptionbilling.ideal2.service.NoDiscountPolicy;
import com.shubham.app.subscriptionbilling.ideal2.service.NoTrialPolicy;

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

        assertEquals(new BigDecimal("14.04"), bill.getTotal());
    }

    // The fix worth protecting time for even under a 40-minute clock: proration
    // uses the real
    // length of the month (YearMonth#lengthOfMonth()), not a hardcoded constant.
    @Test
    void prorationUsesTheActualNumberOfDaysInThatCalendarMonth() {
        ProductCatalog catalog = catalogWithTheThreeAtlassianProducts();

        UsageRepository nonLeapFebRepository = new UsageRepository();
        nonLeapFebRepository.recordUsage(1, new UsageRecord(JIRA, YearMonth.of(2025, 2), 20));
        BillingService nonLeapFebBillingService = new BillingService(catalog, nonLeapFebRepository, new NoTrialPolicy(),
                new NoDiscountPolicy());

        UsageRepository leapFebRepository = new UsageRepository();
        leapFebRepository.recordUsage(1, new UsageRecord(JIRA, YearMonth.of(2024, 2), 20));
        BillingService leapFebBillingService = new BillingService(catalog, leapFebRepository, new NoTrialPolicy(),
                new NoDiscountPolicy());

        assertEquals(new BigDecimal("7.14"),
                nonLeapFebBillingService.generateMonthlyBill(1, YearMonth.of(2025, 2)).getTotal());
        assertEquals(new BigDecimal("6.90"),
                leapFebBillingService.generateMonthlyBill(1, YearMonth.of(2024, 2)).getTotal());
    }

    // The other fix worth protecting time for: the problem says "for a given year,"
    // so the API
    // has to span all twelve months, not just the one the practice code ever tried.
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
        assertEquals(new BigDecimal("39.50"), billingService.annualCost(1, 2025));
    }

    // Confirms the no-op seam really is a no-op, so swapping it for a real policy
    // later changes
    // only this one constructor argument.
    @Test
    void noTrialPolicyAndNoDiscountPolicyAreGenuineNoOps() {
        ProductCatalog catalog = catalogWithTheThreeAtlassianProducts();
        UsageRepository usageRepository = new UsageRepository();
        YearMonth april = YearMonth.of(2025, 4);
        usageRepository.recordUsage(1, new UsageRecord(JIRA, april, 14));

        BillingService billingService = new BillingService(catalog, usageRepository, new NoTrialPolicy(),
                new NoDiscountPolicy());
        Bill bill = billingService.generateMonthlyBill(1, april);

        assertEquals(14, bill.getLineItems().get(0).getBillableDays());
        assertEquals(BigDecimal.ZERO, bill.getDiscount());
    }
}
