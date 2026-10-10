package com.shubham.app.subscriptionbilling.ideal2.entity;

/**
 * The extensibility seam for "how would you handle trial periods?" - decides
 * how many of a product's used days in a given month are actually billable,
 * before proration runs. In this scaled-down version only {@code NoTrialPolicy}
 * exists; a real trial rule (e.g. "first N days free") is one new class
 * implementing this interface, with zero changes to BillingService - see ideal/
 * for a worked example (FixedTrialPeriodPolicy) that this version deliberately
 * skips to stay inside a 40-minute scope.
 */
public interface TrialPolicy {

    int billableDays(Product product, int rawDaysUsed);
}
