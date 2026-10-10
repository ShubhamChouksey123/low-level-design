package com.shubham.app.subscriptionbilling.practice;

import com.shubham.app.subscriptionbilling.practice.service.BillingService;
import com.shubham.app.subscriptionbilling.practice.service.ProductService;
import com.shubham.app.subscriptionbilling.practice.service.UsageService;

public class Main {

    public static void main(String[] args) {

        ProductService productService = new ProductService();
        productService.addProduct("JIRA", 10, 30);
        productService.addProduct("CONFLUENCE", 7.5, 30);
        productService.addProduct("BITBUCKET", 8, 30);

        UsageService usageService = new UsageService();
        usageService.addUsage(0, 14, 1, 1);
        usageService.addUsage(1, 30, 1, 1);
        usageService.addUsage(2, 7, 1, 1);

        BillingService billingService = new BillingService(usageService, productService);

        double amount = billingService.getMonthlyCost(1, 1);
        System.out.println("user : 1 , has a bill of : " + amount);
    }
}
