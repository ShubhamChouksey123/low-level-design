package com.shubham.app.stocktrading.ideal.entity;

import org.junit.jupiter.api.Test;

import com.shubham.app.stocktrading.ideal.exception.InvalidOrderException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderTest {

    @Test
    void priceMustBePositive() {
        assertThrows(InvalidOrderException.class, () -> new Order(0, 1, 1, Side.BUY, 0, 10));
        assertThrows(InvalidOrderException.class, () -> new Order(0, 1, 1, Side.BUY, -5, 10));
    }

    @Test
    void quantityMustBePositive() {
        assertThrows(InvalidOrderException.class, () -> new Order(0, 1, 1, Side.BUY, 100, 0));
        assertThrows(InvalidOrderException.class, () -> new Order(0, 1, 1, Side.BUY, 100, -1));
    }

    @Test
    void fillingReducesRemainingQuantity() {
        Order order = new Order(0, 1, 1, Side.BUY, 100, 10);

        order.fill(4);

        assertEquals(6, order.getRemainingQuantity());
        assertFalse(order.isFullyFilled());
    }

    @Test
    void fillingTheExactRemainingQuantityMarksItFullyFilled() {
        Order order = new Order(0, 1, 1, Side.BUY, 100, 10);

        order.fill(10);

        assertEquals(0, order.getRemainingQuantity());
        assertTrue(order.isFullyFilled());
    }

    @Test
    void fillingMoreThanWhatRemainsThrows() {
        Order order = new Order(0, 1, 1, Side.BUY, 100, 10);

        assertThrows(InvalidOrderException.class, () -> order.fill(11));
    }
}
