package com.shubham.app.connectionpool.ideal.service;

import org.junit.jupiter.api.Test;

import com.shubham.app.connectionpool.ideal.entity.ConnectionPool;
import com.shubham.app.connectionpool.ideal.exception.InvalidPoolSizeException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConnectionPoolCreationServiceTest {

    @Test
    void createsAPoolWithExactlyTheRequestedNumberOfConnections() {
        ConnectionPool pool = new ConnectionPoolCreationService().createPool(2,
                new FirstIdleConnectionAssigningStrategy());

        Optional<Integer> first = pool.acquireConnection(1);
        Optional<Integer> second = pool.acquireConnection(2);
        Optional<Integer> third = pool.acquireConnection(3);

        assertEquals(1, first.orElseThrow());
        assertEquals(2, second.orElseThrow());
        assertEquals(Optional.empty(), third);
    }

    @Test
    void rejectsANonPositivePoolSize() {
        ConnectionPoolCreationService creationService = new ConnectionPoolCreationService();

        assertThrows(InvalidPoolSizeException.class,
                () -> creationService.createPool(0, new FirstIdleConnectionAssigningStrategy()));
    }
}
