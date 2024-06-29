package com.shubham.app.tictactoe.entity;

public enum Symbol {
    X(0), O(1);

    private final int value;

    Symbol(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
