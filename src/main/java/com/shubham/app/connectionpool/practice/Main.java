package com.shubham.app.connectionpool.practice;

import com.shubham.app.connectionpool.practice.entity.ConnectionPool;
import com.shubham.app.connectionpool.practice.service.ConnectionPoolCreationService;
import com.shubham.app.connectionpool.practice.service.FirstEmptyConnectionAssigningStrategy;

public class Main {

    public static void main(String[] args) {
        ConnectionPoolCreationService connectionPoolCreationService = new ConnectionPoolCreationService();
        ConnectionPool pool = connectionPoolCreationService.createPool(3, new FirstEmptyConnectionAssigningStrategy());

        pool.acquireConnection(1);
        pool.acquireConnection(2);
        pool.print();

        pool.acquireConnection(3);
        pool.print();

        pool.acquireConnection(4);

        pool.releaseConnection(2);
        pool.print();
    }
}
