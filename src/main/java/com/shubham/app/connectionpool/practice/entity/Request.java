package com.shubham.app.connectionpool.practice.entity;

public class Request {

    private int id;

    public Request(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Request{" + "id=" + id + '}';
    }
}
