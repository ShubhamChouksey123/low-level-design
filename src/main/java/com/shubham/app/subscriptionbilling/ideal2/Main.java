package com.shubham.app.subscriptionbilling.ideal2;

import com.shubham.app.subscriptionbilling.ideal2.entity.Bill;
import com.shubham.app.subscriptionbilling.ideal2.entity.BillLineItem;
import com.shubham.app.subscriptionbilling.ideal2.entity.BillingService;
import com.shubham.app.subscriptionbilling.ideal2.entity.Product;
import com.shubham.app.subscriptionbilling.ideal2.entity.ProductCatalog;
import com.shubham.app.subscriptionbilling.ideal2.entity.UsageRecord;
import com.shubham.app.subscriptionbilling.ideal2.entity.UsageRepository;
import com.shubham.app.subscriptionbilling.ideal2.service.NoDiscountPolicy;
import com.shubham.app.subscriptionbilling.ideal2.service.NoTrialPolicy;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

public class Main {

    private static final int JIRA = 1;
    private static final int CONFLUENCE = 2;
    private static final int BITBUCKET = 3;

    public static void main(String[] args) {
        ProductCatalog catalog = new ProductCatalog();
        catalog.register(new Product(JIRA, "JIRA", new BigDecimal("10")));
        catalog.register(new Product(CONFLUENCE, "CONFLUENCE", new BigDecimal("7.5")));
        catalog.register(new Product(BITBUCKET, "BITBUCKET", new BigDecimal("8")));

        // In the room: "DiscountPolicy and TrialPolicy are the two seams - a bundle
        // discount or a
        // free trial would each be one new class implementing one of these interfaces,
        // with zero
        // changes to BillingService. Here's the no-op version of each, passed into
        // every
        // BillingService below, so the design compiles and runs end to end."

        System.out.println("Practice scenario (JIRA 14 days, CONFLUENCE 30 days, BITBUCKET 7 days), April 2025:");
        UsageRepository usageRepository = new UsageRepository();
        YearMonth april2025 = YearMonth.of(2025, 4);
        usageRepository.recordUsage(1, new UsageRecord(JIRA, april2025, 14));
        usageRepository.recordUsage(1, new UsageRecord(CONFLUENCE, april2025, 30));
        usageRepository.recordUsage(1, new UsageRecord(BITBUCKET, april2025, 7));
        BillingService aprilBillingService = new BillingService(catalog, usageRepository, new NoTrialPolicy(),
                new NoDiscountPolicy());
        Bill aprilBill = aprilBillingService.generateMonthlyBill(1, april2025);
        for (BillLineItem lineItem : aprilBill.getLineItems()) {
            System.out.println("  " + lineItem.getProductName() + ": $" + lineItem.getAmount());
        }
        System.out.println("  Total: $" + aprilBill.getTotal());

        System.out.println("\nSame JIRA usage (20 days): Feb 2024 (leap, 29 days) vs Feb 2025 (28 days):");
        UsageRepository febRepository = new UsageRepository();
        febRepository.recordUsage(1, new UsageRecord(JIRA, YearMonth.of(2024, 2), 20));
        febRepository.recordUsage(1, new UsageRecord(JIRA, YearMonth.of(2025, 2), 20));
        BillingService febBillingService = new BillingService(catalog, febRepository, new NoTrialPolicy(),
                new NoDiscountPolicy());
        System.out
                .println("  Feb 2024: $" + febBillingService.generateMonthlyBill(1, YearMonth.of(2024, 2)).getTotal());
        System.out
                .println("  Feb 2025: $" + febBillingService.generateMonthlyBill(1, YearMonth.of(2025, 2)).getTotal());

        System.out.println("\nFull year (2025), JIRA used 10 days every month - the actual stated requirement:");
        UsageRepository yearRepository = new UsageRepository();
        for (int month = 1; month <= 12; month++) {
            yearRepository.recordUsage(1, new UsageRecord(JIRA, YearMonth.of(2025, month), 10));
        }
        BillingService yearBillingService = new BillingService(catalog, yearRepository, new NoTrialPolicy(),
                new NoDiscountPolicy());
        List<Bill> monthlyBills = yearBillingService.monthlyBills(1, 2025);
        for (Bill bill : monthlyBills) {
            System.out.println("  " + bill.getPeriod() + ": $" + bill.getTotal());
        }
        System.out.println("  Annual cost: $" + yearBillingService.annualCost(1, 2025));
    }
}
