package com.shubham.app.connectionpool.ideal.entity;

import java.util.List;
import java.util.Optional;

public interface ConnectionAssigningStrategy {

    Optional<Connection> chooseConnection(List<Connection> connections);
}
