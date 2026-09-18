package com.shubham.app.connectionpool.ideal.service;

import org.junit.jupiter.api.Test;

import com.shubham.app.connectionpool.ideal.entity.Connection;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FirstIdleConnectionAssigningStrategyTest {

    private final FirstIdleConnectionAssigningStrategy strategy = new FirstIdleConnectionAssigningStrategy();

    @Test
    void choosesTheFirstIdleConnection() {
        Connection first = new Connection(1);
        Connection second = new Connection(2);

        Optional<Connection> chosen = strategy.chooseConnection(List.of(first, second));

        assertEquals(1, chosen.orElseThrow().getId());
    }

    @Test
    void returnsEmptyWhenThereAreNoConnectionsToChooseFrom() {
        Optional<Connection> chosen = strategy.chooseConnection(List.of());

        assertTrue(chosen.isEmpty());
    }
}
