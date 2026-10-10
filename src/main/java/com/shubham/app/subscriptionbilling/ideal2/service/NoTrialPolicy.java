package com.shubham.app.subscriptionbilling.ideal2.service;

import com.shubham.app.subscriptionbilling.ideal2.entity.Product;
import com.shubham.app.subscriptionbilling.ideal2.entity.TrialPolicy;

public class NoTrialPolicy implements TrialPolicy {

    @Override
    public int billableDays(Product product, int rawDaysUsed) {
        return rawDaysUsed;
    }
}
