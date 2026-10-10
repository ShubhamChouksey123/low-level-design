package com.shubham.app.subscriptionbilling.ideal.entity;

/**
 * The Strategy for "how would you handle trial periods?" - decides how many of
 * a product's used days in a given month are actually billable, before
 * proration runs.
 */
public interface TrialPolicy {

    int billableDays(Product product, int rawDaysUsed);
}
