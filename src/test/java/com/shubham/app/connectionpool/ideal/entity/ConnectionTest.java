package com.shubham.app.connectionpool.ideal.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ConnectionTest {

    @Test
    void newConnectionStartsIdle() {
        Connection connection = new Connection(1);

        assertTrue(connection.isIdle());
    }
}
