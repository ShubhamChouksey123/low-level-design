package com.shubham.app.connectionpool.practice.service;

import com.shubham.app.connectionpool.practice.entity.Connection;
import com.shubham.app.connectionpool.practice.entity.ConnectionAssigningStrategy;
import com.shubham.app.connectionpool.practice.entity.State;

import java.util.Map;
import java.util.Optional;

public class FirstEmptyConnectionAssigningStrategy implements ConnectionAssigningStrategy {

    @Override
    public synchronized Optional<Connection> getFreeConnection(Map<Integer, Connection> connectionIdToConnection) {

        Optional<Connection> freeConnection = Optional.empty();
        for (Connection connection : connectionIdToConnection.values()) {
            if (connection.getState() == State.IDLE) {
                freeConnection = Optional.of(connection);
                break;
            }
        }
        return freeConnection;
    }
}
