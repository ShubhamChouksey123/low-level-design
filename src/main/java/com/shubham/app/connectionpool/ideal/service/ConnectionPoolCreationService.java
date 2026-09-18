package com.shubham.app.connectionpool.ideal.service;

import com.shubham.app.connectionpool.ideal.entity.Connection;
import com.shubham.app.connectionpool.ideal.entity.ConnectionAssigningStrategy;
import com.shubham.app.connectionpool.ideal.entity.ConnectionPool;
import com.shubham.app.connectionpool.ideal.exception.InvalidPoolSizeException;

import java.util.ArrayList;
import java.util.List;

public class ConnectionPoolCreationService {

    public ConnectionPool createPool(int poolSize, ConnectionAssigningStrategy assigningStrategy) {
        if (poolSize <= 0) {
            throw new InvalidPoolSizeException("a connection pool must have at least one connection");
        }

        List<Connection> connections = new ArrayList<>();
        for (int i = 1; i <= poolSize; i++) {
            connections.add(new Connection(i));
        }

        return new ConnectionPool(connections, assigningStrategy);
    }
}
