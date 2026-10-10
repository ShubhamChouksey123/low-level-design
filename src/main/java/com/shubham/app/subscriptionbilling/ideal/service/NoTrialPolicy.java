package com.shubham.app.subscriptionbilling.ideal.service;

import com.shubham.app.subscriptionbilling.ideal.entity.Product;
import com.shubham.app.subscriptionbilling.ideal.entity.TrialPolicy;

public class NoTrialPolicy implements TrialPolicy {

    @Override
    public int billableDays(Product product, int rawDaysUsed) {
        return rawDaysUsed;
    }
}
