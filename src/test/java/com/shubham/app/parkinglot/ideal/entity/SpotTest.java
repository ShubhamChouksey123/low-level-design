package com.shubham.app.parkinglot.ideal.entity;

import org.junit.jupiter.api.Test;

import com.shubham.app.parkinglot.ideal.exception.InvalidSpotException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpotTest {

    @Test
    void newSpotStartsVacant() {
        Spot spot = new Spot(0);

        assertTrue(spot.isVacant());
    }

    @Test
    void negativeIdIsRejectedAtConstruction() {
        assertThrows(InvalidSpotException.class, () -> new Spot(-1));
    }
}
