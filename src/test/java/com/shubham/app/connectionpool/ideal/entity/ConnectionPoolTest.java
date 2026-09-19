package com.shubham.app.connectionpool.ideal.entity;

import org.junit.jupiter.api.Test;

import com.shubham.app.connectionpool.ideal.exception.InvalidRequestIdException;
import com.shubham.app.connectionpool.ideal.service.FirstIdleConnectionAssigningStrategy;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConnectionPoolTest {

    private ConnectionPool newPool(int size) {
        List<Connection> connections = new java.util.ArrayList<>();
        for (int i = 1; i <= size; i++) {
            connections.add(new Connection(i));
        }
        return new ConnectionPool(connections, new FirstIdleConnectionAssigningStrategy());
    }

    @Test
    void acquiringReturnsAConnectionIdWhenOneIsFree() {
        ConnectionPool pool = newPool(1);

        Optional<Integer> connectionId = pool.acquireConnection(1);

        assertEquals(1, connectionId.orElseThrow());
    }

    @Test
    void acquiringWhenThePoolIsFullQueuesTheRequest() {
        ConnectionPool pool = newPool(1);
        pool.acquireConnection(1);

        Optional<Integer> connectionId = pool.acquireConnection(2);

        assertTrue(connectionId.isEmpty());
    }

    @Test
    void releasingHandsTheConnectionToTheNextWaitingRequest() {
        ConnectionPool pool = newPool(1);
        pool.acquireConnection(1);
        pool.acquireConnection(2);

        pool.releaseConnection(1);

        assertThrows(InvalidRequestIdException.class, () -> pool.releaseConnection(1));
        assertDoesNotThrow(() -> pool.releaseConnection(2));
    }

    // Regression test for practice/session-06-connection-pool.md's bug: releasing a
    // connection
    // when nobody is waiting must not throw — that's the ordinary case, not a rare
    // edge case.
    @Test
    void releasingWithNoWaitingRequestsDoesNotThrow() {
        ConnectionPool pool = newPool(2);
        pool.acquireConnection(1);
        pool.acquireConnection(2);

        assertDoesNotThrow(() -> pool.releaseConnection(1));
    }

    @Test
    void releasingAnUnknownRequestIdThrows() {
        ConnectionPool pool = newPool(1);

        assertThrows(InvalidRequestIdException.class, () -> pool.releaseConnection(999));
    }

    @Test
    void expiringAQueuedRequestSkipsItWhenAConnectionIsLaterReleased() {
        ConnectionPool pool = newPool(1);
        pool.acquireConnection(1);
        pool.acquireConnection(2);

        pool.expireRequest(2);
        pool.releaseConnection(1);

        // Request 2 was expired, so the connection freed by releaseConnection(1) above
        // must
        // still be sitting idle for the next fresh acquirer, not already handed to
        // request 2.
        Optional<Integer> connectionId = pool.acquireConnection(3);
        assertEquals(1, connectionId.orElseThrow());
    }

    @Test
    void expiringARequestThatWasNeverQueuedThrows() {
        ConnectionPool pool = newPool(1);

        assertThrows(InvalidRequestIdException.class, () -> pool.expireRequest(999));
    }

    @Test
    void expiringARequestThatAlreadyHoldsAConnectionThrows() {
        ConnectionPool pool = newPool(1);
        pool.acquireConnection(1);

        assertThrows(InvalidRequestIdException.class, () -> pool.expireRequest(1));
    }

    @Test
    void expiringTheSameRequestTwiceThrowsOnTheSecondCall() {
        ConnectionPool pool = newPool(1);
        pool.acquireConnection(1);
        pool.acquireConnection(2);

        pool.expireRequest(2);

        assertThrows(InvalidRequestIdException.class, () -> pool.expireRequest(2));
    }

    @Test
    void concurrentAcquiresNeverDoubleAssignTheSameConnection() throws InterruptedException {
        int poolSize = 20;
        ConnectionPool pool = newPool(poolSize);

        ExecutorService executor = Executors.newFixedThreadPool(poolSize);
        AtomicInteger acquiredCount = new AtomicInteger(0);
        AtomicInteger queuedCount = new AtomicInteger(0);

        for (int i = 0; i < poolSize * 2; i++) {
            int requestId = i;
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    Optional<Integer> connectionId = pool.acquireConnection(requestId);
                    if (connectionId.isPresent()) {
                        acquiredCount.incrementAndGet();
                    } else {
                        queuedCount.incrementAndGet();
                    }
                }
            });
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));

        assertEquals(poolSize, acquiredCount.get());
        assertEquals(poolSize, queuedCount.get());
    }
}
