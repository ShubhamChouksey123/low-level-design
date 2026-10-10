package com.shubham.app.subscriptionbilling.practice.service;

import com.shubham.app.subscriptionbilling.practice.entity.ProductUsage;
import com.shubham.app.subscriptionbilling.practice.exception.InvalidUsageException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class UsageService {

    List<ProductUsage> productUsages;
    private AtomicInteger usageId;

    public UsageService() {
        this.productUsages = new ArrayList<>();
        this.usageId = new AtomicInteger(0);
    }

    public void addUsage(int productId, int usedInDays, int userId, int month) {

        if (usedInDays < 0) {
            throw new InvalidUsageException("invalid number of days");
        }

        ProductUsage usage = new ProductUsage(usageId.getAndIncrement(), productId, usedInDays, userId, month);
        productUsages.add(usage);
    }

    public List<ProductUsage> getMonthlyUsage(int userId, int month) {

        List<ProductUsage> usages = new ArrayList<>();

        for (ProductUsage productUsage : productUsages) {
            if (productUsage.getUserId() == userId && productUsage.getMonth() == month) {
                usages.add(productUsage);
            }
        }

        return usages;
    }
}
