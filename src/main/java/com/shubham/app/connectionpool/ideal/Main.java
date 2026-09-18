package com.shubham.app.connectionpool.ideal;

import com.shubham.app.connectionpool.ideal.entity.ConnectionPool;
import com.shubham.app.connectionpool.ideal.exception.InvalidRequestIdException;
import com.shubham.app.connectionpool.ideal.service.ConnectionPoolCreationService;
import com.shubham.app.connectionpool.ideal.service.FirstIdleConnectionAssigningStrategy;

import java.util.Optional;

public class Main {

    public static void main(String[] args) {
        ConnectionPool pool = new ConnectionPoolCreationService().createPool(2,
                new FirstIdleConnectionAssigningStrategy());

        System.out.println("acquire(1): " + pool.acquireConnection(1));
        System.out.println("acquire(2): " + pool.acquireConnection(2));

        // Pool is full — this request queues instead of getting a connection.
        Optional<Integer> queued = pool.acquireConnection(3);
        System.out.println("acquire(3), pool full: " + queued);

        // Releasing connection 1 immediately hands it to the queued request (3).
        pool.releaseConnection(1);
        System.out.println("released 1 — request 3 should now hold a connection");

        // This is the practice attempt's bug, fixed: releasing when nobody is waiting
        // must
        // NOT throw. Both connections are held (by 2 and 3), so releasing 2 leaves the
        // queue
        // empty here — request 3 already took the freed slot from connection 1 above.
        pool.releaseConnection(2);
        System.out.println("released 2 with an empty queue — no crash");

        try {
            pool.releaseConnection(99);
        } catch (InvalidRequestIdException e) {
            System.out.println("Rejected: " + e.getMessage());
        }
    }
}
