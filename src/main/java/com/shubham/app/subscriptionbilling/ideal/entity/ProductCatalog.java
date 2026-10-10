package com.shubham.app.subscriptionbilling.ideal.entity;

import com.shubham.app.subscriptionbilling.ideal.exception.InvalidProductException;

import java.util.HashMap;
import java.util.Map;

/**
 * Products indexed by id for an O(1) lookup - the practice code's
 * {@code ProductService.getProduct} did a linear scan over every registered
 * product on every single call.
 */
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
