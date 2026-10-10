package com.shubham.app.subscriptionbilling.ideal;

import com.shubham.app.subscriptionbilling.ideal.entity.Bill;
import com.shubham.app.subscriptionbilling.ideal.entity.BillLineItem;
import com.shubham.app.subscriptionbilling.ideal.entity.BillingService;
import com.shubham.app.subscriptionbilling.ideal.entity.Product;
import com.shubham.app.subscriptionbilling.ideal.entity.ProductCatalog;
import com.shubham.app.subscriptionbilling.ideal.entity.UsageRecord;
import com.shubham.app.subscriptionbilling.ideal.entity.UsageRepository;
import com.shubham.app.subscriptionbilling.ideal.service.BundleDiscountPolicy;
import com.shubham.app.subscriptionbilling.ideal.service.FixedTrialPeriodPolicy;
import com.shubham.app.subscriptionbilling.ideal.service.NoDiscountPolicy;
import com.shubham.app.subscriptionbilling.ideal.service.NoTrialPolicy;

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

        System.out.println("Practice scenario (JIRA 14 days, CONFLUENCE 30 days, BITBUCKET 7 days), April 2025:");
        UsageRepository usageRepository = new UsageRepository();
        YearMonth april2025 = YearMonth.of(2025, 4);
        usageRepository.recordUsage(1, new UsageRecord(JIRA, april2025, 14));
        usageRepository.recordUsage(1, new UsageRecord(CONFLUENCE, april2025, 30));
        usageRepository.recordUsage(1, new UsageRecord(BITBUCKET, april2025, 7));

        BillingService billingService = new BillingService(catalog, usageRepository, new NoTrialPolicy(),
                new NoDiscountPolicy());
        Bill aprilBill = billingService.generateMonthlyBill(1, april2025);
        for (BillLineItem lineItem : aprilBill.getLineItems()) {
            System.out.println("  " + lineItem.getProductName() + ": $" + lineItem.getAmount() + " ("
                    + lineItem.getBillableDays() + " days)");
        }
        System.out.println("  Total: $" + aprilBill.getTotal());

        System.out.println("\nSame JIRA usage (20 days), Feb 2024 (leap, 29 days) vs Feb 2025 (28 days):");
        UsageRepository febRepository = new UsageRepository();
        YearMonth feb2024 = YearMonth.of(2024, 2);
        YearMonth feb2025 = YearMonth.of(2025, 2);
        febRepository.recordUsage(1, new UsageRecord(JIRA, feb2024, 20));
        febRepository.recordUsage(1, new UsageRecord(JIRA, feb2025, 20));
        BillingService febBillingService = new BillingService(catalog, febRepository, new NoTrialPolicy(),
                new NoDiscountPolicy());
        System.out.println("  Feb 2024 (29 days): $" + febBillingService.generateMonthlyBill(1, feb2024).getTotal());
        System.out.println("  Feb 2025 (28 days): $" + febBillingService.generateMonthlyBill(1, feb2025).getTotal());

        System.out.println("\nFull year (2025), JIRA used 10 days every month, no other products:");
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

        System.out.println("\nSame April scenario, with a 10%-off-for-2+-products bundle discount and a"
                + " 5-day free trial - zero changes to BillingService itself:");
        BillingService discountedBillingService = new BillingService(catalog, usageRepository,
                new FixedTrialPeriodPolicy(5), new BundleDiscountPolicy(new BigDecimal("10"), 2));
        Bill discountedBill = discountedBillingService.generateMonthlyBill(1, april2025);
        for (BillLineItem lineItem : discountedBill.getLineItems()) {
            System.out.println("  " + lineItem.getProductName() + ": $" + lineItem.getAmount() + " ("
                    + lineItem.getBillableDays() + " billable days)");
        }
        System.out.println("  Subtotal: $" + discountedBill.getSubtotal());
        System.out.println("  Discount: $" + discountedBill.getDiscount());
        System.out.println("  Total: $" + discountedBill.getTotal());
    }
}
