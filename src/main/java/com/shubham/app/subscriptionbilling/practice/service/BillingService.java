package com.shubham.app.subscriptionbilling.practice.service;

import com.shubham.app.subscriptionbilling.practice.entity.Product;
import com.shubham.app.subscriptionbilling.practice.entity.ProductUsage;

import java.util.List;

public class BillingService {

    private UsageService usageService;
    private ProductService productService;

    public BillingService(UsageService usageService, ProductService productService) {
        this.usageService = usageService;
        this.productService = productService;
    }

    public double getMonthlyCost(int userId, int month) {

        List<ProductUsage> usages = usageService.getMonthlyUsage(userId, month);
        double amount = 0.0;

        for (ProductUsage usage : usages) {
            Product product = productService.getProduct(usage.getProductId());
            double price = product.getPricePerDay() * usage.getUsedInDays();
            amount += price;
        }
        return amount;
    }
}
