package com.shubham.app.stocktrading.practice.Exception;

public class InvalidStockQuantity extends RuntimeException {
    public InvalidStockQuantity(String message) {
        super(message);
    }
}
