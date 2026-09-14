package com.shubham.app.elevatorsystem.entity;

import org.junit.jupiter.api.Test;

import com.shubham.app.elevatorsystem.exception.InvalidFloorException;
import com.shubham.app.elevatorsystem.service.IdleState;
import com.shubham.app.elevatorsystem.service.MovingUpState;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ElevatorTest {

    @Test
    void idleElevatorTransitionsToMovingUpAndBackToIdle() {
        Elevator elevator = new Elevator(1, 0, 10, 0, new IdleState());

        elevator.addRequest(5);
        assertInstanceOf(MovingUpState.class, elevator.getState());
        assertTrue(elevator.hasUpStops());

        for (int i = 0; i < 5; i++) {
            elevator.step();
        }

        assertEquals(5, elevator.getCurrentFloor());
        assertInstanceOf(IdleState.class, elevator.getState());
        assertFalse(elevator.hasUpStops());
        assertTrue(elevator.getServedFloors().contains(5));
    }

    @Test
    void requestInPathWhileMovingIsServedWithoutMissingTheOriginalTarget() {
        Elevator elevator = new Elevator(1, 0, 10, 0, new IdleState());

        elevator.addRequest(5);
        elevator.step(); // now at floor 1, still heading to 5
        elevator.addRequest(3); // in-path request, ahead of current floor

        for (int i = 0; i < 4; i++) {
            elevator.step();
        }

        assertEquals(5, elevator.getCurrentFloor());
        assertTrue(elevator.getServedFloors().containsAll(java.util.List.of(3, 5)));
    }

    @Test
    void requestOutsideBuildingRangeThrows() {
        Elevator elevator = new Elevator(1, 0, 10, 0, new IdleState());

        assertThrows(InvalidFloorException.class, () -> elevator.addRequest(11));
        assertThrows(InvalidFloorException.class, () -> elevator.addRequest(-1));
    }

    @Test
    void requestForCurrentFloorIsANoOp() {
        Elevator elevator = new Elevator(1, 0, 10, 4, new IdleState());

        elevator.addRequest(4);

        assertInstanceOf(IdleState.class, elevator.getState());
        assertFalse(elevator.hasUpStops());
        assertFalse(elevator.hasDownStops());
    }
}
