package com.shubham.app.connectionpool.practice.service;

import com.shubham.app.connectionpool.practice.entity.ConnectionAssigningStrategy;
import com.shubham.app.connectionpool.practice.entity.ConnectionPool;

public class ConnectionPoolCreationService {

    public ConnectionPoolCreationService() {
    }

    public ConnectionPool createPool(int poolSize, ConnectionAssigningStrategy connectionAssigningStrategy) {
        return new ConnectionPool(poolSize, connectionAssigningStrategy);
    }
}
