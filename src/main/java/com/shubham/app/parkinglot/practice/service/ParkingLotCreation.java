package com.shubham.app.parkinglot.practice.service;

import com.shubham.app.parkinglot.practice.entity.ParkingLot;
import com.shubham.app.parkinglot.practice.entity.Spot;
import com.shubham.app.parkinglot.practice.entity.SpotStatus;

import java.util.ArrayList;
import java.util.List;

public class ParkingLotCreation {

    public ParkingLotCreation() {
    }

    public ParkingLot createParking(int size) {

        if (size < 0) {
            throw new IllegalArgumentException("invalid size");
        }

        List<Spot> spots = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            spots.add(new Spot(i, SpotStatus.VACANT));
        }

        ParkingLot parkingLot = new ParkingLot(spots);
        return parkingLot;
    }
}
