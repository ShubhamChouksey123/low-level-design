package com.shubham.app.subscriptionbilling.ideal.entity;

import org.junit.jupiter.api.Test;

import com.shubham.app.subscriptionbilling.ideal.exception.InvalidProductException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ProductTest {

    @Test
    void constructorRejectsABlankName() {
        assertThrows(InvalidProductException.class, () -> new Product(1, "", BigDecimal.TEN));
        assertThrows(InvalidProductException.class, () -> new Product(1, null, BigDecimal.TEN));
    }

    @Test
    void constructorRejectsANonPositivePrice() {
        assertThrows(InvalidProductException.class, () -> new Product(1, "JIRA", BigDecimal.ZERO));
        assertThrows(InvalidProductException.class, () -> new Product(1, "JIRA", new BigDecimal("-5")));
        assertThrows(InvalidProductException.class, () -> new Product(1, "JIRA", null));
    }
}
