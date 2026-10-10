package com.shubham.app.subscriptionbilling.practice.service;

import com.shubham.app.subscriptionbilling.practice.entity.Product;
import com.shubham.app.subscriptionbilling.practice.exception.InvalidProductId;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ProductService {

    private List<Product> products;
    private AtomicInteger productId;

    public ProductService() {
        this.products = new ArrayList<>();
        productId = new AtomicInteger(0);
    }

    public void addProduct(String name, double basePrice, int days) {
        Product product = new Product(productId.getAndIncrement(), name, basePrice, days);
        products.add(product);
    }

    public Product getProduct(int id) {
        for (Product product : products) {
            if (product.getId() == id)
                return product;
        }

        throw new InvalidProductId("invalid product");
    }
}
