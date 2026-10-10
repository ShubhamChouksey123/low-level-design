package com.shubham.app.subscriptionbilling.ideal.service;

import com.shubham.app.subscriptionbilling.ideal.entity.Product;
import com.shubham.app.subscriptionbilling.ideal.entity.TrialPolicy;
import com.shubham.app.subscriptionbilling.ideal.exception.InvalidBillingConfigException;

/**
 * The first {@code trialDays} of usage are free, every month, for every
 * product. This is one reasonable reading of the open-ended "trial periods"
 * follow-up - the problem only ever hands over day-counts per month, with no
 * subscription start date to anchor a one-time "first N days ever, then never
 * again" trial against. If that's the intended semantics instead, this class is
 * the one place that would change - nothing in {@code BillingService} would
 * need to.
 */
public class FixedTrialPeriodPolicy implements TrialPolicy {

    private final int trialDays;

    public FixedTrialPeriodPolicy(int trialDays) {
        if (trialDays < 0) {
            throw new InvalidBillingConfigException("trialDays must not be negative: " + trialDays);
        }
        this.trialDays = trialDays;
    }

    @Override
    public int billableDays(Product product, int rawDaysUsed) {
        return Math.max(0, rawDaysUsed - trialDays);
    }
}
