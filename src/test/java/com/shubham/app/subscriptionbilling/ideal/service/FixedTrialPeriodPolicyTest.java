package com.shubham.app.subscriptionbilling.ideal.service;

import org.junit.jupiter.api.Test;

import com.shubham.app.subscriptionbilling.ideal.entity.Product;
import com.shubham.app.subscriptionbilling.ideal.entity.TrialPolicy;
import com.shubham.app.subscriptionbilling.ideal.exception.InvalidBillingConfigException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FixedTrialPeriodPolicyTest {

    private final Product product = new Product(1, "JIRA", BigDecimal.TEN);

    @Test
    void constructorRejectsANegativeTrialLength() {
        assertThrows(InvalidBillingConfigException.class, () -> new FixedTrialPeriodPolicy(-1));
    }

    @Test
    void subtractsTheTrialDaysFromRawUsage() {
        TrialPolicy policy = new FixedTrialPeriodPolicy(5);
        assertEquals(9, policy.billableDays(product, 14));
    }

    @Test
    void neverGoesNegativeWhenRawUsageIsBelowTheTrialLength() {
        TrialPolicy policy = new FixedTrialPeriodPolicy(10);
        assertEquals(0, policy.billableDays(product, 3));
    }
}
