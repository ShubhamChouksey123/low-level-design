package com.shubham.app.connectionpool.practice.entity;

import com.shubham.app.connectionpool.practice.exception.InvalidRequestIdException;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ConnectionPool {

    private Map<Integer, Connection> connectionIdToConnection;
    private Map<Integer, Connection> requestIdToAcquiredConnection;
    private ConnectionAssigningStrategy connectionAssigningStrategy;
    private Deque<Integer> requestQueue;

    public ConnectionPool(int poolSize, ConnectionAssigningStrategy connectionAssigningStrategy) {

        HashMap<Integer, Connection> connectionIdToConnection = new HashMap<>();
        for (int i = 1; i <= poolSize; i++) {
            Connection connection = new Connection(i);
            connectionIdToConnection.put(i, connection);
        }
        this.connectionIdToConnection = connectionIdToConnection;
        this.requestIdToAcquiredConnection = new HashMap<>();
        this.connectionAssigningStrategy = connectionAssigningStrategy;
        this.requestQueue = new ArrayDeque<>();
    }

    /**
     * returns acquired connection id
     *
     * @param requestId
     * @return
     */
    public synchronized int acquireConnection(int requestId) {

        Optional<Connection> freeConnection = connectionAssigningStrategy.getFreeConnection(connectionIdToConnection);
        if (freeConnection.isEmpty()) {
            requestQueue.add(requestId);
            System.out.println("request with id " + requestId + " added in request queue");
            return -1;
        }

        freeConnection.get().occupy();
        requestIdToAcquiredConnection.put(requestId, freeConnection.get());
        return freeConnection.get().getId();
    }

    public synchronized void releaseConnection(int requestId) {
        Connection connection = requestIdToAcquiredConnection.get(requestId);
        if (connection == null) {
            throw new InvalidRequestIdException("invalid request-id");
        }

        connection.free();
        requestIdToAcquiredConnection.remove(requestId);

        if(!requestQueue.isEmpty()){
            int waitingRequestId = requestQueue.pollFirst();
            acquireConnection(waitingRequestId);
        }
    }

    public void print() {

        for (Connection connection : connectionIdToConnection.values()) {
            System.out.println(connection + ", and occupied by ");
        }
        System.out.println(requestIdToAcquiredConnection);
        System.out.println("");
    }
}
