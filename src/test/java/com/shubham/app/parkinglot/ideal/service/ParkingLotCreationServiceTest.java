package com.shubham.app.parkinglot.ideal.service;

import org.junit.jupiter.api.Test;

import com.shubham.app.parkinglot.ideal.entity.ParkingLot;
import com.shubham.app.parkinglot.ideal.exception.InvalidSpotException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ParkingLotCreationServiceTest {

    @Test
    void createsALotWithTheRequestedNumberOfSpots() {
        ParkingLot lot = new ParkingLotCreationService().createParkingLot(5,
                new FirstAvailableSpotAllocationStrategy());

        assertEquals(5, lot.getSpots().size());
    }

    @Test
    void rejectsANonPositiveSize() {
        ParkingLotCreationService creationService = new ParkingLotCreationService();

        assertThrows(InvalidSpotException.class,
                () -> creationService.createParkingLot(0, new FirstAvailableSpotAllocationStrategy()));
    }
}
