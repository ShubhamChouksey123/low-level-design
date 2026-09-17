package com.shubham.app.parkinglot.ideal.service;

import com.shubham.app.parkinglot.ideal.entity.Spot;
import com.shubham.app.parkinglot.ideal.entity.SpotAllocationStrategy;

import java.util.List;
import java.util.Optional;

public class FirstAvailableSpotAllocationStrategy implements SpotAllocationStrategy {

    @Override
    public Optional<Spot> chooseSpot(List<Spot> spots) {
        return spots.stream().filter(Spot::isVacant).findFirst();
    }
}
