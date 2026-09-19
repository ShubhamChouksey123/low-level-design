package com.shubham.app.connectionpool.ideal.entity;

import com.shubham.app.connectionpool.ideal.exception.InvalidRequestIdException;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ConnectionPool {

    private final List<Connection> connections;
    private final ConnectionAssigningStrategy assigningStrategy;
    private final Map<Integer, Connection> requestIdToConnection = new HashMap<>();
    private final Deque<Integer> waitingRequestIds = new ArrayDeque<>();

    public ConnectionPool(List<Connection> connections, ConnectionAssigningStrategy assigningStrategy) {
        this.connections = List.copyOf(connections);
        this.assigningStrategy = assigningStrategy;
    }

    // Finding an idle connection and claiming it must happen as one atomic step,
    // the same
    // lesson as ParkingLot.claimVacantSpot: otherwise two requests could both be
    // handed the
    // same connection before either occupies it.
    public synchronized Optional<Integer> acquireConnection(int requestId) {
        Optional<Connection> freeConnection = assigningStrategy.chooseConnection(connections);
        if (freeConnection.isEmpty()) {
            waitingRequestIds.addLast(requestId);
            return Optional.empty();
        }

        Connection connection = freeConnection.get();
        connection.occupy();
        requestIdToConnection.put(requestId, connection);
        return Optional.of(connection.getId());
    }

    public synchronized void releaseConnection(int requestId) {
        Connection connection = requestIdToConnection.remove(requestId);
        if (connection == null) {
            throw new InvalidRequestIdException("no connection is held by request: " + requestId);
        }

        connection.free();

        // The fix: only hand the freed connection to a waiting request if one actually
        // exists. The practice attempt polled the queue unconditionally and threw a
        // NullPointerException whenever nobody was waiting — which is the ordinary case
        // for a
        // release, not a rare edge case.
        if (!waitingRequestIds.isEmpty()) {
            int nextRequestId = waitingRequestIds.removeFirst();
            acquireConnection(nextRequestId);
        }
    }

    // Only a request that is actually sitting in the wait queue can be expired — a
    // request
    // that already holds a connection isn't queued, so it isn't this method's
    // concern.
    public synchronized void expireRequest(int requestId) {
        boolean removed = waitingRequestIds.remove(Integer.valueOf(requestId));
        if (!removed) {
            throw new InvalidRequestIdException("no queued request: " + requestId);
        }
    }
}
