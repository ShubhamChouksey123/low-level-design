package com.shubham.app.subscriptionbilling.ideal.entity;

import org.junit.jupiter.api.Test;

import com.shubham.app.subscriptionbilling.ideal.exception.InvalidProductException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProductCatalogTest {

    @Test
    void getProductReturnsARegisteredProduct() {
        ProductCatalog catalog = new ProductCatalog();
        Product jira = new Product(1, "JIRA", BigDecimal.TEN);
        catalog.register(jira);

        assertEquals(jira, catalog.getProduct(1));
    }

    @Test
    void getProductThrowsForAnUnregisteredId() {
        ProductCatalog catalog = new ProductCatalog();
        assertThrows(InvalidProductException.class, () -> catalog.getProduct(99));
    }
}
