package com.shubham.app.parkinglot.ideal.entity;

import java.util.List;
import java.util.Optional;

public interface SpotAllocationStrategy {

    Optional<Spot> chooseSpot(List<Spot> spots);
}
