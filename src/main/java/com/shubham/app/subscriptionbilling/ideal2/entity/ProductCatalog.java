package com.shubham.app.subscriptionbilling.ideal2.entity;

import com.shubham.app.subscriptionbilling.ideal2.exception.InvalidProductException;

import java.util.HashMap;
import java.util.Map;

public class ProductCatalog {

    private final Map<Integer, Product> productsById = new HashMap<>();

    public void register(Product product) {
        productsById.put(product.getId(), product);
    }

    public Product getProduct(int productId) {
        Product product = productsById.get(productId);
        if (product == null) {
            throw new InvalidProductException("no such product: " + productId);
        }
        return product;
    }
}
