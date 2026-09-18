package com.shubham.app.connectionpool.practice.entity;

import java.util.Map;
import java.util.Optional;

public interface ConnectionAssigningStrategy {
    Optional<Connection> getFreeConnection(Map<Integer, Connection> connectionIdToConnection);
}
