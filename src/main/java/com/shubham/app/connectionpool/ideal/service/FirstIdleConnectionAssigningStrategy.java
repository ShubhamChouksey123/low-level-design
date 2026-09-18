package com.shubham.app.connectionpool.ideal.service;

import com.shubham.app.connectionpool.ideal.entity.Connection;
import com.shubham.app.connectionpool.ideal.entity.ConnectionAssigningStrategy;

import java.util.List;
import java.util.Optional;

public class FirstIdleConnectionAssigningStrategy implements ConnectionAssigningStrategy {

    @Override
    public Optional<Connection> chooseConnection(List<Connection> connections) {
        for (Connection connection : connections) {
            if (connection.isIdle()) {
                return Optional.of(connection);
            }
        }
        return Optional.empty();
    }
}
