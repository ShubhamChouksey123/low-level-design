package com.shubham.app.parkinglot.ideal.entity;

import com.shubham.app.parkinglot.ideal.exception.InvalidSpotException;

public class Spot {

    private final int id;
    private SpotStatus status;

    public Spot(int id) {
        if (id < 0) {
            throw new InvalidSpotException("spot id must not be negative: " + id);
        }
        this.id = id;
        this.status = SpotStatus.VACANT;
    }

    public int getId() {
        return id;
    }

    public boolean isVacant() {
        return status == SpotStatus.VACANT;
    }

    // package-private: only ParkingLot drives a spot's status, and only as part of
    // an
    // already-synchronized claim/release so no caller can occupy() a spot without
    // going
    // through the atomic find-and-claim in ParkingLot.
    void occupy() {
        if (status == SpotStatus.OCCUPIED) {
            throw new InvalidSpotException("spot " + id + " is already occupied");
        }
        status = SpotStatus.OCCUPIED;
    }

    void vacate() {
        status = SpotStatus.VACANT;
    }

    @Override
    public String toString() {
        return "Spot{id=" + id + ", status=" + status + '}';
    }
}
