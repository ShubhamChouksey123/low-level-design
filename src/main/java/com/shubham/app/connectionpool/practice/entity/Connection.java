package com.shubham.app.connectionpool.practice.entity;

public class Connection {
    private int id;
    private State state;

    public Connection(int id) {
        this.id = id;
        this.state = State.IDLE;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public State getState() {
        return state;
    }

    public void occupy() {
        this.state = State.OCCUPIED;
    }

    public void free() {
        this.state = State.IDLE;
    }

    @Override
    public String toString() {
        return "Connection{" + "id=" + id + ", state=" + state + '}';
    }
}
