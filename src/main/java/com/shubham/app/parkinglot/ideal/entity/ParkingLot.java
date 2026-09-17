package com.shubham.app.parkinglot.ideal.entity;

import com.shubham.app.parkinglot.ideal.exception.ParkingFullException;

import java.util.List;
import java.util.Optional;

public class ParkingLot {

    private final List<Spot> spots;
    private final SpotAllocationStrategy allocationStrategy;

    public ParkingLot(List<Spot> spots, SpotAllocationStrategy allocationStrategy) {
        this.spots = List.copyOf(spots);
        this.allocationStrategy = allocationStrategy;
    }

    // Finding a vacant spot and claiming it must happen as one atomic step —
    // otherwise two
    // threads can both pick the same vacant spot before either flips it to
    // OCCUPIED. Locking
    // Spot.occupy() alone would not fix this: the race is across the whole
    // find-then-claim
    // sequence, not inside a single spot.
    public synchronized Spot claimVacantSpot() {
        Optional<Spot> chosen = allocationStrategy.chooseSpot(spots);
        Spot spot = chosen.orElseThrow(() -> new ParkingFullException("parking is full, come again later"));
        spot.occupy();
        return spot;
    }

    public synchronized void releaseSpot(Spot spot) {
        spot.vacate();
    }

    public List<Spot> getSpots() {
        return spots;
    }
}
