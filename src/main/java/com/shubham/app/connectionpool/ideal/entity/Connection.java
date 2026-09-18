package com.shubham.app.connectionpool.ideal.entity;

public class Connection {

    private final int id;
    private State state;

    public Connection(int id) {
        this.id = id;
        this.state = State.IDLE;
    }

    public int getId() {
        return id;
    }

    public boolean isIdle() {
        return state == State.IDLE;
    }

    // package-private: only ConnectionPool drives a connection's state, and only as
    // part of an
    // already-synchronized acquire/release, so no caller can occupy() a connection
    // outside the
    // pool's own atomic find-and-claim.
    void occupy() {
        state = State.OCCUPIED;
    }

    void free() {
        state = State.IDLE;
    }

    @Override
    public String toString() {
        return "Connection{id=" + id + ", state=" + state + '}';
    }
}
