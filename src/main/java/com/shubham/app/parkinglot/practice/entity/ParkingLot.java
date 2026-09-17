package com.shubham.app.parkinglot.practice.entity;

import java.util.List;

public class ParkingLot {

    private List<Spot> spots;

    public ParkingLot(List<Spot> spots) {
        this.spots = spots;
    }

    public List<Spot> getSpots() {
        return spots;
    }

    public void setSpots(List<Spot> spots) {
        this.spots = spots;
    }

    @Override
    public String toString() {
        return "ParkingLot{" + "spots=" + spots + '}';
    }
}
