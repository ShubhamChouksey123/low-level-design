package com.shubham.app.parkinglot.ideal.service;

import com.shubham.app.parkinglot.ideal.entity.ParkingLot;
import com.shubham.app.parkinglot.ideal.entity.Spot;
import com.shubham.app.parkinglot.ideal.entity.SpotAllocationStrategy;
import com.shubham.app.parkinglot.ideal.exception.InvalidSpotException;

import java.util.ArrayList;
import java.util.List;

public class ParkingLotCreationService {

    public ParkingLot createParkingLot(int numberOfSpots, SpotAllocationStrategy allocationStrategy) {
        if (numberOfSpots <= 0) {
            throw new InvalidSpotException("a parking lot must have at least one spot");
        }

        List<Spot> spots = new ArrayList<>();
        for (int i = 0; i < numberOfSpots; i++) {
            spots.add(new Spot(i));
        }

        return new ParkingLot(spots, allocationStrategy);
    }
}
