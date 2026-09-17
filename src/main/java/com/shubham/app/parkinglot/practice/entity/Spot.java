package com.shubham.app.parkinglot.practice.entity;

import com.shubham.app.parkinglot.practice.exception.InvalidSpotException;

public class Spot {

    private int id;
    private SpotStatus status;

    public Spot(int id) {
        this.id = id;
    }

    public Spot(int id, SpotStatus status) {
        this.id = id;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id < 0) {
            throw new InvalidSpotException("id should not negative");
        }
        this.id = id;
    }

    public SpotStatus getStatus() {
        return status;
    }

    public synchronized void setStatus(SpotStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Spot{" + "id=" + id + ", status=" + status + '}';
    }
}
